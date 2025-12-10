package com.ejemplo.view;

import com.ejemplo.model.Eliminatoria;
import com.ejemplo.model.Equipo;
import com.ejemplo.model.Partido;
import com.ejemplo.model.Torneo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.List;

public class VistaEliminatoria extends VistaBase {

    private final Torneo torneo;
    private final Eliminatoria eliminatoria;
    private TextArea salida;
    private ScrollPane scrollPane;
    private VBox contenedorPrincipal;

    public VistaEliminatoria(Torneo torneo) {
        super("🏆 Fase Eliminatoria");
        this.torneo = torneo;
        this.eliminatoria = torneo.getEliminatoria();
        
        inicializarComponentes();
        construirInterfaz();
        actualizarVista();
    }

    private void inicializarComponentes() {
        salida = new TextArea();
        salida.setEditable(false);
        salida.setPrefHeight(150);
        salida.setWrapText(true);
        
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
        // Botón para actualizar la vista
        Button btnActualizar = new Button("🔄 Actualizar Vista");
        btnActualizar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnActualizar.setOnAction(e -> actualizarVista());
        
        // Botón para verificar y generar cuartos automáticamente
        Button btnVerificarCuartos = new Button("🎲 Verificar y Generar Cuartos");
        btnVerificarCuartos.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnVerificarCuartos.setOnAction(e -> verificarYGenerarCuartos());
        
        // Botón para descargar PDF de eliminatoria
        Button btnDescargarPDF = new Button("📄 Descargar PDF Eliminatoria");
        btnDescargarPDF.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnDescargarPDF.setOnAction(e -> descargarPDFEliminatoria());
        
        HBox botonesSuperiores = new HBox(15, btnActualizar, btnVerificarCuartos, btnDescargarPDF);
        botonesSuperiores.setAlignment(Pos.CENTER);
        botonesSuperiores.setPadding(new Insets(10));
        
        scrollPane.setContent(contenedorPrincipal);
        
        VBox layoutPrincipal = new VBox(15);
        layoutPrincipal.setPadding(new Insets(15));
        layoutPrincipal.getChildren().addAll(botonesSuperiores, scrollPane, salida);
        
        getChildren().add(layoutPrincipal);
    }

    private void actualizarVista() {
        contenedorPrincipal.getChildren().clear();
        
        // Obtener la eliminatoria del torneo
        Eliminatoria elim = torneo.getEliminatoria();
        
        // Panel de Cuartos de Final
        crearPanelFase("🏟️ CUARTOS DE FINAL", elim.getPartidosCuartos(), "cuartos");
        
        // Panel de Semifinales
        crearPanelFase("⚔️ SEMIFINALES", elim.getPartidosSemifinal(), "semifinales");
        
        // Panel de Tercer Puesto
        if (elim.getPartidoTercerPuesto() != null) {
            crearPanelPartidoUnico("🥉 TERCER PUESTO", elim.getPartidoTercerPuesto());
        } else {
            crearPanelVacio("🥉 TERCER PUESTO", "Las semifinales aún no se han jugado");
        }
        
        // Panel de Final
        if (elim.getPartidoFinal() != null) {
            crearPanelPartidoUnico("🏆 FINAL", elim.getPartidoFinal());
        } else {
            crearPanelVacio("🏆 FINAL", "Las semifinales aún no se han jugado");
        }
        
        // Actualizar mensaje informativo
        actualizarMensaje();
    }

    private void crearPanelFase(String titulo, List<Partido> partidos, String fase) {
        VBox panelFase = new VBox(15);
        panelFase.setPadding(new Insets(20));
        panelFase.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        // Título de la fase
        Text tituloText = new Text(titulo);
        tituloText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tituloText.setFill(Color.web("#1976D2"));
        
        if (partidos == null || partidos.isEmpty()) {
            Label mensaje = new Label("Aún no se han generado los partidos de esta fase");
            mensaje.setStyle("-fx-text-fill: #757575; -fx-font-style: italic;");
            panelFase.getChildren().addAll(tituloText, mensaje);
        } else {
            // Crear un grid para los partidos
            GridPane gridPartidos = new GridPane();
            gridPartidos.setHgap(20);
            gridPartidos.setVgap(15);
            gridPartidos.setPadding(new Insets(10));
            
            int col = 0;
            int row = 0;
            for (Partido partido : partidos) {
                VBox cardPartido = crearCardPartido(partido, fase);
                gridPartidos.add(cardPartido, col, row);
                col++;
                if (col >= 2) {
                    col = 0;
                    row++;
                }
            }
            
            panelFase.getChildren().addAll(tituloText, gridPartidos);
        }
        
        contenedorPrincipal.getChildren().add(panelFase);
    }

