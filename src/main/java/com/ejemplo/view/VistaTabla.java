package com.ejemplo.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ejemplo.model.*;
import com.ejemplo.util.GeneradorPDF;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class VistaTabla extends VistaBase {

    private final Torneo torneo;
    private ScrollPane scrollPane;
    private VBox contenedorPrincipal;

    public VistaTabla(Torneo torneo) {
        super("📊 Tablas del Torneo");
        this.torneo = torneo;

        inicializarComponentes();
        construirInterfaz();
    }

    private void inicializarComponentes() {
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        contenedorPrincipal = new VBox(20);
        contenedorPrincipal.setPadding(new Insets(20));
        contenedorPrincipal.setAlignment(Pos.TOP_CENTER);
    }

    private void construirInterfaz() {
        Button btnPosiciones = new Button("🏆 Ver Tabla de Posiciones");
        btnPosiciones.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnPosiciones.setOnAction(e -> mostrarTablaPosiciones());
        
        Button btnGoleadores = new Button("⚽ Ver Tabla de Goleadores");
        btnGoleadores.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnGoleadores.setOnAction(e -> mostrarTablaGoleadores());
        
        HBox botones = new HBox(15, btnPosiciones, btnGoleadores);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(10));
        
        scrollPane.setContent(contenedorPrincipal);
        
        VBox layoutPrincipal = new VBox(15);
        layoutPrincipal.setPadding(new Insets(15));
        layoutPrincipal.getChildren().addAll(botones, scrollPane);
        
        getChildren().add(layoutPrincipal);
    }

    private void mostrarTablaPosiciones() {
        contenedorPrincipal.getChildren().clear();
        
        Map<String, List<Equipo>> grupos = torneo.getGrupos();
        
        if (grupos.isEmpty()) {
            Label mensaje = new Label("⚠️ No hay grupos generados. Por favor, realiza el sorteo primero.");
            mensaje.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 14px;");
            contenedorPrincipal.getChildren().add(mensaje);
            return;
        }

        Text titulo = new Text("🏆 TABLA DE POSICIONES POR GRUPO 🏆");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titulo.setFill(Color.web("#1976D2"));
        
        // Botón para descargar PDF
        Button btnDescargarPDF = new Button("📄 Descargar PDF de Tabla de Posiciones");
        btnDescargarPDF.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnDescargarPDF.setOnAction(e -> descargarPDFPosiciones());
        
        HBox tituloBox = new HBox(15, titulo, btnDescargarPDF);
        tituloBox.setAlignment(Pos.CENTER);
        tituloBox.setPadding(new Insets(10));
        
        contenedorPrincipal.getChildren().add(tituloBox);

        // Crear una tabla para cada grupo
        for (Map.Entry<String, List<Equipo>> entry : grupos.entrySet()) {
            String nombreGrupo = entry.getKey();
            List<Equipo> equiposDelGrupo = entry.getValue();
            
            // Clonar y ordenar
            List<Equipo> tablaGrupo = new ArrayList<>(equiposDelGrupo);
            tablaGrupo.sort((e1, e2) -> {
                int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
                if (cmp == 0)
                    cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                                          (e1.getGolesFavor() - e1.getGolesContra()));
                if (cmp == 0)
                    cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
                return cmp;
            });

            // Título del grupo
            Text tituloGrupo = new Text(nombreGrupo);
            tituloGrupo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            tituloGrupo.setFill(Color.web("#424242"));
            
            // Crear TableView
            TableView<Equipo> tabla = crearTablaPosiciones(tablaGrupo);
            
            VBox grupoBox = new VBox(10);
            grupoBox.setPadding(new Insets(15));
            grupoBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
            grupoBox.getChildren().addAll(tituloGrupo, tabla);
            
            contenedorPrincipal.getChildren().add(grupoBox);
        }
    }

    private TableView<Equipo> crearTablaPosiciones(List<Equipo> equipos) {
        TableView<Equipo> tabla = new TableView<>();
        ObservableList<Equipo> datos = FXCollections.observableArrayList(equipos);
        tabla.setItems(datos);
        tabla.setPrefHeight(200);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Columna de Posición
        TableColumn<Equipo, Integer> colPos = new TableColumn<>("#");
        colPos.setCellValueFactory(cellData -> {
            int pos = equipos.indexOf(cellData.getValue()) + 1;
            return javafx.beans.binding.Bindings.createObjectBinding(() -> pos);
        });
        colPos.setPrefWidth(50);
        colPos.setStyle("-fx-alignment: CENTER;");
        
        // Columna de Equipo
        TableColumn<Equipo, String> colEquipo = new TableColumn<>("EQUIPO");
        colEquipo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEquipo.setPrefWidth(200);
        colEquipo.setStyle("-fx-alignment: CENTER-LEFT;");
        
        // Columna de Partidos Jugados
        TableColumn<Equipo, Integer> colPJ = new TableColumn<>("PJ");
        colPJ.setCellValueFactory(new PropertyValueFactory<>("partidosJugados"));
        colPJ.setPrefWidth(60);
        colPJ.setStyle("-fx-alignment: CENTER;");
        
        // Columna de Ganados
        TableColumn<Equipo, Integer> colG = new TableColumn<>("G");
        colG.setCellValueFactory(new PropertyValueFactory<>("ganados"));
        colG.setPrefWidth(60);
        colG.setStyle("-fx-alignment: CENTER;");
        
        // Columna de Empatados
        TableColumn<Equipo, Integer> colE = new TableColumn<>("E");
        colE.setCellValueFactory(new PropertyValueFactory<>("empatados"));
        colE.setPrefWidth(60);
        colE.setStyle("-fx-alignment: CENTER;");
        
        // Columna de Perdidos
        TableColumn<Equipo, Integer> colP = new TableColumn<>("P");
        colP.setCellValueFactory(new PropertyValueFactory<>("perdidos"));
        colP.setPrefWidth(60);
        colP.setStyle("-fx-alignment: CENTER;");
        
        // Columna de Goles a Favor
        TableColumn<Equipo, Integer> colGF = new TableColumn<>("GF");
        colGF.setCellValueFactory(new PropertyValueFactory<>("golesFavor"));
        colGF.setPrefWidth(60);
        colGF.setStyle("-fx-alignment: CENTER;");
        
        // Columna de Goles en Contra
        TableColumn<Equipo, Integer> colGC = new TableColumn<>("GC");
        colGC.setCellValueFactory(new PropertyValueFactory<>("golesContra"));
        colGC.setPrefWidth(60);
        colGC.setStyle("-fx-alignment: CENTER;");
        
        // Columna de Diferencia de Goles
        TableColumn<Equipo, Integer> colDG = new TableColumn<>("DG");
        colDG.setCellValueFactory(cellData -> {
            int dif = cellData.getValue().getGolesFavor() - cellData.getValue().getGolesContra();
            return javafx.beans.binding.Bindings.createObjectBinding(() -> dif);
        });
        colDG.setPrefWidth(60);
        colDG.setStyle("-fx-alignment: CENTER;");
        
        // Columna de Puntos
        TableColumn<Equipo, Integer> colPts = new TableColumn<>("PTS");
        colPts.setCellValueFactory(new PropertyValueFactory<>("puntos"));
        colPts.setPrefWidth(70);
        colPts.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        
        tabla.getColumns().addAll(colPos, colEquipo, colPJ, colG, colE, colP, colGF, colGC, colDG, colPts);
        
        // Estilo de la tabla
        tabla.setStyle("-fx-background-color: white;");
        
        // Estilo de encabezados
        tabla.getStyleClass().add("table-view");
        
        return tabla;
    }

    private void mostrarTablaGoleadores() {
        contenedorPrincipal.getChildren().clear();
        
        List<Jugador> goleadoresTemp = torneo.mostrarGoleadores();
        
        // Filtrar solo los jugadores que tienen goles (mayor a 0)
        final List<Jugador> goleadores = goleadoresTemp.stream()
                .filter(j -> j.getGoles() > 0)
                .collect(Collectors.toList());
        
        if (goleadores.isEmpty()) {
            Label mensaje = new Label("⚠️ No hay goleadores registrados aún.");
            mensaje.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 14px;");
            contenedorPrincipal.getChildren().add(mensaje);
            return;
        }

        Text titulo = new Text("⚽ TABLA DE GOLEADORES ⚽");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titulo.setFill(Color.web("#4CAF50"));
        
        // Botón para descargar PDF
        Button btnDescargarPDF = new Button("📄 Descargar PDF de Goleadores");
        btnDescargarPDF.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnDescargarPDF.setOnAction(e -> descargarPDFGoleadores());
        
        HBox tituloBox = new HBox(15, titulo, btnDescargarPDF);
        tituloBox.setAlignment(Pos.CENTER);
        tituloBox.setPadding(new Insets(10));
        
        contenedorPrincipal.getChildren().add(tituloBox);

        // Crear TableView para goleadores
        TableView<Jugador> tabla = new TableView<>();
        ObservableList<Jugador> datos = FXCollections.observableArrayList(goleadores);
        tabla.setItems(datos);
        tabla.setPrefHeight(400);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Columna de Posición
        TableColumn<Jugador, Integer> colPos = new TableColumn<>("#");
        colPos.setCellValueFactory(cellData -> {
            int pos = goleadores.indexOf(cellData.getValue()) + 1;
            return javafx.beans.binding.Bindings.createObjectBinding(() -> pos);
        });
        colPos.setPrefWidth(50);
        colPos.setStyle("-fx-alignment: CENTER;");
        
        // Columna de Nombre
        TableColumn<Jugador, String> colNombre = new TableColumn<>("JUGADOR");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(200);
        
        // Columna de Equipo
        TableColumn<Jugador, String> colEquipo = new TableColumn<>("EQUIPO");
        colEquipo.setCellValueFactory(cellData -> {
            String nombreEquipo = cellData.getValue().getEquipo() != null ? 
                cellData.getValue().getEquipo().getNombre() : "Sin equipo";
            return javafx.beans.binding.Bindings.createObjectBinding(() -> nombreEquipo);
        });
        colEquipo.setPrefWidth(150);
        
        // Columna de Posición (del jugador)
        TableColumn<Jugador, String> colPosicion = new TableColumn<>("POSICIÓN");
        colPosicion.setCellValueFactory(cellData -> {
            String pos = cellData.getValue().getPosicion() != null ? 
                cellData.getValue().getPosicion().toString() : "-";
            return javafx.beans.binding.Bindings.createObjectBinding(() -> pos);
        });
        colPosicion.setPrefWidth(100);
        
        // Columna de Goles
        TableColumn<Jugador, Integer> colGoles = new TableColumn<>("GOLES");
        colGoles.setCellValueFactory(new PropertyValueFactory<>("goles"));
        colGoles.setPrefWidth(100);
        colGoles.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        
        tabla.getColumns().addAll(colPos, colNombre, colEquipo, colPosicion, colGoles);
        
        // Estilo de la tabla
        tabla.setStyle("-fx-background-color: white;");
        
        VBox tablaBox = new VBox(10);
        tablaBox.setPadding(new Insets(15));
        tablaBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        tablaBox.getChildren().add(tabla);
        
        contenedorPrincipal.getChildren().add(tablaBox);
    }
    
    private void descargarPDFPosiciones() {
        try {
            Map<String, List<Equipo>> grupos = torneo.getGrupos();
            if (grupos.isEmpty()) {
                mostrarError("No hay grupos generados para generar el PDF.");
                return;
            }
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar PDF de Tabla de Posiciones");
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileChooser.setInitialFileName("TablaPosiciones_" + timestamp + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
            
            Stage stage = (Stage) contenedorPrincipal.getScene().getWindow();
            java.io.File archivo = fileChooser.showSaveDialog(stage);
            
            if (archivo != null) {
                String rutaCompleta = archivo.getAbsolutePath();
                if (!rutaCompleta.toLowerCase().endsWith(".pdf")) {
                    rutaCompleta += ".pdf";
                }
                GeneradorPDF.generarPDFTablaPosiciones(torneo, rutaCompleta);
                mostrarInfo("✅ PDF de Tabla de Posiciones generado exitosamente:\n" + rutaCompleta);
            }
        } catch (Exception ex) {
            mostrarError("Error al generar el PDF: " + ex.getMessage());
        }
    }
    
    private void descargarPDFGoleadores() {
        try {
            List<Jugador> goleadores = torneo.mostrarGoleadores();
            goleadores = goleadores.stream()
                    .filter(j -> j.getGoles() > 0)
                    .collect(Collectors.toList());
            
            if (goleadores.isEmpty()) {
                mostrarError("No hay goleadores registrados para generar el PDF.");
                return;
            }
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar PDF de Goleadores");
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileChooser.setInitialFileName("TablaGoleadores_" + timestamp + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
            
            Stage stage = (Stage) contenedorPrincipal.getScene().getWindow();
            java.io.File archivo = fileChooser.showSaveDialog(stage);
            
            if (archivo != null) {
                String rutaCompleta = archivo.getAbsolutePath();
                if (!rutaCompleta.toLowerCase().endsWith(".pdf")) {
                    rutaCompleta += ".pdf";
                }
                GeneradorPDF.generarPDFGoleadores(torneo, rutaCompleta);
                mostrarInfo("✅ PDF de Goleadores generado exitosamente:\n" + rutaCompleta);
            }
        } catch (Exception ex) {
            mostrarError("Error al generar el PDF: " + ex.getMessage());
        }
    }
    
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
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
