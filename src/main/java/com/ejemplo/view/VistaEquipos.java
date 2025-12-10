package com.ejemplo.view;

import java.util.List;
import java.util.Map;

import com.ejemplo.model.Equipo;
import com.ejemplo.model.Jugador;
import com.ejemplo.model.Posicion;
import com.ejemplo.model.PosicionTabla;
import com.ejemplo.model.Torneo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class VistaEquipos extends VistaBase {

    private final Torneo torneo;

    public VistaEquipos(Torneo torneo) {
        super("⚽ Gestión de Equipos");
        this.torneo = torneo;

        // 🔹 Campos de entrada
        TextField txtNombreEquipo = new TextField();
        txtNombreEquipo.setPromptText("Nombre del equipo");

        Button btnAgregarEquipo = new Button("Agregar Equipo");

        TextField txtNombreJugador = new TextField();
        txtNombreJugador.setPromptText("Nombre del jugador");

        Spinner<Integer> spnEdad = new Spinner<>(12, 60, 18);
        Spinner<Integer> spnNumero = new Spinner<>(1, 99, 10);

        ComboBox<Posicion> cmbPosicion = new ComboBox<>();
        cmbPosicion.getItems().setAll(Posicion.values());

        ComboBox<Equipo> cmbEquipos = new ComboBox<>();
        // 💡 Mejor práctica: usar setAll en lugar de addAll para refrescar
        cmbEquipos.getItems().setAll(torneo.getEquipos());

        Button btnAgregarJugador = new Button("Agregar Jugador");
        Button btnMostrarEquipos = new Button("Mostrar Equipos");
        Button btnMostrarTabla = new Button("Mostrar Tabla");

        // ListView para mostrar equipos
        ListView<Equipo> listaEquipos = new ListView<>();
        listaEquipos.setPrefHeight(200);
        listaEquipos.setVisible(false);
        listaEquipos.setManaged(false);

        TextArea salida = new TextArea();
        salida.setEditable(false);
        salida.setPrefHeight(250);

        // 🔹 Acciones
        btnAgregarEquipo.setOnAction(e -> {
            try {
                String nombre = txtNombreEquipo.getText().trim();
                torneo.agregarEquipo(new Equipo(nombre));
                cmbEquipos.getItems().setAll(torneo.getEquipos());
                listaEquipos.getItems().setAll(torneo.getEquipos());
                salida.setText("✅ Equipo agregado: " + nombre);
                txtNombreEquipo.clear();
            } catch (Exception ex) {
                mostrarError("Error al agregar equipo", ex.getMessage());
            }
        });

        btnAgregarJugador.setOnAction(e -> {
            try {
                Equipo equipo = cmbEquipos.getValue();
                if (equipo == null) {
                    mostrarError("Selecciona un equipo", "Debes elegir un equipo primero.");
                    return;
                }
                String nombre = txtNombreJugador.getText().trim();
                int edad = spnEdad.getValue();
                int numero = spnNumero.getValue();
                Posicion pos = cmbPosicion.getValue();

                if (nombre.isEmpty() || pos == null) {
                    mostrarError("Campos vacíos", "Debes llenar todos los campos del jugador.");
                    return;
                }

                // 🚨 CORRECCIÓN CLAVE: Pasamos el objeto 'equipo' al constructor de Jugador
                // Asume que el constructor es: Jugador(String nombre, int edad, Posicion pos, int numero, Equipo equipo)
                Jugador jugador = new Jugador(nombre, edad, pos, numero, equipo);
                
                torneo.agregarJugadorEquipo(equipo, jugador);
                salida.setText("✅ Jugador agregado: " + nombre + " al equipo " + equipo.getNombre());

                txtNombreJugador.clear();
            } catch (Exception ex) {
                mostrarError("Error al agregar jugador", ex.getMessage());
            }
        });

        // 🔹 Acción del botón Mostrar Equipos
        btnMostrarEquipos.setOnAction(e -> {
            listaEquipos.getItems().setAll(torneo.getEquipos());
            if (torneo.getEquipos().isEmpty()) {
                salida.setText("⚠️ No hay equipos registrados.");
                listaEquipos.setVisible(false);
                listaEquipos.setManaged(false);
            } else {
                listaEquipos.setVisible(true);
                listaEquipos.setManaged(true);
                salida.setText("📋 Selecciona un equipo para ver sus jugadores:");
            }
        });

        // 🔹 Acción al seleccionar un equipo de la lista
        listaEquipos.getSelectionModel().selectedItemProperty().addListener((obs, oldEquipo, newEquipo) -> {
            if (newEquipo != null) {
                mostrarJugadoresEquipo(newEquipo, salida);
            }
        });

        // 🔹 Layout
        VBox boxEquipo = new VBox(10, new Text("Agregar Equipo"), txtNombreEquipo, btnAgregarEquipo);
        VBox boxJugador = new VBox(10, new Text("Agregar Jugador"),
                new HBox(5, new Label("Equipo:"), cmbEquipos),
                txtNombreJugador,
                new HBox(10, new Label("Edad:"), spnEdad, new Label("Número:"), spnNumero),
                new HBox(10, new Label("Posición:"), cmbPosicion),
                btnAgregarJugador);

        VBox boxBotones = new VBox(15, boxEquipo, boxJugador, btnMostrarEquipos, listaEquipos, btnMostrarTabla, salida);
        boxBotones.setPadding(new Insets(20));
        boxBotones.setAlignment(Pos.TOP_CENTER);

        getChildren().add(boxBotones);
    }

    // 🔹 Método para mostrar los jugadores de un equipo
    private void mostrarJugadoresEquipo(Equipo equipo, TextArea salida) {
        StringBuilder sb = new StringBuilder();
        sb.append("👥 JUGADORES DEL EQUIPO: ").append(equipo.getNombre()).append("\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        List<Jugador> jugadores = equipo.getJugadores();
        if (jugadores.isEmpty()) {
            sb.append("⚠️ Este equipo no tiene jugadores registrados.\n");
        } else {
            sb.append("Total de jugadores: ").append(jugadores.size()).append("\n\n");
            for (int i = 0; i < jugadores.size(); i++) {
                Jugador jugador = jugadores.get(i);
                sb.append(String.format("%d. %s\n", i + 1, jugador.toString()));
            }
        }
        
        salida.setText(sb.toString());
    }

    // 🛑 Dejamos solo el método auxiliar 'mostrarError'
    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}