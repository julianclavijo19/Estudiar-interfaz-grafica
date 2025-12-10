package com.ejemplo.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ejemplo.model.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class VistaTabla extends VistaBase {

    private final TextArea salida;

    public VistaTabla(Torneo torneo) {
        super("📊 Tablas del Torneo");

        salida = new TextArea();
        salida.setEditable(false);
        salida.setPrefHeight(400);

        Button btnPosiciones = new Button("🏆 Ver Tabla de Posiciones");
        Button btnGoleadores = new Button("⚽ Ver Tabla de Goleadores");
       // Archivo: VistaTabla.java

btnPosiciones.setOnAction(e -> {
    Map<String, List<Equipo>> grupos = torneo.getGrupos();
    
    if (grupos.isEmpty()) {
        salida.setText("⚠️ No hay grupos generados. Por favor, realiza el sorteo primero.");
        return;
    }

    StringBuilder sb = new StringBuilder("🏆 TABLA DE POSICIONES POR GRUPO 🏆\n\n");
    
    // Iterar sobre cada grupo
    for (Map.Entry<String, List<Equipo>> entry : grupos.entrySet()) {
        String nombreGrupo = entry.getKey();
        List<Equipo> equiposDelGrupo = entry.getValue();
        
        // 1. Clonar la lista para poder ordenarla sin afectar el original del Torneo
        List<Equipo> tablaGrupo = new ArrayList<>(equiposDelGrupo);
        
        // 2. Aplicar el criterio de ordenamiento (Puntos > Diferencia de Goles > Goles a Favor)
        tablaGrupo.sort((e1, e2) -> {
            int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
            if (cmp == 0)
                cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                                      (e1.getGolesFavor() - e1.getGolesContra()));
            if (cmp == 0)
                cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
            return cmp;
        });
        
        // 3. Imprimir el encabezado del grupo
        sb.append("--- ").append(nombreGrupo).append(" ---\n");
        sb.append(String.format("%-20s | %5s | %4s | %4s | %4s\n", "EQUIPO", "PTS", "GF", "GC", "DIF"));
        sb.append("---------------------|-------|------|------|------\n");

        // 4. Imprimir los equipos ordenados
        for (Equipo eq : tablaGrupo) {
            sb.append(String.format("%-20s | %5d | %4d | %4d | %4d\n",
                    eq.getNombre(), 
                    eq.getPuntos(), 
                    eq.getGolesFavor(), 
                    eq.getGolesContra(),
                    (eq.getGolesFavor() - eq.getGolesContra())));
        }
        sb.append("\n"); // Espacio entre grupos
    }

    salida.setText(sb.toString());
});

        btnGoleadores.setOnAction(e -> {
            List<Jugador> goleadores = torneo.mostrarGoleadores();

            StringBuilder sb = new StringBuilder("⚽ GOLEADORES ⚽\n\n");

            for (Jugador j : goleadores) {
                sb.append(j.getNombre())
                        .append(" (").append(j.getEquipo().getNombre()).append(")")
                        .append(" - ").append(j.getGoles()).append(" goles\n");
            }

            salida.setText(sb.toString());
        });

        VBox box = new VBox(15, btnPosiciones, btnGoleadores, salida);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        getChildren().add(box);
    }
}