    private void crearPanelPartidoUnico(String titulo, Partido partido) {
        VBox panelFase = new VBox(15);
        panelFase.setPadding(new Insets(20));
        panelFase.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #FF9800; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Text tituloText = new Text(titulo);
        tituloText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tituloText.setFill(Color.web("#F57C00"));
        
        VBox cardPartido = crearCardPartido(partido, "final");
        cardPartido.setMaxWidth(600);
        
        panelFase.getChildren().addAll(tituloText, cardPartido);
        contenedorPrincipal.getChildren().add(panelFase);
    }

    private void crearPanelVacio(String titulo, String mensaje) {
        VBox panelFase = new VBox(15);
        panelFase.setPadding(new Insets(20));
        panelFase.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #9E9E9E; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Text tituloText = new Text(titulo);
        tituloText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tituloText.setFill(Color.web("#616161"));
        
        Label mensajeLabel = new Label(mensaje);
        mensajeLabel.setStyle("-fx-text-fill: #757575; -fx-font-style: italic;");
        
        panelFase.getChildren().addAll(tituloText, mensajeLabel);
        contenedorPrincipal.getChildren().add(panelFase);
    }

    private VBox crearCardPartido(Partido partido, String fase) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
        card.setMinWidth(300);
        
        // Información del partido
        Equipo local = partido.getEquipoLocal();
        Equipo visitante = partido.getEquipoVisitante();
        
        Label lblLocal = new Label(local.getNombre());
        lblLocal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblVisitante = new Label(visitante.getNombre());
        lblVisitante.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Estado del partido
        String estado;
        String colorEstado;
        if (partido.getJugado()) {
            estado = "✅ JUGADO";
            colorEstado = "#4CAF50";
            
            // Mostrar resultado
            Label lblResultado = new Label(String.format("%d - %d", 
                partido.getGolesLocal(), partido.getGolesVisitante()));
            lblResultado.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
            
            // Mostrar ganador
            Equipo ganador = partido.getGanador();
            if (ganador != null) {
                Label lblGanador = new Label("🏆 Ganador: " + ganador.getNombre());
                lblGanador.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF9800;");
                card.getChildren().add(lblGanador);
            }
            
            card.getChildren().addAll(lblLocal, lblResultado, lblVisitante);
        } else {
            estado = "⏳ PENDIENTE";
            colorEstado = "#FF9800";
            Label lblVs = new Label("vs");
            lblVs.setStyle("-fx-font-size: 16px; -fx-text-fill: #757575;");
            card.getChildren().addAll(lblLocal, lblVs, lblVisitante);
        }
        
        Label lblEstado = new Label(estado);
        lblEstado.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold;", colorEstado));
        
        // Todos los partidos de eliminatoria se registran desde la pestaña de Gestión de Partidos
        if (!partido.getJugado()) {
            Label lblInfo = new Label("💡 Registra el resultado desde la pestaña 'Partidos'");
            lblInfo.setStyle("-fx-text-fill: #757575; -fx-font-style: italic; -fx-font-size: 11px;");
            card.getChildren().add(lblInfo);
        }
        
        card.getChildren().add(lblEstado);
        
