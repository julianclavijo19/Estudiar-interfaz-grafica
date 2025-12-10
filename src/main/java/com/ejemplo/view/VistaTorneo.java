package com.ejemplo.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

import com.ejemplo.model.Equipo;
import com.ejemplo.model.Partido;
import com.ejemplo.model.PosicionTabla;
import com.ejemplo.model.Torneo;

public class VistaTorneo extends VistaBase {

    public VistaTorneo(Torneo torneo) {
        super("🏟️ Gestión del Torneo");

        Label lbl = new Label("Nombre del torneo: " + torneo.getNombre());
        Button btnCrearGrupos = new Button("🎲 Crear Grupos y Sortear");
        Button btnVerPartidos = new Button("📅 Ver Partidos");
        Button btnVerTabla = new Button("📋 Ver Tabla de Posiciones");

        TextArea salida = new TextArea();
        salida.setEditable(false);
        salida.setPrefHeight(400);

        // 🔹 Acción del botón "Crear grupos"
        btnCrearGrupos.setOnAction(e -> {
            try {
                torneo.crearGrupos(); // genera grupos y partidos
                salida.setText(generarTextoGruposYPartidos(torneo));
                btnCrearGrupos.setDisable(true); // ✅ Evitar sortear dos veces
            } catch (IllegalStateException ex) {
                salida.setText(ex.getMessage());
            }
        });

        // 🔹 Acción del botón "Ver partidos"
        btnVerPartidos.setOnAction(e -> {
            if (torneo.getPartidos().isEmpty()) {
                salida.setText("⚠️ No hay partidos generados todavía.");
                return;
            }
            StringBuilder sb = new StringBuilder("📅 Partidos generados:\n\n");
            for (Partido p : torneo.getPartidos()) {
                sb.append("⚽ ").append(p.getEquipoLocal().getNombre())
                        .append(" vs ").append(p.getEquipoVisitante().getNombre())
                        .append("\n");
            }
            salida.setText(sb.toString());
        });

        // 🔹 Acción del botón "Ver tabla"
   btnVerTabla.setOnAction(e -> {
    Map<String, List<Equipo>> grupos = torneo.getGrupos();

    if (grupos == null || grupos.isEmpty()) {
        salida.setText("⚠️ No hay grupos generados. Por favor, realiza el sorteo primero.");
        return;
    }

    StringBuilder sb = new StringBuilder("🏆 TABLA DE POSICIONES POR GRUPO 🏆\n\n");

    // Iterar sobre cada grupo
    for (Map.Entry<String, List<Equipo>> entry : grupos.entrySet()) {
        String nombreGrupo = entry.getKey();
        List<Equipo> tablaGrupo = new java.util.ArrayList<>(entry.getValue()); 

        // 1. Aplicar el criterio de ordenamiento (Puntos > Diferencia de Goles > Goles a Favor)
        tablaGrupo.sort((e1, e2) -> {
            int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
            if (cmp == 0)
                cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                                      (e1.getGolesFavor() - e1.getGolesContra()));
            if (cmp == 0)
                cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
            return cmp;
        });

        // 2. Imprimir el encabezado del grupo
        sb.append("--- ").append(nombreGrupo).append(" ---\n");
        sb.append(String.format("%-20s | %5s | %4s | %4s | %4s\n", "EQUIPO", "PTS", "GF", "GC", "DIF"));
        sb.append("---------------------|-------|------|------|------\n");

        // 3. Imprimir los equipos ordenados
        for (Equipo eq : tablaGrupo) {
            sb.append(String.format("%-20s | %5d | %4d | %4d | %4d\n",
                    eq.getNombre(),
                    eq.getPuntos(),
                    eq.getGolesFavor(),
                    eq.getGolesContra(),
                    (eq.getGolesFavor() - eq.getGolesContra())));
        }
        sb.append("\n");
    }

    salida.setText(sb.toString());
});

        // Si el sorteo ya se hizo antes, desactiva el botón
        if (torneo.isSorteoRealizado()) {
            btnCrearGrupos.setDisable(true);
        }

        VBox box = new VBox(10, lbl, btnCrearGrupos, btnVerPartidos, btnVerTabla, salida);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        getChildren().add(box);
    }

    // 🔸 Método auxiliar para mostrar grupos y partidos generados
    private String generarTextoGruposYPartidos(Torneo torneo) {
        StringBuilder sb = new StringBuilder("🏆 Sorteo de grupos realizado:\n\n");

        for (Map.Entry<String, List<Equipo>> entry : torneo.getGrupos().entrySet()) {
            sb.append("🔹 ").append(entry.getKey()).append(":\n");
            for (Equipo e : entry.getValue()) {
                sb.append("   - ").append(e.getNombre()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("📅 Partidos generados:\n");
        for (Partido p : torneo.getPartidos()) {
            sb.append("⚽ ").append(p.getEquipoLocal().getNombre())
                    .append(" vs ").append(p.getEquipoVisitante().getNombre())
                    .append("\n");
        }

        return sb.toString();
    }
}
