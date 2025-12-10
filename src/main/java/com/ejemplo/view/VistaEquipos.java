package com.ejemplo.view;

import java.util.List;

import com.ejemplo.model.Equipo;
import com.ejemplo.model.Jugador;
import com.ejemplo.model.Posicion;
import com.ejemplo.model.Torneo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class VistaEquipos extends VistaBase {

    private final Torneo torneo;
    private ComboBox<Equipo> cmbEquipos;
    private TableView<Jugador> tablaJugadores;
    private TextArea salida;

    public VistaEquipos(Torneo torneo) {
        super("⚽ Gestión de Equipos");
        this.torneo = torneo;

        construirInterfaz();
    }

    private void construirInterfaz() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        VBox contenedorPrincipal = new VBox(20);
        contenedorPrincipal.setPadding(new Insets(20));
        contenedorPrincipal.setAlignment(Pos.TOP_CENTER);
        
        // ========== PANEL 1: AGREGAR EQUIPO ==========
        VBox panelEquipo = crearPanelAgregarEquipo();
        
        // ========== PANEL 2: AGREGAR JUGADOR ==========
        VBox panelJugador = crearPanelAgregarJugador();
        
        // ========== PANEL 3: VER EQUIPOS Y JUGADORES ==========
        VBox panelVisualizacion = crearPanelVisualizacion();
        
        contenedorPrincipal.getChildren().addAll(panelEquipo, panelJugador, panelVisualizacion);
        scrollPane.setContent(contenedorPrincipal);
        
        VBox layoutPrincipal = new VBox(15);
        layoutPrincipal.setPadding(new Insets(15));
        layoutPrincipal.getChildren().add(scrollPane);
        
        getChildren().add(layoutPrincipal);
    }

    private VBox crearPanelAgregarEquipo() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #E3F2FD; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Text titulo = new Text("➕ AGREGAR NUEVO EQUIPO");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titulo.setFill(Color.web("#1976D2"));
        
        TextField txtNombreEquipo = new TextField();
        txtNombreEquipo.setPromptText("Nombre del equipo");
        txtNombreEquipo.setPrefWidth(300);
        txtNombreEquipo.setStyle("-fx-font-size: 14px;");
        
        Button btnAgregarEquipo = new Button("✅ Agregar Equipo");
        btnAgregarEquipo.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnAgregarEquipo.setOnAction(e -> {
            try {
                String nombre = txtNombreEquipo.getText().trim();
                if (nombre.isEmpty()) {
                    mostrarError("Error", "El nombre del equipo no puede estar vacío.");
                    return;
                }
                torneo.agregarEquipo(new Equipo(nombre));
                cmbEquipos.getItems().setAll(torneo.getEquipos());
                mostrarInfo("✅ Equipo agregado: " + nombre);
                txtNombreEquipo.clear();
            } catch (Exception ex) {
                mostrarError("Error al agregar equipo", ex.getMessage());
            }
        });
        
        HBox inputBox = new HBox(10, txtNombreEquipo, btnAgregarEquipo);
        inputBox.setAlignment(Pos.CENTER);
        
        panel.getChildren().addAll(titulo, inputBox);
        return panel;
    }

    private VBox crearPanelAgregarJugador() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #F1F8E9; -fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Text titulo = new Text("👤 AGREGAR JUGADOR A EQUIPO");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titulo.setFill(Color.web("#388E3C"));
        
        cmbEquipos = new ComboBox<>();
        cmbEquipos.getItems().setAll(torneo.getEquipos());
        cmbEquipos.setPromptText("Seleccione un equipo");
        cmbEquipos.setPrefWidth(300);
        cmbEquipos.setStyle("-fx-font-size: 14px;");
        
        TextField txtNombreJugador = new TextField();
        txtNombreJugador.setPromptText("Nombre del jugador");
        txtNombreJugador.setPrefWidth(200);
        txtNombreJugador.setStyle("-fx-font-size: 14px;");
        
        Spinner<Integer> spnEdad = new Spinner<>(12, 60, 18);
        spnEdad.setPrefWidth(80);
        spnEdad.setStyle("-fx-font-size: 14px;");
        
        Spinner<Integer> spnNumero = new Spinner<>(1, 99, 10);
        spnNumero.setPrefWidth(80);
        spnNumero.setStyle("-fx-font-size: 14px;");
        
        ComboBox<Posicion> cmbPosicion = new ComboBox<>();
        cmbPosicion.getItems().setAll(Posicion.values());
        cmbPosicion.setPromptText("Posición");
        cmbPosicion.setPrefWidth(150);
        cmbPosicion.setStyle("-fx-font-size: 14px;");
        
        Button btnAgregarJugador = new Button("✅ Agregar Jugador");
        btnAgregarJugador.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnAgregarJugador.setOnAction(e -> {
            try {
                Equipo equipo = cmbEquipos.getValue();
                if (equipo == null) {
                    mostrarError("Error", "Debes elegir un equipo primero.");
                    return;
                }
                String nombre = txtNombreJugador.getText().trim();
                int edad = spnEdad.getValue();
                int numero = spnNumero.getValue();
                Posicion pos = cmbPosicion.getValue();

                if (nombre.isEmpty() || pos == null) {
                    mostrarError("Error", "Debes llenar todos los campos del jugador.");
                    return;
                }

                Jugador jugador = new Jugador(nombre, edad, pos, numero, equipo);
                torneo.agregarJugadorEquipo(equipo, jugador);
                
                // Actualizar tabla si el equipo está seleccionado
                if (cmbEquipos.getValue() != null && cmbEquipos.getValue().equals(equipo)) {
                    actualizarTablaJugadores(equipo);
                }
                
                mostrarInfo("✅ Jugador agregado: " + nombre + " al equipo " + equipo.getNombre());
                txtNombreJugador.clear();
            } catch (Exception ex) {
                mostrarError("Error al agregar jugador", ex.getMessage());
            }
        });
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));
        
        grid.add(new Label("Equipo:"), 0, 0);
        grid.add(cmbEquipos, 1, 0, 2, 1);
        
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(txtNombreJugador, 1, 1);
        
        grid.add(new Label("Edad:"), 0, 2);
        grid.add(spnEdad, 1, 2);
        
        grid.add(new Label("Número:"), 2, 2);
        grid.add(spnNumero, 3, 2);
        
        grid.add(new Label("Posición:"), 0, 3);
        grid.add(cmbPosicion, 1, 3);
        
        panel.getChildren().addAll(titulo, grid, btnAgregarJugador);
        return panel;
    }

    private VBox crearPanelVisualizacion() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #FFF3E0; -fx-border-color: #FF9800; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Text titulo = new Text("📋 VER EQUIPOS Y JUGADORES");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titulo.setFill(Color.web("#F57C00"));
        
        ComboBox<Equipo> cmbEquiposVer = new ComboBox<>();
        cmbEquiposVer.getItems().setAll(torneo.getEquipos());
        cmbEquiposVer.setPromptText("Seleccione un equipo para ver sus jugadores");
        cmbEquiposVer.setPrefWidth(400);
        cmbEquiposVer.setStyle("-fx-font-size: 14px;");
        
        // Crear tabla de jugadores
        tablaJugadores = new TableView<>();
        tablaJugadores.setPrefHeight(300);
        tablaJugadores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaJugadores.setStyle("-fx-background-color: white;");
        
        // Columnas de la tabla
        TableColumn<Jugador, Integer> colNumero = new TableColumn<>("#");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colNumero.setPrefWidth(60);
        colNumero.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Jugador, String> colNombre = new TableColumn<>("NOMBRE");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(200);
        
        TableColumn<Jugador, Integer> colEdad = new TableColumn<>("EDAD");
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colEdad.setPrefWidth(80);
        colEdad.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Jugador, String> colPosicion = new TableColumn<>("POSICIÓN");
        colPosicion.setCellValueFactory(cellData -> {
            String pos = cellData.getValue().getPosicion() != null ? 
                cellData.getValue().getPosicion().toString() : "-";
            return javafx.beans.binding.Bindings.createObjectBinding(() -> pos);
        });
        colPosicion.setPrefWidth(120);
        
        TableColumn<Jugador, Integer> colGoles = new TableColumn<>("GOLES");
        colGoles.setCellValueFactory(new PropertyValueFactory<>("goles"));
        colGoles.setPrefWidth(80);
        colGoles.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Jugador, Integer> colAmarillas = new TableColumn<>("AMARILLAS");
        colAmarillas.setCellValueFactory(new PropertyValueFactory<>("tarjetasAmarillasAcumuladas"));
        colAmarillas.setPrefWidth(100);
        colAmarillas.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Jugador, Integer> colRojas = new TableColumn<>("ROJAS");
        colRojas.setCellValueFactory(new PropertyValueFactory<>("tarjetasRojasAcumuladas"));
        colRojas.setPrefWidth(80);
        colRojas.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Jugador, String> colEstado = new TableColumn<>("ESTADO");
        colEstado.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().estaSuspendido() ? "❌ Suspendido" : "✅ Activo";
            return javafx.beans.binding.Bindings.createObjectBinding(() -> estado);
        });
        colEstado.setPrefWidth(120);
        
        tablaJugadores.getColumns().addAll(colNumero, colNombre, colEdad, colPosicion, colGoles, colAmarillas, colRojas, colEstado);
        
        // Listener para actualizar tabla cuando se selecciona un equipo
        cmbEquiposVer.valueProperty().addListener((obs, oldEquipo, newEquipo) -> {
            if (newEquipo != null) {
                actualizarTablaJugadores(newEquipo);
            } else {
                tablaJugadores.getItems().clear();
            }
        });
        
        Label lblInfo = new Label("Seleccione un equipo para ver sus jugadores");
        lblInfo.setStyle("-fx-text-fill: #757575; -fx-font-style: italic;");
        
        panel.getChildren().addAll(titulo, cmbEquiposVer, lblInfo, tablaJugadores);
        return panel;
    }
    
    private void actualizarTablaJugadores(Equipo equipo) {
        List<Jugador> jugadores = equipo.getJugadores();
        ObservableList<Jugador> datos = FXCollections.observableArrayList(jugadores);
        tablaJugadores.setItems(datos);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    private void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