        return card;
    }

    private void registrarResultadoPartido(Partido partido, String fase) {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("📝 Registrar Resultado");
        dialog.setHeaderText(String.format("Registrar resultado: %s vs %s", 
            partido.getEquipoLocal().getNombre(), 
            partido.getEquipoVisitante().getNombre()));
        
        ButtonType registrarButtonType = new ButtonType("✅ Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registrarButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField txtGolesLocal = new TextField();
        txtGolesLocal.setPromptText("Goles");
        TextField txtGolesVisitante = new TextField();
        txtGolesVisitante.setPromptText("Goles");
        
        // Si es eliminatoria y puede haber empate, agregar opción de penales
        CheckBox chkPenales = new CheckBox("Definición por penales");
        TextField txtPenalesLocal = new TextField();
        txtPenalesLocal.setPromptText("Penales");
        txtPenalesLocal.setDisable(true);
        TextField txtPenalesVisitante = new TextField();
        txtPenalesVisitante.setPromptText("Penales");
        txtPenalesVisitante.setDisable(true);
        
        chkPenales.setOnAction(e -> {
            boolean activado = chkPenales.isSelected();
            txtPenalesLocal.setDisable(!activado);
            txtPenalesVisitante.setDisable(!activado);
        });
        
        grid.add(new Label(partido.getEquipoLocal().getNombre() + ":"), 0, 0);
        grid.add(txtGolesLocal, 1, 0);
        grid.add(new Label(partido.getEquipoVisitante().getNombre() + ":"), 0, 1);
        grid.add(txtGolesVisitante, 1, 1);
        grid.add(chkPenales, 0, 2, 2, 1);
        grid.add(new Label("Penales " + partido.getEquipoLocal().getNombre() + ":"), 0, 3);
        grid.add(txtPenalesLocal, 1, 3);
        grid.add(new Label("Penales " + partido.getEquipoVisitante().getNombre() + ":"), 0, 4);
        grid.add(txtPenalesVisitante, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registrarButtonType) {
                try {
                    int golesLocal = Integer.parseInt(txtGolesLocal.getText().trim());
                    int golesVisitante = Integer.parseInt(txtGolesVisitante.getText().trim());
                    
                    if (golesLocal < 0 || golesVisitante < 0) {
                        mostrarError("Los goles no pueden ser negativos");
                        return null;
                    }
                    
                    // Si hay empate y se marcó penales, registrar penales
                    if (golesLocal == golesVisitante && chkPenales.isSelected()) {
                        int penalesLocal = Integer.parseInt(txtPenalesLocal.getText().trim());
                        int penalesVisitante = Integer.parseInt(txtPenalesVisitante.getText().trim());
                        
                        Equipo ganador = penalesLocal > penalesVisitante ? 
                            partido.getEquipoLocal() : partido.getEquipoVisitante();
                        
                        partido.setResultadoPenales(penalesLocal, penalesVisitante, ganador);
                    }
                    
                    // Registrar resultado
                    partido.registrarResultados(golesLocal, golesVisitante);
                    
                    // Avanzar en la eliminatoria si es necesario
                    eliminatoria.registrarResultadoYAvanzar(partido, golesLocal, golesVisitante);
                    
                    return new int[]{golesLocal, golesVisitante};
                } catch (NumberFormatException e) {
                    mostrarError("Debe ingresar números válidos");
                    return null;
                } catch (Exception e) {
                    mostrarError("Error: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado != null) {
                mostrarInfo("✅ Resultado registrado correctamente");
                actualizarVista();
            }
        });
    }

    private void verificarYGenerarCuartos() {
        try {
            if (torneo.todosPartidosDeGruposJugados()) {
                List<Equipo> clasificados = torneo.clasificarOchoMejores();
                if (clasificados.size() >= 8) {
                    eliminatoria.sortearCuartos(clasificados);
                    mostrarInfo("✅ Cuartos de final generados correctamente");
                    actualizarVista();
                } else {
                    mostrarError("No hay suficientes equipos clasificados (se necesitan 8)");
                }
            } else {
                mostrarError("Deben jugarse todos los partidos de grupos primero");
            }
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void actualizarMensaje() {
        Eliminatoria elim = torneo.getEliminatoria();
        StringBuilder mensaje = new StringBuilder();
        
        int cuartosJugados = (int) elim.getPartidosCuartos().stream().filter(Partido::getJugado).count();
        int semisJugadas = (int) elim.getPartidosSemifinal().stream().filter(Partido::getJugado).count();
        
        mensaje.append("📊 Estado de la Eliminatoria:\n");
        mensaje.append(String.format("  • Cuartos: %d/%d jugados\n", cuartosJugados, elim.getPartidosCuartos().size()));
        mensaje.append(String.format("  • Semifinales: %d/%d jugadas\n", semisJugadas, elim.getPartidosSemifinal().size()));
        mensaje.append(String.format("  • Tercer Puesto: %s\n", elim.getPartidoTercerPuesto() != null && elim.getPartidoTercerPuesto().getJugado() ? "Jugado" : "Pendiente"));
        mensaje.append(String.format("  • Final: %s\n", elim.getPartidoFinal() != null && elim.getPartidoFinal().getJugado() ? "Jugada" : "Pendiente"));
        
        salida.setText(mensaje.toString());
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void descargarPDFEliminatoria() {
        try {
            Eliminatoria elim = torneo.getEliminatoria();
            
            // Verificar si hay partidos de eliminatoria
            if (elim.getPartidosCuartos().isEmpty() && 
                elim.getPartidosSemifinal().isEmpty() && 
                elim.getPartidoFinal() == null && 
                elim.getPartidoTercerPuesto() == null) {
                mostrarError("No hay partidos de eliminatoria para generar el PDF.");
                return;
            }
            
            // Crear FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar PDF de Eliminatoria");
            
            // Crear nombre de archivo sugerido con fecha y hora
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = "Eliminatoria_" + timestamp + ".pdf";
            fileChooser.setInitialFileName(nombreArchivo);
            
            // Filtrar solo archivos PDF
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);
            
            // Obtener la ventana actual (Stage)
            Stage stage = (Stage) salida.getScene().getWindow();
            
            // Mostrar diálogo de guardado
            java.io.File archivo = fileChooser.showSaveDialog(stage);
            
            if (archivo != null) {
                try {
                    // Generar el PDF en la ubicación seleccionada
                    String rutaCompleta = archivo.getAbsolutePath();
                    // Asegurar que tenga extensión .pdf
                    if (!rutaCompleta.toLowerCase().endsWith(".pdf")) {
                        rutaCompleta += ".pdf";
                    }
                    
                    com.ejemplo.util.GeneradorPDF.generarPDFEliminatoria(torneo, rutaCompleta);
                    mostrarInfo("✅ PDF de eliminatoria generado exitosamente:\n" + rutaCompleta);
                } catch (Exception ex) {
                    mostrarError("Error al generar el PDF: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            mostrarError("Error: " + ex.getMessage());
        }
    }
}
