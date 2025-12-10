package com.ejemplo.view;

import com.ejemplo.controller.PartidosController;
import com.ejemplo.model.*;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VistaPartidos extends VistaBase {

    private final PartidosController controller;
    private final Torneo torneo;
    
    // Controles para asignar fecha (Programación)
    private ComboBox<Partido> cmbPartidosFecha; 
    private TextField txtFecha;
    
    // Controles para registrar resultado (Resultados)
    private ComboBox<Partido> cmbPartidosResultado;
    private TextField txtGolesLocal;
    private TextField txtGolesVisitante;
    private Label lblGolesLocalNombre;
    private Label lblGolesVisitanteNombre;
    
    // Controles para editar resultado
    private ComboBox<Partido> cmbPartidosEditados;
    private TextField txtGolesLocalEdit;
    private TextField txtGolesVisitanteEdit;
    private Label lblGolesLocalEditNombre;
    private Label lblGolesVisitanteEditNombre;
    
    // 🆕 Botón para gestión de tarjetas
    private Button btnGestionarTarjetas;
    
    // ⚽ Botón para registrar goles por jugador
    private Button btnRegistrarGoles;
    
    // Controles generales
    private Button btnSortear;
    private Button btnDescargarPDF;
    private TextArea salida;

    public VistaPartidos(Torneo torneo) {
        super(""); // Sin título automático, usaremos nuestro título personalizado
        this.torneo = torneo;
        this.controller = new PartidosController(torneo);
        
        // 1. Inicializar todos los componentes (incluyendo etiquetas dinámicas)
        inicializarComponentes();

        // 2. Crear los paneles de interfaz PRIMERO (para que todos los componentes estén inicializados)
        VBox boxFecha = crearPanelAsignacionFecha();
        VBox boxResultado = crearPanelRegistroResultado();
        VBox boxEditar = crearPanelEditarResultado();
        
        // 3. Comprobar estado inicial del sorteo y cargar datos (DESPUÉS de crear los paneles)
        if (torneo.isSorteoRealizado()) {
              btnSortear.setDisable(true);
        }
        actualizarCombos(); // Actualizar combos después de que todos los componentes estén inicializados
        
        // HBox para contener los tres paneles de acción
        HBox paneles = new HBox(20, boxFecha, boxResultado, boxEditar);
        paneles.setAlignment(Pos.CENTER);
        paneles.setPadding(new Insets(10));
        
        // Botón para mostrar todos los partidos
        Button btnMostrar = new Button("👀 Mostrar Partidos");
        btnMostrar.setOnAction(e -> mostrarPartidos());

        // Layout principal que contiene los paneles y el área de salida
        HBox botonesAccion = new HBox(15, btnSortear, btnDescargarPDF);
        botonesAccion.setAlignment(Pos.CENTER);
        botonesAccion.setPadding(new Insets(10));
        
        btnSortear.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnDescargarPDF.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnMostrar.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        
        Label tituloPrincipal = new Label("📅 GESTIÓN DE PARTIDOS");
        tituloPrincipal.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1976D2; -fx-padding: 20 0;");
        
        salida.setStyle("-fx-font-size: 13px; -fx-font-family: 'Consolas', 'Monaco', monospace;");
        
        VBox boxPrincipal = new VBox(20);
        boxPrincipal.setPadding(new Insets(20));
        boxPrincipal.setAlignment(Pos.TOP_CENTER);
        boxPrincipal.getChildren().addAll(
                tituloPrincipal,
                botonesAccion,
                paneles, 
                salida,
                btnMostrar
        );

        // Usar ScrollPane para asegurar que el contenido sea visible
        ScrollPane scrollPane = new ScrollPane(boxPrincipal);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefViewportWidth(Double.MAX_VALUE);
        scrollPane.setPrefViewportHeight(Double.MAX_VALUE);

        // Limpiar children anteriores (del VistaBase) y agregar nuestro contenido
        getChildren().clear();
        getChildren().add(scrollPane);
        
        // Asegurar que el StackPane tenga tamaño adecuado
        setPrefWidth(Double.MAX_VALUE);
        setPrefHeight(Double.MAX_VALUE);
    }
    
    // ====================================================================
    // 1. INICIALIZACIÓN DE COMPONENTES
    // ====================================================================
    
    private void inicializarComponentes() {
        // --- Controles generales ---
        btnSortear = new Button("🎲 Sortear Fase de Grupos");
        btnSortear.setOnAction(e -> handleSortear());
        
        salida = new TextArea();
        salida.setEditable(false);
        salida.setPrefHeight(200);
        salida.setPrefWidth(750); // Ajustar ancho para mejor visualización

        // --- Controles de Asignación de Fecha ---
        cmbPartidosFecha = new ComboBox<>();
        cmbPartidosFecha.setPrefWidth(300);
        cmbPartidosFecha.setPromptText("Seleccione partido (sin fecha)");
        txtFecha = new TextField();
        txtFecha.setPromptText("Ej: 2025-10-20 15:30 (YYYY-MM-DD HH:MM)");
        
        // --- Controles de Registro de Resultados ---
        cmbPartidosResultado = new ComboBox<>();
        cmbPartidosResultado.setPrefWidth(300);
        cmbPartidosResultado.setPromptText("Seleccione partido (programado)");
        
        // Etiquetas dinámicas iniciales
        lblGolesLocalNombre = new Label("Local:"); 
        lblGolesVisitanteNombre = new Label("Visita:"); 
        
        txtGolesLocal = new TextField();
        txtGolesLocal.setPromptText("Goles");
        txtGolesLocal.setPrefWidth(80);
        txtGolesVisitante = new TextField();
        txtGolesVisitante.setPromptText("Goles");
        txtGolesVisitante.setPrefWidth(80);
        
        // --- Controles de Edición de Resultados ---
        cmbPartidosEditados = new ComboBox<>();
        cmbPartidosEditados.setPrefWidth(300);
        cmbPartidosEditados.setPromptText("Seleccione partido (jugado)");
        
        txtGolesLocalEdit = new TextField();
        txtGolesLocalEdit.setPromptText("Goles");
        txtGolesLocalEdit.setPrefWidth(80);
        txtGolesVisitanteEdit = new TextField();
        txtGolesVisitanteEdit.setPromptText("Goles");
        txtGolesVisitanteEdit.setPrefWidth(80);
        
        lblGolesLocalEditNombre = new Label("Local:");
        lblGolesVisitanteEditNombre = new Label("Visita:");
        
        // 🆕 Inicializar botón de tarjetas
        btnGestionarTarjetas = new Button("🟨/🟥 Añadir Tarjetas a Jugadores");
        btnGestionarTarjetas.setOnAction(e -> mostrarDialogoTarjetas());
        
        // ⚽ Inicializar botón de goles
        btnRegistrarGoles = new Button("⚽ Registrar Goles por Jugador");
        btnRegistrarGoles.setOnAction(e -> mostrarDialogoGoles());
        
        // 📄 Inicializar botón de descargar PDF
        btnDescargarPDF = new Button("📄 Descargar PDF de Partidos");
        btnDescargarPDF.setOnAction(e -> descargarPDF());

        // 🟢 Listener para actualizar las etiquetas de goles
        cmbPartidosResultado.valueProperty().addListener((obs, oldPartido, newPartido) -> {
            if (newPartido != null) {
                // Muestra el nombre completo del equipo seguido de dos puntos
                lblGolesLocalNombre.setText(newPartido.getEquipoLocal().getNombre() + ":");
                lblGolesVisitanteNombre.setText(newPartido.getEquipoVisitante().getNombre() + ":");
                // Los botones se activan/desactivan según la selección
                btnGestionarTarjetas.setDisable(false);
                btnRegistrarGoles.setDisable(false);
            } else {
                lblGolesLocalNombre.setText("Local:");
                lblGolesVisitanteNombre.setText("Visita:");
                btnGestionarTarjetas.setDisable(true);
                btnRegistrarGoles.setDisable(true);
            }
            txtGolesLocal.clear(); // Limpiar TextFields al cambiar de partido
            txtGolesVisitante.clear();
        });
        
        // Listener para actualizar las etiquetas de goles en edición
        cmbPartidosEditados.valueProperty().addListener((obs, oldPartido, newPartido) -> {
            if (newPartido != null && newPartido.getJugado()) {
                lblGolesLocalEditNombre.setText(newPartido.getEquipoLocal().getNombre() + ":");
                lblGolesVisitanteEditNombre.setText(newPartido.getEquipoVisitante().getNombre() + ":");
                // Mostrar resultado actual
                txtGolesLocalEdit.setText(String.valueOf(newPartido.getGolesLocal()));
                txtGolesVisitanteEdit.setText(String.valueOf(newPartido.getGolesVisitante()));
            } else {
                lblGolesLocalEditNombre.setText("Local:");
                lblGolesVisitanteEditNombre.setText("Visita:");
                txtGolesLocalEdit.clear();
                txtGolesVisitanteEdit.clear();
            }
        });
        
        // Desactivar los botones al inicio
        btnGestionarTarjetas.setDisable(true);
        btnRegistrarGoles.setDisable(true);
    }
    
    // ====================================================================
    // 2. CREACIÓN DE PANELES DE LAYOUT
    // ====================================================================
    
    private VBox crearPanelAsignacionFecha() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #E3F2FD; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Text titulo = new Text("📅 PROGRAMACIÓN DE PARTIDOS");
        titulo.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        titulo.setFill(javafx.scene.paint.Color.web("#1976D2"));
        
        cmbPartidosFecha.setStyle("-fx-font-size: 14px;");
        txtFecha.setStyle("-fx-font-size: 14px;");
        
        Button btnProgramar = new Button("📅 Asignar Fecha");
        btnProgramar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnProgramar.setOnAction(e -> handleProgramar());
        
        Label lblInfo = new Label("Seleccione un partido sin fecha y asigne fecha/hora");
        lblInfo.setStyle("-fx-text-fill: #757575; -fx-font-size: 11px;");
        
        panel.getChildren().addAll(titulo, lblInfo, cmbPartidosFecha, txtFecha, btnProgramar);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setMinWidth(380);
        return panel;
    }

    private VBox crearPanelRegistroResultado() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #F1F8E9; -fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Text titulo = new Text("⚽ REGISTRO DE RESULTADO");
        titulo.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        titulo.setFill(javafx.scene.paint.Color.web("#388E3C"));
        
        cmbPartidosResultado.setStyle("-fx-font-size: 14px;");
        txtGolesLocal.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER;");
        txtGolesVisitante.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER;");
        
        // Panel de goles con mejor diseño
        VBox golesBox = new VBox(10);
        golesBox.setPadding(new Insets(10));
        golesBox.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; -fx-border-width: 1; -fx-border-radius: 5;");
        
        HBox golesInputBox = new HBox(15);
        golesInputBox.setAlignment(Pos.CENTER);
        
        VBox localBox = new VBox(5);
        localBox.setAlignment(Pos.CENTER);
        lblGolesLocalNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        localBox.getChildren().addAll(lblGolesLocalNombre, txtGolesLocal);
        
        Label lblVs = new Label("VS");
        lblVs.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #757575;");
        
        VBox visitanteBox = new VBox(5);
        visitanteBox.setAlignment(Pos.CENTER);
        lblGolesVisitanteNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        visitanteBox.getChildren().addAll(lblGolesVisitanteNombre, txtGolesVisitante);
        
        golesInputBox.getChildren().addAll(localBox, lblVs, visitanteBox);
        golesBox.getChildren().add(golesInputBox);
        
        // Botones de acciones
        btnRegistrarGoles.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnGestionarTarjetas.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        
        VBox accionesBox = new VBox(8, btnRegistrarGoles, btnGestionarTarjetas);
        accionesBox.setAlignment(Pos.CENTER);
        
        Button btnRegistrar = new Button("✅ Registrar Resultado");
        btnRegistrar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnRegistrar.setOnAction(e -> handleRegistrar());
        
        Label lblInfo = new Label("Seleccione un partido programado y registre el resultado");
        lblInfo.setStyle("-fx-text-fill: #757575; -fx-font-size: 11px;");
        
        panel.getChildren().addAll(titulo, lblInfo, cmbPartidosResultado, golesBox, accionesBox, btnRegistrar);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setMinWidth(380);
        return panel;
    }
    
    private VBox crearPanelEditarResultado() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #FFF3E0; -fx-border-color: #FF9800; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        Text titulo = new Text("✏️ EDITAR RESULTADO");
        titulo.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        titulo.setFill(javafx.scene.paint.Color.web("#F57C00"));
        
        cmbPartidosEditados.setStyle("-fx-font-size: 14px;");
        txtGolesLocalEdit.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER;");
        txtGolesVisitanteEdit.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER;");
        
        // Panel de goles con mejor diseño
        VBox golesBox = new VBox(10);
        golesBox.setPadding(new Insets(10));
        golesBox.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; -fx-border-width: 1; -fx-border-radius: 5;");
        
        HBox golesInputBox = new HBox(15);
        golesInputBox.setAlignment(Pos.CENTER);
        
        VBox localBox = new VBox(5);
        localBox.setAlignment(Pos.CENTER);
        lblGolesLocalEditNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        localBox.getChildren().addAll(lblGolesLocalEditNombre, txtGolesLocalEdit);
        
        Label lblVs = new Label("VS");
        lblVs.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #757575;");
        
        VBox visitanteBox = new VBox(5);
        visitanteBox.setAlignment(Pos.CENTER);
        lblGolesVisitanteEditNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        visitanteBox.getChildren().addAll(lblGolesVisitanteEditNombre, txtGolesVisitanteEdit);
        
        golesInputBox.getChildren().addAll(localBox, lblVs, visitanteBox);
        golesBox.getChildren().add(golesInputBox);
        
        Button btnEditar = new Button("✏️ Editar Resultado");
        btnEditar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnEditar.setOnAction(e -> handleEditar());
        
        Label lblInfo = new Label("Seleccione un partido jugado para editar su resultado");
        lblInfo.setStyle("-fx-text-fill: #757575; -fx-font-size: 11px;");
        
        panel.getChildren().addAll(titulo, lblInfo, cmbPartidosEditados, golesBox, btnEditar);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setMinWidth(380);
        return panel;
    }

    // ====================================================================
    // 3. MANEJADORES DE EVENTOS
    // ====================================================================

    private void handleSortear() {
        try {
            if (!torneo.isSorteoRealizado()) {
                controller.sortearFaseGrupos();
                mostrarInfo("✅ Fase de grupos sorteada y partidos generados.\n\n📄 Puedes descargar el PDF usando el botón 'Descargar PDF de Partidos'.");
                btnSortear.setDisable(true);
            } else {
                mostrarInfo("ℹ️ El sorteo ya se había realizado.");
            }
            actualizarCombos();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }
    
    private void descargarPDF() {
        try {
            if (torneo.getPartidos().isEmpty()) {
                mostrarError("No hay partidos para generar el PDF. Realice el sorteo de la fase de grupos primero.");
                return;
            }
            
            // Crear FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar PDF de Partidos");
            
            // Crear nombre de archivo sugerido con fecha y hora
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = "Partidos_FaseGrupos_" + timestamp + ".pdf";
            fileChooser.setInitialFileName(nombreArchivo);
            
            // Filtrar solo archivos PDF
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);
            
            // Obtener la ventana actual (Stage)
            Stage stage = (Stage) btnDescargarPDF.getScene().getWindow();
            
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
                    
                    com.ejemplo.util.GeneradorPDF.generarPDFPartidos(torneo, rutaCompleta);
                    mostrarInfo("✅ PDF generado exitosamente:\n" + rutaCompleta);
                } catch (Exception ex) {
                    mostrarError("Error al generar el PDF: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            mostrarError("Error: " + ex.getMessage());
        }
    }

    private void handleProgramar() {
        try {
            Partido partido = cmbPartidosFecha.getValue();
            if (partido == null) {
                mostrarError("Debe seleccionar un partido para programar");
                return;
            }

            String fecha = txtFecha.getText().trim();
            if (fecha.isEmpty()) {
                mostrarError("Debe ingresar una fecha y hora válida");
                return;
            }

            // Pasa el objeto Partido completo
            controller.programarPartido(partido, fecha); 
            
            // Asegurar que el partido esté en la lista del torneo (por si es de eliminatoria)
            if (!torneo.getPartidos().contains(partido)) {
                torneo.getPartidos().add(partido);
            }
            
            salida.setText("✅ Fecha asignada para " + partido.getEquipoLocal().getNombre() +
                            " vs " + partido.getEquipoVisitante().getNombre() + 
                            " el " + fecha + ".\n");
            txtFecha.clear();
            
            actualizarCombos(); // Mueve el partido al combo de Resultados
            
        } catch (Exception ex) {
            mostrarError("Error al asignar fecha: " + ex.getMessage());
        }
    }
    
    private void handleEditar() {
        try {
            Partido partido = cmbPartidosEditados.getValue();
            if (partido == null) {
                mostrarError("Debe seleccionar un partido jugado para editar su resultado");
                return;
            }
            
            if (!partido.getJugado()) {
                mostrarError("Este partido aún no ha sido jugado. Use la opción de 'Registrar Resultado'.");
                return;
            }
            
            int golesLocalNuevo, golesVisitanteNuevo;
            try {
                golesLocalNuevo = Integer.parseInt(txtGolesLocalEdit.getText().trim());
                golesVisitanteNuevo = Integer.parseInt(txtGolesVisitanteEdit.getText().trim());
            } catch (NumberFormatException ex) {
                mostrarError("Debe ingresar números válidos para los goles.");
                return;
            }
            
            if (golesLocalNuevo < 0 || golesVisitanteNuevo < 0) {
                mostrarError("Los goles no pueden ser negativos.");
                return;
            }
            
            // Obtener resultado anterior
            int golesLocalAnterior = partido.getGolesLocal();
            int golesVisitanteAnterior = partido.getGolesVisitante();
            
            // Si no hay cambios, no hacer nada
            if (golesLocalNuevo == golesLocalAnterior && golesVisitanteNuevo == golesVisitanteAnterior) {
                mostrarInfo("No hay cambios en el resultado.");
                return;
            }
            
            // Verificar si es partido de eliminatoria
            boolean esEliminatoria = esPartidoEliminatoria(partido);
            
            // Si el resultado es 0-0, permitir editar directamente sin diálogo de goles
            if (golesLocalNuevo == 0 && golesVisitanteNuevo == 0) {
                if (esEliminatoria) {
                    // Para eliminatoria, 0-0 también puede requerir penales
                    preguntarYEditarConPenales(partido, golesLocalNuevo, golesVisitanteNuevo, 
                                              golesLocalAnterior, golesVisitanteAnterior);
                } else {
                    // Revertir estadísticas anteriores
                    torneo.revertirEstadisticasEquipos(partido, golesLocalAnterior, golesVisitanteAnterior);
                    
                    // Limpiar goles por jugador y penales
                    partido.getGolesPorJugador().clear();
                    partido.limpiarPenales();
                    
                    // Actualizar resultado del partido
                    partido.setGolesLocal(golesLocalNuevo);
                    partido.setGolesVisitante(golesVisitanteNuevo);
                    
                    // Actualizar estadísticas con nuevo resultado
                    torneo.actualizarEstadisticasEquipos(partido, golesLocalNuevo, golesVisitanteNuevo);
                    
                    salida.setText("✅ Resultado editado: " + 
                                  partido.getEquipoLocal().getNombre() + " " + golesLocalNuevo + 
                                  " - " + golesVisitanteNuevo + " " + partido.getEquipoVisitante().getNombre() + 
                                  ".\nLa tabla de posiciones se ha actualizado.");
                    txtGolesLocalEdit.clear();
                    txtGolesVisitanteEdit.clear();
                    actualizarCombos();
                }
            }
            // Si hay goles, abrir diálogo para asignar goles a jugadores (igual que en registro)
            else {
                mostrarDialogoGolesIntegradoEdicion(partido, golesLocalNuevo, golesVisitanteNuevo, 
                                                    golesLocalAnterior, golesVisitanteAnterior);
            }
            
        } catch (Exception ex) {
            mostrarError("Error al editar resultado: " + ex.getMessage());
        }
    }
    
    private void preguntarYEditarConPenales(Partido partido, int golesLocalNuevo, int golesVisitanteNuevo,
                                           int golesLocalAnterior, int golesVisitanteAnterior) {
        Dialog<PenalesResult> dialog = new Dialog<>();
        dialog.setTitle("🏆 Editar Partido Eliminatorio - Definición por Penales");
        dialog.setHeaderText(String.format("El partido terminó %d - %d (EMPATE)\nIngrese los penales:", 
            golesLocalNuevo, golesVisitanteNuevo));
        
        ButtonType registrarButtonType = new ButtonType("✅ Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registrarButtonType, ButtonType.CANCEL);
        
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        
        Label lblInfo = new Label("Ingrese los penales de cada equipo:");
        lblInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));
        
        Label lblLocal = new Label(partido.getEquipoLocal().getNombre() + ":");
        lblLocal.setStyle("-fx-font-weight: bold;");
        TextField txtPenalesLocal = new TextField();
        if (partido.getPenalesLocal() != null) {
            txtPenalesLocal.setText(String.valueOf(partido.getPenalesLocal()));
        }
        txtPenalesLocal.setPromptText("Ej: 4");
        txtPenalesLocal.setPrefWidth(100);
        
        Label lblVisitante = new Label(partido.getEquipoVisitante().getNombre() + ":");
        lblVisitante.setStyle("-fx-font-weight: bold;");
        TextField txtPenalesVisitante = new TextField();
        if (partido.getPenalesVisitante() != null) {
            txtPenalesVisitante.setText(String.valueOf(partido.getPenalesVisitante()));
        }
        txtPenalesVisitante.setPromptText("Ej: 3");
        txtPenalesVisitante.setPrefWidth(100);
        
        grid.add(lblLocal, 0, 0);
        grid.add(txtPenalesLocal, 1, 0);
        grid.add(lblVisitante, 0, 1);
        grid.add(txtPenalesVisitante, 1, 1);
        
        Label lblAdvertencia = new Label("⚠️ Los penales deben ser diferentes. Debe haber un ganador.");
        lblAdvertencia.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 11px;");
        
        contentBox.getChildren().addAll(lblInfo, grid, lblAdvertencia);
        dialog.getDialogPane().setContent(contentBox);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registrarButtonType) {
                try {
                    int penalesLocal = Integer.parseInt(txtPenalesLocal.getText().trim());
                    int penalesVisitante = Integer.parseInt(txtPenalesVisitante.getText().trim());
                    
                    if (penalesLocal < 0 || penalesVisitante < 0) {
                        mostrarError("Los penales no pueden ser negativos");
                        return null;
                    }
                    
                    if (penalesLocal == penalesVisitante) {
                        mostrarError("Los penales no pueden ser iguales. Debe haber un ganador.");
                        return null;
                    }
                    
                    return new PenalesResult(penalesLocal, penalesVisitante, true);
                } catch (NumberFormatException e) {
                    mostrarError("Debe ingresar números válidos para los penales");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if (result != null && result.conPenales) {
                // Revertir estadísticas anteriores
                torneo.revertirEstadisticasEquipos(partido, golesLocalAnterior, golesVisitanteAnterior);
                
                // Determinar ganador
                Equipo ganador = result.penalesLocal > result.penalesVisitante ? 
                    partido.getEquipoLocal() : partido.getEquipoVisitante();
                
                // Actualizar resultado del partido
                partido.setGolesLocal(golesLocalNuevo);
                partido.setGolesVisitante(golesVisitanteNuevo);
                partido.setResultadoPenales(result.penalesLocal, result.penalesVisitante, ganador);
                
                // Actualizar estadísticas con nuevo resultado
                torneo.actualizarEstadisticasEquipos(partido, golesLocalNuevo, golesVisitanteNuevo);
                
                String mensaje = "✅ Resultado editado: " + 
                    partido.getEquipoLocal().getNombre() + " " + golesLocalNuevo + 
                    " - " + golesVisitanteNuevo + " " + partido.getEquipoVisitante().getNombre();
                
                mensaje += String.format("\n🏆 Ganador por penales: %s (%d-%d)", 
                    ganador.getNombre(), result.penalesLocal, result.penalesVisitante);
                
                salida.setText(mensaje);
                txtGolesLocalEdit.clear();
                txtGolesVisitanteEdit.clear();
                actualizarCombos();
            }
        });
    }
    
    private void handleRegistrar() {
        try {
            Partido partido = cmbPartidosResultado.getValue();
            if (partido == null) {
                mostrarError("Debe seleccionar un partido programado para registrar el resultado");
                return;
            }

            // Obtener goles de los TextFields
            int golesLocal = Integer.parseInt(txtGolesLocal.getText().trim());
            int golesVisitante = Integer.parseInt(txtGolesVisitante.getText().trim());
            
            if (golesLocal < 0 || golesVisitante < 0) {
                 mostrarError("Los goles deben ser valores positivos.");
                 return;
            }

            // Verificar si es partido de eliminatoria
            boolean esEliminatoria = esPartidoEliminatoria(partido);
            
            // Si es partido eliminatorio y hay empate, preguntar por penales
            if (esEliminatoria && golesLocal == golesVisitante) {
                preguntarYRegistrarConPenales(partido, golesLocal, golesVisitante);
                return;
            }
            
            // Verificar si ya se registraron goles por jugador
            int golesRegistradosLocal = partido.getGolesLocal();
            int golesRegistradosVisitante = partido.getGolesVisitante();
            
            // Si el resultado es 0-0, registrar directamente sin abrir diálogo de goles
            if (golesLocal == 0 && golesVisitante == 0) {
                if (esEliminatoria) {
                    // Para eliminatoria, 0-0 también puede requerir penales
                    preguntarYRegistrarConPenales(partido, golesLocal, golesVisitante);
                } else {
                    controller.registrarResultado(partido, golesLocal, golesVisitante);
                    salida.setText("✅ Resultado registrado: " + 
                                    partido.getEquipoLocal().getNombre() + " " + golesLocal + 
                                    " - " + golesVisitante + " " + partido.getEquipoVisitante().getNombre() + 
                                    ".\nLa tabla de posiciones se ha actualizado.");
                    txtGolesLocal.clear();
                    txtGolesVisitante.clear();
                    actualizarCombos();
                }
            }
            // Si no hay goles registrados por jugador y hay goles, abrir diálogo primero
            else if (golesRegistradosLocal == 0 && golesRegistradosVisitante == 0) {
                // Si es eliminatoria y hay empate, después de asignar goles también preguntará por penales
                mostrarDialogoGolesIntegrado(partido, golesLocal, golesVisitante);
            } else {
                // Si ya hay goles registrados, verificar que coincidan y registrar el resultado final
                if (golesRegistradosLocal != golesLocal || golesRegistradosVisitante != golesVisitante) {
                    mostrarError(String.format("Los goles registrados por jugador (%d-%d) no coinciden con el total ingresado (%d-%d). " +
                                             "Por favor, ajusta los goles.",
                                             golesRegistradosLocal, golesRegistradosVisitante,
                                             golesLocal, golesVisitante));
                    return;
                }
                
                // Registrar el resultado final del partido
                if (esEliminatoria) {
                    // Para eliminatoria, verificar si necesita penales
                    registrarResultadoEliminatoria(partido, golesLocal, golesVisitante);
                } else {
                    controller.registrarResultado(partido, golesLocal, golesVisitante);
                    salida.setText("✅ Resultado registrado: " + 
                                    partido.getEquipoLocal().getNombre() + " " + golesLocal + 
                                    " - " + golesVisitante + " " + partido.getEquipoVisitante().getNombre() + 
                                    ".\nLa tabla de posiciones se ha actualizado.");
                    txtGolesLocal.clear();
                    txtGolesVisitante.clear();
                    actualizarCombos();
                }
            }
            
        } catch (NumberFormatException ex) {
            mostrarError("Debe ingresar números válidos para los goles.");
        } catch (Exception ex) {
            mostrarError("Error al registrar resultado: " + ex.getMessage());
        }
    }
    
    /**
     * Pregunta si el partido se definió por penales y registra el resultado
     */
    private void preguntarYRegistrarConPenales(Partido partido, int golesLocal, int golesVisitante) {
        Dialog<PenalesResult> dialog = new Dialog<>();
        dialog.setTitle("🏆 Partido Eliminatorio - Definición por Penales");
        dialog.setHeaderText(String.format("El partido terminó %d - %d (EMPATE)\nEn partidos eliminatorios, los empates se definen por penales.", 
            golesLocal, golesVisitante));
        
        ButtonType registrarButtonType = new ButtonType("✅ Registrar con Penales", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registrarButtonType, ButtonType.CANCEL);
        
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        
        Label lblInfo = new Label("Ingrese los penales de cada equipo:");
        lblInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));
        
        Label lblLocal = new Label(partido.getEquipoLocal().getNombre() + ":");
        lblLocal.setStyle("-fx-font-weight: bold;");
        TextField txtPenalesLocal = new TextField();
        txtPenalesLocal.setPromptText("Ej: 4");
        txtPenalesLocal.setPrefWidth(100);
        
        Label lblVisitante = new Label(partido.getEquipoVisitante().getNombre() + ":");
        lblVisitante.setStyle("-fx-font-weight: bold;");
        TextField txtPenalesVisitante = new TextField();
        txtPenalesVisitante.setPromptText("Ej: 3");
        txtPenalesVisitante.setPrefWidth(100);
        
        grid.add(lblLocal, 0, 0);
        grid.add(txtPenalesLocal, 1, 0);
        grid.add(lblVisitante, 0, 1);
        grid.add(txtPenalesVisitante, 1, 1);
        
        Label lblAdvertencia = new Label("⚠️ Los penales deben ser diferentes. Debe haber un ganador.");
        lblAdvertencia.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 11px;");
        
        contentBox.getChildren().addAll(lblInfo, grid, lblAdvertencia);
        dialog.getDialogPane().setContent(contentBox);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registrarButtonType) {
                try {
                    int penalesLocal = Integer.parseInt(txtPenalesLocal.getText().trim());
                    int penalesVisitante = Integer.parseInt(txtPenalesVisitante.getText().trim());
                    
                    if (penalesLocal < 0 || penalesVisitante < 0) {
                        mostrarError("Los penales no pueden ser negativos");
                        return null;
                    }
                    
                    if (penalesLocal == penalesVisitante) {
                        mostrarError("Los penales no pueden ser iguales. Debe haber un ganador.");
                        return null;
                    }
                    
                    return new PenalesResult(penalesLocal, penalesVisitante, true);
                } catch (NumberFormatException e) {
                    mostrarError("Debe ingresar números válidos para los penales");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if (result != null && result.conPenales) {
                // Determinar ganador
                Equipo ganador = result.penalesLocal > result.penalesVisitante ? 
                    partido.getEquipoLocal() : partido.getEquipoVisitante();
                
                // Registrar penales
                partido.setResultadoPenales(result.penalesLocal, result.penalesVisitante, ganador);
                
                // Registrar resultado del partido
                partido.registrarResultados(golesLocal, golesVisitante);
                
                // Avanzar en eliminatoria si es necesario
                String mensaje = "✅ Resultado registrado: " + 
                    partido.getEquipoLocal().getNombre() + " " + golesLocal + 
                    " - " + golesVisitante + " " + partido.getEquipoVisitante().getNombre();
                
                mensaje += String.format("\n🏆 Ganador por penales: %s (%d-%d)", 
                    ganador.getNombre(), result.penalesLocal, result.penalesVisitante);
                
                if (esPartidoEliminatoria(partido)) {
                    // Guardar estado antes de avanzar
                    int semifinalesAntes = torneo.getEliminatoria().getPartidosSemifinal().size();
                    boolean finalAntes = torneo.getEliminatoria().getPartidoFinal() != null;
                    
                    torneo.getEliminatoria().registrarResultadoYAvanzar(partido, golesLocal, golesVisitante);
                    
                    // Verificar si se generaron nuevas fases
                    int semifinalesDespues = torneo.getEliminatoria().getPartidosSemifinal().size();
                    boolean finalDespues = torneo.getEliminatoria().getPartidoFinal() != null;
                    
                    // Agregar nuevos partidos a la lista del torneo
                    if (semifinalesDespues > semifinalesAntes) {
                        torneo.getPartidos().addAll(torneo.getEliminatoria().getPartidosSemifinal());
                        mensaje += "\n\n⚔️ ¡SEMIFINALES GENERADAS AUTOMÁTICAMENTE!";
                        for (Partido semi : torneo.getEliminatoria().getPartidosSemifinal()) {
                            mensaje += "\n  • " + semi.getEquipoLocal().getNombre() + " vs " + semi.getEquipoVisitante().getNombre();
                        }
                    }
                    
                    if (finalDespues && !finalAntes) {
                        if (torneo.getEliminatoria().getPartidoFinal() != null) {
                            torneo.getPartidos().add(torneo.getEliminatoria().getPartidoFinal());
                            mensaje += "\n\n🏆 ¡FINAL GENERADA AUTOMÁTICAMENTE!";
                            mensaje += "\n  • " + torneo.getEliminatoria().getPartidoFinal().getEquipoLocal().getNombre() + 
                                      " vs " + torneo.getEliminatoria().getPartidoFinal().getEquipoVisitante().getNombre();
                        }
                        if (torneo.getEliminatoria().getPartidoTercerPuesto() != null) {
                            torneo.getPartidos().add(torneo.getEliminatoria().getPartidoTercerPuesto());
                            mensaje += "\n\n🥉 ¡TERCER PUESTO GENERADO AUTOMÁTICAMENTE!";
                            mensaje += "\n  • " + torneo.getEliminatoria().getPartidoTercerPuesto().getEquipoLocal().getNombre() + 
                                      " vs " + torneo.getEliminatoria().getPartidoTercerPuesto().getEquipoVisitante().getNombre();
                        }
                    }
                }
                
                salida.setText(mensaje);
                txtGolesLocal.clear();
                txtGolesVisitante.clear();
                actualizarCombos();
            }
        });
    }
    
    /**
     * Registra resultado de partido eliminatorio (puede requerir penales si hay empate)
     */
    private void registrarResultadoEliminatoria(Partido partido, int golesLocal, int golesVisitante) {
        if (golesLocal == golesVisitante && partido.getPenalesLocal() == null) {
            // Si hay empate y no hay penales registrados, preguntar
            preguntarYRegistrarConPenales(partido, golesLocal, golesVisitante);
        } else {
            // Registrar normalmente
            partido.registrarResultados(golesLocal, golesVisitante);
            
            // Guardar estado antes de avanzar
            int semifinalesAntes = torneo.getEliminatoria().getPartidosSemifinal().size();
            boolean finalAntes = torneo.getEliminatoria().getPartidoFinal() != null;
            
            torneo.getEliminatoria().registrarResultadoYAvanzar(partido, golesLocal, golesVisitante);
            
            // Verificar si se generaron nuevas fases
            int semifinalesDespues = torneo.getEliminatoria().getPartidosSemifinal().size();
            boolean finalDespues = torneo.getEliminatoria().getPartidoFinal() != null;
            
            String mensaje = "✅ Resultado registrado: " + 
                partido.getEquipoLocal().getNombre() + " " + golesLocal + 
                " - " + golesVisitante + " " + partido.getEquipoVisitante().getNombre();
            
            if (partido.getPenalesLocal() != null) {
                mensaje += String.format("\n🏆 Ganador por penales: %s (%d-%d)", 
                    partido.getGanadorPorPenales().getNombre(), 
                    partido.getPenalesLocal(), partido.getPenalesVisitante());
            }
            
            // Agregar nuevos partidos a la lista del torneo y mostrar mensajes
            // Asegurar que todos los partidos de eliminatoria estén en la lista del torneo
            if (torneo.getEliminatoria() != null) {
                List<Partido> partidosEliminatoria = torneo.getEliminatoria().getTodosLosPartidos();
                for (Partido partidoElim : partidosEliminatoria) {
                    if (!torneo.getPartidos().contains(partidoElim)) {
                        torneo.getPartidos().add(partidoElim);
                    }
                }
            }
            
            if (semifinalesDespues > semifinalesAntes) {
                mensaje += "\n\n⚔️ ¡SEMIFINALES GENERADAS AUTOMÁTICAMENTE!";
                for (Partido semi : torneo.getEliminatoria().getPartidosSemifinal()) {
                    mensaje += "\n  • " + semi.getEquipoLocal().getNombre() + " vs " + semi.getEquipoVisitante().getNombre();
                }
            }
            
            if (finalDespues && !finalAntes) {
                if (torneo.getEliminatoria().getPartidoFinal() != null) {
                    mensaje += "\n\n🏆 ¡FINAL GENERADA AUTOMÁTICAMENTE!";
                    mensaje += "\n  • " + torneo.getEliminatoria().getPartidoFinal().getEquipoLocal().getNombre() + 
                              " vs " + torneo.getEliminatoria().getPartidoFinal().getEquipoVisitante().getNombre();
                }
                if (torneo.getEliminatoria().getPartidoTercerPuesto() != null) {
                    mensaje += "\n\n🥉 ¡TERCER PUESTO GENERADO AUTOMÁTICAMENTE!";
                    mensaje += "\n  • " + torneo.getEliminatoria().getPartidoTercerPuesto().getEquipoLocal().getNombre() + 
                              " vs " + torneo.getEliminatoria().getPartidoTercerPuesto().getEquipoVisitante().getNombre();
                }
            }
            
            salida.setText(mensaje);
            txtGolesLocal.clear();
            txtGolesVisitante.clear();
            actualizarCombos();
        }
    }
    
    /**
     * Clase auxiliar para manejar resultado de penales
     */
    private static class PenalesResult {
        int penalesLocal;
        int penalesVisitante;
        boolean conPenales;
        
        PenalesResult(int penalesLocal, int penalesVisitante, boolean conPenales) {
            this.penalesLocal = penalesLocal;
            this.penalesVisitante = penalesVisitante;
            this.conPenales = conPenales;
        }
    }
    
    // ====================================================================
    // 5. DIÁLOGO DE REGISTRO DE GOLES POR JUGADOR
    // ====================================================================
    
    /**
     * Versión integrada del diálogo de goles que se abre después de registrar el resultado.
     * Permite distribuir los goles totales entre los jugadores.
     * Los goles se asignan automáticamente al hacer clic en "+1".
     */
    private void mostrarDialogoGolesIntegrado(Partido partido, int golesLocalTotal, int golesVisitanteTotal) {
        Equipo equipoLocal = partido.getEquipoLocal();
        Equipo equipoVisitante = partido.getEquipoVisitante();
        
        // Resetear los goles del partido y limpiar goles por jugador para empezar desde cero
        partido.setGolesLocal(0);
        partido.setGolesVisitante(0);
        partido.limpiarGolesPorJugador();
        
        // Crear el diálogo
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("⚽ Registrar Goles por Jugador");
        dialog.setHeaderText(String.format("Distribuye los goles: %s %d - %d %s\nHaz clic en los jugadores para asignar goles",
            equipoLocal.getNombre(), golesLocalTotal, golesVisitanteTotal, equipoVisitante.getNombre()));
        
        ButtonType registrarGolesButtonType = new ButtonType("✅ Registrar Goles", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelarButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(registrarGolesButtonType, cancelarButtonType);
        
        // Contenedor principal
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        
        // Crear labels para el resumen (se pasarán a los paneles para actualización automática)
        Label lblTotalLocal = new Label();
        Label lblTotalVisitante = new Label();
        Label lblMensaje = new Label();
        
        // Crear paneles para cada equipo (pasar referencias a los labels para actualización automática)
        VBox panelLocal = crearPanelGolesEquipoIntegrado(equipoLocal, partido, "🔵 " + equipoLocal.getNombre(),
                                                         lblTotalLocal, lblTotalVisitante, lblMensaje,
                                                         golesLocalTotal, golesVisitanteTotal, dialog);
        VBox panelVisitante = crearPanelGolesEquipoIntegrado(equipoVisitante, partido, "🔴 " + equipoVisitante.getNombre(),
                                                            lblTotalLocal, lblTotalVisitante, lblMensaje,
                                                            golesLocalTotal, golesVisitanteTotal, dialog);
        
        HBox equiposBox = new HBox(20, panelLocal, panelVisitante);
        equiposBox.setAlignment(Pos.CENTER);
        
        // Inicializar resumen y totales
        actualizarResumenGolesIntegrado(partido, golesLocalTotal, golesVisitanteTotal, 
                                       lblTotalLocal, lblTotalVisitante, lblMensaje);
        
        VBox resumenBox = new VBox(5, 
            new HBox(10, lblTotalLocal, new Label("vs"), lblTotalVisitante),
            lblMensaje);
        resumenBox.setAlignment(Pos.CENTER);
        
        contentBox.getChildren().addAll(equiposBox, resumenBox);
        dialog.getDialogPane().setContent(contentBox);
        
        // Configurar resultado - registrar los goles por jugador cuando se hace clic en "Registrar Goles"
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registrarGolesButtonType) {
                // Verificar si se completaron todos los goles
                int golesRegistradosLocal = partido.getGolesLocal();
                int golesRegistradosVisitante = partido.getGolesVisitante();
                
                if (golesRegistradosLocal != golesLocalTotal || golesRegistradosVisitante != golesVisitanteTotal) {
                    mostrarError(String.format("Los goles registrados (%d-%d) no coinciden con el total (%d-%d). " +
                                             "Asigna todos los goles antes de registrar.",
                                             golesRegistradosLocal, golesRegistradosVisitante,
                                             golesLocalTotal, golesVisitanteTotal));
                    return null; // Retornar null previene el cierre del diálogo
                }
                
                // Los goles por jugador ya están registrados
                // Si es partido eliminatorio y hay empate, preguntar por penales
                boolean esEliminatoria = esPartidoEliminatoria(partido);
                if (esEliminatoria && golesLocalTotal == golesVisitanteTotal) {
                    // Cerrar este diálogo primero
                    return true; // Cerrar diálogo de goles
                }
                
                // Si no es empate o no es eliminatoria, registrar normalmente
                try {
                    if (esEliminatoria) {
                        // Para eliminatoria sin empate, usar el método específico
                        registrarResultadoEliminatoria(partido, golesLocalTotal, golesVisitanteTotal);
                    } else {
                        controller.registrarResultado(partido, golesLocalTotal, golesVisitanteTotal);
                    }
                    mostrarResumenGolesRegistrados(partido, golesLocalTotal, golesVisitanteTotal);
                    return true; // Retornar true cierra el diálogo automáticamente
                } catch (Exception e) {
                    mostrarError("Error al registrar el resultado: " + e.getMessage());
                    return null; // Prevenir cierre si hay error
                }
            }
            return false;
        });
        
        // Mostrar diálogo - se cierra automáticamente cuando se retorna true
        dialog.showAndWait().ifPresent(registrado -> {
            if (registrado) {
                // Verificar si es eliminatoria y hay empate para preguntar por penales
                boolean esEliminatoria = esPartidoEliminatoria(partido);
                if (esEliminatoria && golesLocalTotal == golesVisitanteTotal) {
                    // Preguntar por penales
                    preguntarYRegistrarConPenales(partido, golesLocalTotal, golesVisitanteTotal);
                } else {
                    // Actualizar los campos de goles en la interfaz principal
                    txtGolesLocal.setText(String.valueOf(partido.getGolesLocal()));
                    txtGolesVisitante.setText(String.valueOf(partido.getGolesVisitante()));
                    
                    // Limpiar campos y actualizar combos
                    txtGolesLocal.clear();
                    txtGolesVisitante.clear();
                    actualizarCombos();
                    
                    // Mostrar mensaje de éxito
                    salida.setText("✅ Resultado registrado: " + 
                                    partido.getEquipoLocal().getNombre() + " " + golesLocalTotal + 
                                    " - " + golesVisitanteTotal + " " + partido.getEquipoVisitante().getNombre() + 
                                    ".\nLa tabla de posiciones se ha actualizado.");
                }
            }
        });
    }
    
    /**
     * Diálogo de goles integrado para edición de resultados (similar a registro pero para editar)
     */
    private void mostrarDialogoGolesIntegradoEdicion(Partido partido, int golesLocalNuevo, int golesVisitanteNuevo,
                                                     int golesLocalAnterior, int golesVisitanteAnterior) {
        Equipo equipoLocal = partido.getEquipoLocal();
        Equipo equipoVisitante = partido.getEquipoVisitante();
        
        // Guardar goles actuales por jugador antes de limpiar
        Map<String, Integer> golesPorJugadorAnteriores = new HashMap<>(partido.getGolesPorJugador());
        
        // Resetear los goles del partido y limpiar goles por jugador para empezar desde cero
        partido.setGolesLocal(0);
        partido.setGolesVisitante(0);
        partido.limpiarGolesPorJugador();
        
        // Crear el diálogo
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("✏️ Editar Goles por Jugador");
        dialog.setHeaderText(String.format("Distribuye los goles: %s %d - %d %s\nHaz clic en los jugadores para asignar goles",
            equipoLocal.getNombre(), golesLocalNuevo, golesVisitanteNuevo, equipoVisitante.getNombre()));
        
        ButtonType registrarGolesButtonType = new ButtonType("✅ Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelarButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(registrarGolesButtonType, cancelarButtonType);
        
        // Contenedor principal
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        
        // Crear labels para el resumen (se pasarán a los paneles para actualización automática)
        Label lblTotalLocal = new Label();
        Label lblTotalVisitante = new Label();
        Label lblMensaje = new Label();
        
        // Crear paneles para cada equipo (pasar referencias a los labels para actualización automática)
        VBox panelLocal = crearPanelGolesEquipoIntegrado(equipoLocal, partido, "🔵 " + equipoLocal.getNombre(),
                                                         lblTotalLocal, lblTotalVisitante, lblMensaje,
                                                         golesLocalNuevo, golesVisitanteNuevo, dialog);
        VBox panelVisitante = crearPanelGolesEquipoIntegrado(equipoVisitante, partido, "🔴 " + equipoVisitante.getNombre(),
                                                            lblTotalLocal, lblTotalVisitante, lblMensaje,
                                                            golesLocalNuevo, golesVisitanteNuevo, dialog);
        
        HBox equiposBox = new HBox(20, panelLocal, panelVisitante);
        equiposBox.setAlignment(Pos.CENTER);
        
        // Inicializar resumen y totales
        actualizarResumenGolesIntegrado(partido, golesLocalNuevo, golesVisitanteNuevo, 
                                       lblTotalLocal, lblTotalVisitante, lblMensaje);
        
        VBox resumenBox = new VBox(5, 
            new HBox(10, lblTotalLocal, new Label("vs"), lblTotalVisitante),
            lblMensaje);
        resumenBox.setAlignment(Pos.CENTER);
        
        contentBox.getChildren().addAll(equiposBox, resumenBox);
        dialog.getDialogPane().setContent(contentBox);
        
        // Configurar resultado - registrar los goles por jugador cuando se hace clic en "Guardar Cambios"
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registrarGolesButtonType) {
                // Verificar si se completaron todos los goles
                int golesRegistradosLocal = partido.getGolesLocal();
                int golesRegistradosVisitante = partido.getGolesVisitante();
                
                if (golesRegistradosLocal != golesLocalNuevo || golesRegistradosVisitante != golesVisitanteNuevo) {
                    mostrarError(String.format("Los goles registrados (%d-%d) no coinciden con el total (%d-%d). " +
                                             "Asigna todos los goles antes de guardar.",
                                             golesRegistradosLocal, golesRegistradosVisitante,
                                             golesLocalNuevo, golesVisitanteNuevo));
                    return null; // Retornar null previene el cierre del diálogo
                }
                
                // Los goles por jugador ya están registrados
                // Verificar si es partido eliminatorio y hay empate
                boolean esEliminatoria = esPartidoEliminatoria(partido);
                if (esEliminatoria && golesLocalNuevo == golesVisitanteNuevo) {
                    // Cerrar este diálogo primero
                    return true; // Cerrar diálogo de goles
                }
                
                // Si no es empate o no es eliminatoria, aplicar cambios
                try {
                    aplicarEdicionResultado(partido, golesLocalNuevo, golesVisitanteNuevo, 
                                          golesLocalAnterior, golesVisitanteAnterior, esEliminatoria);
                    return true; // Retornar true cierra el diálogo automáticamente
                } catch (Exception e) {
                    mostrarError("Error al editar el resultado: " + e.getMessage());
                    return null; // Prevenir cierre si hay error
                }
            }
            return false;
        });
        
        // Mostrar diálogo - se cierra automáticamente cuando se retorna true
        dialog.showAndWait().ifPresent(guardado -> {
            if (guardado) {
                // Verificar si es eliminatoria y hay empate para preguntar por penales
                boolean esEliminatoria = esPartidoEliminatoria(partido);
                if (esEliminatoria && golesLocalNuevo == golesVisitanteNuevo) {
                    // Preguntar por penales
                    preguntarYEditarConPenales(partido, golesLocalNuevo, golesVisitanteNuevo, 
                                              golesLocalAnterior, golesVisitanteAnterior);
                } else {
                    // Actualizar los campos de goles en la interfaz principal
                    txtGolesLocalEdit.setText(String.valueOf(partido.getGolesLocal()));
                    txtGolesVisitanteEdit.setText(String.valueOf(partido.getGolesVisitante()));
                    
                    // Limpiar campos y actualizar combos
                    txtGolesLocalEdit.clear();
                    txtGolesVisitanteEdit.clear();
                    actualizarCombos();
                }
            } else {
                // Si se canceló, restaurar goles por jugador anteriores
                partido.getGolesPorJugador().clear();
                partido.getGolesPorJugador().putAll(golesPorJugadorAnteriores);
                partido.setGolesLocal(golesLocalAnterior);
                partido.setGolesVisitante(golesVisitanteAnterior);
            }
        });
    }
    
    /**
     * Aplica los cambios de edición al resultado del partido
     */
    private void aplicarEdicionResultado(Partido partido, int golesLocalNuevo, int golesVisitanteNuevo,
                                         int golesLocalAnterior, int golesVisitanteAnterior, boolean esEliminatoria) {
        // Revertir estadísticas anteriores
        torneo.revertirEstadisticasEquipos(partido, golesLocalAnterior, golesVisitanteAnterior);
        
        // Limpiar penales si ya no hay empate
        if (!esEliminatoria || golesLocalNuevo != golesVisitanteNuevo) {
            partido.limpiarPenales();
        }
        
        // Actualizar resultado del partido (los goles por jugador ya están asignados en el diálogo)
        partido.setGolesLocal(golesLocalNuevo);
        partido.setGolesVisitante(golesVisitanteNuevo);
        
        // Actualizar estadísticas con nuevo resultado
        torneo.actualizarEstadisticasEquipos(partido, golesLocalNuevo, golesVisitanteNuevo);
        
        salida.setText("✅ Resultado editado: " + 
                      partido.getEquipoLocal().getNombre() + " " + golesLocalNuevo + 
                      " - " + golesVisitanteNuevo + " " + partido.getEquipoVisitante().getNombre() + 
                      ".\nLa tabla de posiciones se ha actualizado.");
    }
    
    /**
     * Versión original del diálogo (mantener para el botón separado si se necesita).
     */
    private void mostrarDialogoGoles() {
        Partido partido = cmbPartidosResultado.getValue();
        if (partido == null) {
            mostrarError("Debe seleccionar primero un partido para registrar goles.");
            return;
        }
        
        Equipo equipoLocal = partido.getEquipoLocal();
        Equipo equipoVisitante = partido.getEquipoVisitante();
        
        // Crear el diálogo
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("⚽ Registrar Goles por Jugador");
        dialog.setHeaderText("Haz clic en los jugadores para agregar goles (o usa los botones +/-)");
        
        ButtonType cerrarButtonType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(cerrarButtonType);
        
        // Contenedor principal
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        
        // Mostrar resumen de goles actuales
        Label lblResumen = new Label();
        Label lblTotalGoles = new Label();
        actualizarResumenGoles(partido, lblResumen, lblTotalGoles);
        
        // Crear paneles para cada equipo con sus jugadores (pasar labels para actualización automática)
        VBox panelLocal = crearPanelGolesEquipo(equipoLocal, partido, "🔵 " + equipoLocal.getNombre(), lblResumen, lblTotalGoles);
        VBox panelVisitante = crearPanelGolesEquipo(equipoVisitante, partido, "🔴 " + equipoVisitante.getNombre(), lblResumen, lblTotalGoles);
        
        HBox equiposBox = new HBox(20, panelLocal, panelVisitante);
        equiposBox.setAlignment(Pos.CENTER);
        
        // Botón para actualizar el resumen manualmente
        Button btnActualizar = new Button("🔄 Actualizar Resumen");
        btnActualizar.setOnAction(e -> actualizarResumenGoles(partido, lblResumen, lblTotalGoles));
        
        VBox resumenBox = new VBox(5, lblTotalGoles, lblResumen, btnActualizar);
        resumenBox.setAlignment(Pos.CENTER);
        
        contentBox.getChildren().addAll(equiposBox, resumenBox);
        dialog.getDialogPane().setContent(contentBox);
        
        // Mostrar diálogo
        dialog.showAndWait();
        
        // Actualizar los campos de goles en la interfaz principal
        txtGolesLocal.setText(String.valueOf(partido.getGolesLocal()));
        txtGolesVisitante.setText(String.valueOf(partido.getGolesVisitante()));
    }
    
    /**
     * Crea un panel para un equipo con botones para agregar goles a cada jugador (versión integrada).
     * Los goles se asignan automáticamente al hacer clic.
     */
    private VBox crearPanelGolesEquipoIntegrado(Equipo equipo, Partido partido, String titulo, 
                                                Label lblTotalLocal, Label lblTotalVisitante, 
                                                Label lblMensaje, int golesLocalTotal, int golesVisitanteTotal,
                                                Dialog<Boolean> dialog) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");
        
        Text tituloText = new Text(titulo);
        tituloText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        panel.getChildren().add(tituloText);
        
        List<Jugador> jugadores = equipo.getJugadores();
        if (jugadores.isEmpty()) {
            panel.getChildren().add(new Label("Sin jugadores registrados"));
            return panel;
        }
        
        // Crear una fila para cada jugador
        for (Jugador jugador : jugadores) {
            HBox filaJugador = new HBox(10);
            filaJugador.setAlignment(Pos.CENTER_LEFT);
            
            // Nombre del jugador
            Label lblJugador = new Label(String.format("[%d] %s (%s)", 
                jugador.getNumero(), jugador.getNombre(), jugador.getPosicion()));
            lblJugador.setPrefWidth(180);
            
            // Botón para agregar gol
            Button btnAgregarGol = new Button("+1");
            btnAgregarGol.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            btnAgregarGol.setPrefWidth(50);
            
            // Botón para quitar gol
            Button btnQuitarGol = new Button("-1");
            btnQuitarGol.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
            btnQuitarGol.setPrefWidth(50);
            
            // Contador de goles del jugador en este partido
            Label lblGoles = new Label("Goles: " + obtenerGolesJugador(partido, jugador));
            lblGoles.setPrefWidth(80);
            
            // Acción del botón: agregar gol, actualizar contador y verificar si se completó
            btnAgregarGol.setOnAction(e -> {
                // Agregar el gol (esto actualiza automáticamente los goles del partido y del jugador)
                partido.agregarGol(equipo, jugador);
                
                // Actualizar el contador de este jugador
                lblGoles.setText("Goles: " + obtenerGolesJugador(partido, jugador));
                
                // Actualizar los totales y el mensaje
                actualizarResumenGolesIntegrado(partido, golesLocalTotal, golesVisitanteTotal,
                                               lblTotalLocal, lblTotalVisitante, lblMensaje);
                
                // Verificar si se completaron todos los goles y registrar automáticamente
                int golesRegistradosLocal = partido.getGolesLocal();
                int golesRegistradosVisitante = partido.getGolesVisitante();
                
                // Los goles se registran automáticamente al hacer clic, pero no se marca el partido como jugado todavía
            });
            
            // Acción del botón: quitar gol, actualizar contador y verificar si se completó
            btnQuitarGol.setOnAction(e -> {
                // Quitar el gol (esto actualiza automáticamente los goles del partido y del jugador)
                partido.quitarGol(equipo, jugador);
                
                // Actualizar el contador de este jugador
                lblGoles.setText("Goles: " + obtenerGolesJugador(partido, jugador));
                
                // Actualizar los totales y el mensaje
                actualizarResumenGolesIntegrado(partido, golesLocalTotal, golesVisitanteTotal,
                                               lblTotalLocal, lblTotalVisitante, lblMensaje);
            });
            
            filaJugador.getChildren().addAll(lblJugador, btnAgregarGol, btnQuitarGol, lblGoles);
            panel.getChildren().add(filaJugador);
        }
        
        return panel;
    }
    
    /**
     * Muestra un resumen de los goles registrados por jugador.
     */
    private void mostrarResumenGolesRegistrados(Partido partido, int golesLocalTotal, int golesVisitanteTotal) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("✅ Goles registrados por jugador:\n\n");
        resumen.append("📊 Total: ").append(partido.getEquipoLocal().getNombre())
               .append(" ").append(golesLocalTotal)
               .append(" - ").append(golesVisitanteTotal)
               .append(" ").append(partido.getEquipoVisitante().getNombre()).append("\n\n");
        
        Map<String, Integer> golesPorJugador = partido.getGolesPorJugador();
        if (golesPorJugador.isEmpty()) {
            resumen.append("⚠️ No se registraron goles por jugador.");
        } else {
            resumen.append("⚽ Goleadores:\n");
            golesPorJugador.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach(entry -> {
                    resumen.append("  • ").append(entry.getKey())
                           .append(": ").append(entry.getValue()).append(" gol(es)\n");
                });
        }
        
        resumen.append("\n💡 Ahora puedes registrar el resultado final del partido.");
        salida.setText(resumen.toString());
    }
    
    /**
     * Crea un panel para un equipo con botones para agregar/quitar goles a cada jugador (versión original).
     */
    private VBox crearPanelGolesEquipo(Equipo equipo, Partido partido, String titulo, Label lblResumen, Label lblTotalGoles) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");
        
        Text tituloText = new Text(titulo);
        tituloText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        panel.getChildren().add(tituloText);
        
        List<Jugador> jugadores = equipo.getJugadores();
        if (jugadores.isEmpty()) {
            panel.getChildren().add(new Label("Sin jugadores registrados"));
            return panel;
        }
        
        // Crear una fila para cada jugador
        for (Jugador jugador : jugadores) {
            HBox filaJugador = new HBox(10);
            filaJugador.setAlignment(Pos.CENTER_LEFT);
            
            // Nombre del jugador
            Label lblJugador = new Label(String.format("[%d] %s (%s)", 
                jugador.getNumero(), jugador.getNombre(), jugador.getPosicion()));
            lblJugador.setPrefWidth(180);
            
            // Botón para agregar gol
            Button btnAgregarGol = new Button("+1");
            btnAgregarGol.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            btnAgregarGol.setPrefWidth(50);
            
            // Botón para quitar gol
            Button btnQuitarGol = new Button("-1");
            btnQuitarGol.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
            btnQuitarGol.setPrefWidth(50);
            
            // Contador de goles del jugador en este partido
            Label lblGoles = new Label("Goles: " + obtenerGolesJugador(partido, jugador));
            lblGoles.setPrefWidth(80);
            
            // Acción del botón: agregar gol y actualizar el contador de este jugador
            btnAgregarGol.setOnAction(e -> {
                partido.agregarGol(equipo, jugador);
                // Actualizar el contador de este jugador
                lblGoles.setText("Goles: " + obtenerGolesJugador(partido, jugador));
                // Actualizar el resumen si existe
                if (lblResumen != null && lblTotalGoles != null) {
                    actualizarResumenGoles(partido, lblResumen, lblTotalGoles);
                }
            });
            
            // Acción del botón: quitar gol y actualizar el contador de este jugador
            btnQuitarGol.setOnAction(e -> {
                partido.quitarGol(equipo, jugador);
                // Actualizar el contador de este jugador
                lblGoles.setText("Goles: " + obtenerGolesJugador(partido, jugador));
                // Actualizar el resumen si existe
                if (lblResumen != null && lblTotalGoles != null) {
                    actualizarResumenGoles(partido, lblResumen, lblTotalGoles);
                }
            });
            
            filaJugador.getChildren().addAll(lblJugador, btnAgregarGol, btnQuitarGol, lblGoles);
            panel.getChildren().add(filaJugador);
        }
        
        return panel;
    }
    
    /**
     * Obtiene la cantidad de goles de un jugador en un partido específico.
     */
    private int obtenerGolesJugador(Partido partido, Jugador jugador) {
        String clave = jugador.getNombre() + " (" + jugador.getEquipo().getNombre() + ")";
        return partido.getGolesPorJugador().getOrDefault(clave, 0);
    }
    
    /**
     * Actualiza el resumen de goles del partido (versión integrada).
     */
    private void actualizarResumenGolesIntegrado(Partido partido, int golesLocalTotal, int golesVisitanteTotal,
                                                Label lblTotalLocal, Label lblTotalVisitante, Label lblMensaje) {
        int golesRegistradosLocal = partido.getGolesLocal();
        int golesRegistradosVisitante = partido.getGolesVisitante();
        
        if (lblTotalLocal != null) {
            String estilo = golesRegistradosLocal == golesLocalTotal ? 
                "-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px;" :
                "-fx-text-fill: orange; -fx-font-weight: bold; -fx-font-size: 14px;";
            lblTotalLocal.setText(String.format("%s: %d/%d", 
                partido.getEquipoLocal().getNombre(), golesRegistradosLocal, golesLocalTotal));
            lblTotalLocal.setStyle(estilo);
        }
        
        if (lblTotalVisitante != null) {
            String estilo = golesRegistradosVisitante == golesVisitanteTotal ? 
                "-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px;" :
                "-fx-text-fill: orange; -fx-font-weight: bold; -fx-font-size: 14px;";
            lblTotalVisitante.setText(String.format("%s: %d/%d", 
                partido.getEquipoVisitante().getNombre(), golesRegistradosVisitante, golesVisitanteTotal));
            lblTotalVisitante.setStyle(estilo);
        }
        
        if (lblMensaje != null) {
            if (golesRegistradosLocal == golesLocalTotal && golesRegistradosVisitante == golesVisitanteTotal) {
                lblMensaje.setText("✅ Todos los goles han sido asignados correctamente");
                lblMensaje.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                int faltanLocal = golesLocalTotal - golesRegistradosLocal;
                int faltanVisitante = golesVisitanteTotal - golesRegistradosVisitante;
                StringBuilder mensaje = new StringBuilder("⚠️ Faltan asignar: ");
                if (faltanLocal > 0) mensaje.append(faltanLocal).append(" gol(es) del local. ");
                if (faltanVisitante > 0) mensaje.append(faltanVisitante).append(" gol(es) del visitante.");
                lblMensaje.setText(mensaje.toString());
                lblMensaje.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            }
        }
    }
    
    /**
     * Actualiza el resumen de goles del partido (versión original).
     */
    private void actualizarResumenGoles(Partido partido, Label lblResumen, Label lblTotalGoles) {
        if (lblTotalGoles != null) {
            lblTotalGoles.setText(String.format("📊 Total: %s %d - %d %s", 
                partido.getEquipoLocal().getNombre(),
                partido.getGolesLocal(),
                partido.getGolesVisitante(),
                partido.getEquipoVisitante().getNombre()));
            lblTotalGoles.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        }
        
        if (lblResumen != null) {
            StringBuilder resumen = new StringBuilder();
            
            Map<String, Integer> golesPorJugador = partido.getGolesPorJugador();
            if (golesPorJugador.isEmpty()) {
                resumen.append("Aún no se han registrado goles por jugador.");
            } else {
                resumen.append("⚽ Goleadores:\n");
                golesPorJugador.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .forEach(entry -> {
                        resumen.append("  • ").append(entry.getKey())
                               .append(": ").append(entry.getValue()).append(" gol(es)\n");
                    });
            }
            
            lblResumen.setText(resumen.toString());
            lblResumen.setStyle("-fx-font-size: 12px;");
        }
    }
    
    // ====================================================================
    // 6. DIÁLOGO DE GESTIÓN DE TARJETAS (MÉTODO CORREGIDO)
    // ====================================================================

    /**
     * Abre un diálogo para registrar tarjetas amarillas o rojas a un jugador específico 
     * del equipo local o visitante del partido seleccionado.
     */
   // Archivo: com.ejemplo.view.VistaPartidos.java (Solo el método corregido)

// Archivo: com.ejemplo.view.VistaPartidos.java (Solo el método corregido)

// Archivo: com.ejemplo.view.VistaPartidos.java (Solo el método corregido)

private void mostrarDialogoTarjetas() {
    Partido partido = cmbPartidosResultado.getValue();
    if (partido == null) {
        mostrarError("Debe seleccionar primero un partido para asignar tarjetas.");
        return;
    }
    
    // 1. Obtener los equipos y jugadores
    Equipo equipoLocal = partido.getEquipoLocal();
    Equipo equipoVisitante = partido.getEquipoVisitante();
    
    // Listas separadas para cada equipo
    List<Jugador> jugadoresLocal = equipoLocal.getJugadores();
    List<Jugador> jugadoresVisitante = equipoVisitante.getJugadores();

    // 2. Crear el diálogo
    // 🛑 CAMBIO: El diálogo ahora devuelve una lista de Jugadores seleccionados.
    Dialog<List<Jugador>> dialog = new Dialog<>();
    dialog.setTitle("Gestión de Tarjetas (Selección Múltiple)");
    dialog.setHeaderText("Seleccione uno o más jugadores y el tipo de tarjeta:");
    
    // 3. Crear botones
    ButtonType registrarButtonType = new ButtonType("Registrar Tarjeta(s)", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(registrarButtonType, ButtonType.CANCEL);

    // 4. Contenedor principal del diálogo
    VBox contentBox = new VBox(15);
    contentBox.setPadding(new Insets(20));

    // =========================================================================
    // 5. SELECCIÓN DE JUGADORES EN DOS COLUMNAS (ListView)
    // =========================================================================
    
    // Crear ListView para el equipo local
    ListView<Jugador> listLocal = new ListView<>(FXCollections.observableArrayList(jugadoresLocal));
    listLocal.setPrefHeight(200);
    listLocal.setPrefWidth(250);
    // 🛑 CLAVE 1: Habilitar selección múltiple
    listLocal.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    
    // Crear ListView para el equipo visitante
    ListView<Jugador> listVisitante = new ListView<>(FXCollections.observableArrayList(jugadoresVisitante));
    listVisitante.setPrefHeight(200);
    listVisitante.setPrefWidth(250);
    // 🛑 CLAVE 1: Habilitar selección múltiple
    listVisitante.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    // Configurar CellFactory para ambos ListView
    listLocal.setCellFactory(lv -> createJugadorListCell());
    listVisitante.setCellFactory(lv -> createJugadorListCell());

    // 🛑 ELIMINAMOS la lógica de 'selección exclusiva'

    // Contenedor de las dos listas con sus títulos
    VBox localBox = new VBox(5, new Text("🔵 " + equipoLocal.getNombre()), listLocal);
    VBox visitanteBox = new VBox(5, new Text("🔴 " + equipoVisitante.getNombre()), listVisitante);

    HBox listasBox = new HBox(20, localBox, visitanteBox);
    listasBox.setAlignment(Pos.CENTER);
    
    // =========================================================================
    // 6. CONTROLES DE TARJETA
    // =========================================================================
    ToggleGroup group = new ToggleGroup();
    RadioButton rbAmarilla = new RadioButton("Amarilla 🟨");
    rbAmarilla.setToggleGroup(group);
    rbAmarilla.setSelected(true);
    
    RadioButton rbRoja = new RadioButton("Roja 🟥");
    rbRoja.setToggleGroup(group);
    
    HBox radioBox = new HBox(15, new Text("Tipo de Tarjeta:"), rbAmarilla, rbRoja);
    radioBox.setAlignment(Pos.CENTER_LEFT);

    // Añadir al VBox principal del contenido
    contentBox.getChildren().addAll(listasBox, radioBox);
    dialog.getDialogPane().setContent(contentBox);

    // 7. Configurar la conversión de resultado
    // 🛑 CLAVE 2: Devolver una lista combinada de jugadores
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == registrarButtonType) {
            List<Jugador> seleccionados = new ArrayList<>();
            // Agregar jugadores seleccionados del equipo local
            seleccionados.addAll(listLocal.getSelectionModel().getSelectedItems());
            // Agregar jugadores seleccionados del equipo visitante
            seleccionados.addAll(listVisitante.getSelectionModel().getSelectedItems());
            
            if (seleccionados.isEmpty()) {
                mostrarError("Debe seleccionar al menos un jugador.");
                return null;
            }
            return seleccionados;
        }
        return null;
    });

    // 8. Mostrar y procesar el resultado
    // 🛑 CLAVE 3: Iterar sobre la lista de jugadores seleccionados
    dialog.showAndWait().ifPresent(jugadoresSeleccionados -> {
        
        String tipo = rbAmarilla.isSelected() ? "Amarilla" : "Roja";
        int tarjetasAplicadas = 0;
        
        for (Jugador jugadorSeleccionado : jugadoresSeleccionados) {
            // Se asume que getEquipo() no es nulo gracias a DatosIniciales
            partido.agregarTarjeta(jugadorSeleccionado.getEquipo(), jugadorSeleccionado, tipo); 
            tarjetasAplicadas++;
        }
        
        if (tarjetasAplicadas > 0) {
            mostrarInfo(String.format("✅ Tarjeta %s registrada para %d jugador(es) seleccionados.", tipo, tarjetasAplicadas));
        } else {
             // Este caso ya lo maneja el ResultConverter, pero es una buena salvaguarda.
             mostrarError("No se seleccionó ningún jugador válido.");
        }
        
        // Es vital actualizar para que se vea el nuevo estado de suspensión (SUSP.)
        actualizarCombos(); 
    });
}


/**
 * Método auxiliar para crear el CellFactory estándar de Jugadores.
 * (Se mantiene igual al último fix, mostrando info relevante)
 */
private ListCell<Jugador> createJugadorListCell() {
    return new ListCell<Jugador>() {
        @Override
        protected void updateItem(Jugador item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText("");
            } else {
                String estado = item.estaSuspendido() ? "SUSP." : "Activo";
                setText(String.format("[%d] %s (%s) - %s", 
                                        item.getNumero(),
                                        item.getNombre(),
                                        item.getPosicion(),
                                        estado));
            }
        }
    };
}

//... el resto de la clase VistaPartidos.java se mantiene igual.
//... el resto de la clase VistaPartidos.java se mantiene igual.
    // ====================================================================
    // 4. ACTUALIZACIÓN DE DATOS Y FILTROS
    // ====================================================================

    /**
     * Refresca ambos ComboBox, separando partidos sin fecha (para programar) 
     * y partidos programados pero no jugados (para registrar resultado).
     */
    public void actualizarCombos() {
        if (!torneo.isSorteoRealizado()) {
            if (cmbPartidosFecha != null) {
                cmbPartidosFecha.getItems().clear();
            }
            if (cmbPartidosResultado != null) {
                cmbPartidosResultado.getItems().clear();
            }
            if (cmbPartidosEditados != null) {
                cmbPartidosEditados.getItems().clear();
            }
            return;
        }
        
        // Obtener todos los partidos del torneo (ya incluye los de eliminatoria que se agregaron automáticamente)
        List<Partido> todosPartidos = new ArrayList<>(controller.obtenerPartidos());
        
        // Verificar si hay eliminatoria y asegurar que todos sus partidos estén en la lista del torneo
        if (torneo.getEliminatoria() != null) {
            List<Partido> partidosEliminatoria = torneo.getEliminatoria().getTodosLosPartidos();
            for (Partido partidoElim : partidosEliminatoria) {
                // Verificar si el partido ya está en la lista del torneo
                // Usar comparación por referencia Y por contenido (equipos y fecha)
                boolean yaExiste = false;
                for (Partido p : todosPartidos) {
                    if (p == partidoElim || 
                        (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                         partidoElim.getEquipoLocal() != null && partidoElim.getEquipoVisitante() != null &&
                         p.getEquipoLocal().equals(partidoElim.getEquipoLocal()) &&
                         p.getEquipoVisitante().equals(partidoElim.getEquipoVisitante()))) {
                        yaExiste = true;
                        break;
                    }
                }
                
                if (!yaExiste) {
                    // Agregar a la lista del torneo si no está
                    torneo.getPartidos().add(partidoElim);
                    todosPartidos.add(partidoElim);
                }
            }
        }
        
        // Usar un Set para eliminar duplicados (por referencia de objeto)
        // Pero también verificar por contenido para evitar duplicados lógicos
        Set<Partido> partidosUnicos = new LinkedHashSet<>();
        for (Partido p : todosPartidos) {
            boolean esDuplicado = false;
            for (Partido existente : partidosUnicos) {
                if (p == existente || 
                    (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                     existente.getEquipoLocal() != null && existente.getEquipoVisitante() != null &&
                     p.getEquipoLocal().equals(existente.getEquipoLocal()) &&
                     p.getEquipoVisitante().equals(existente.getEquipoVisitante()))) {
                    esDuplicado = true;
                    break;
                }
            }
            if (!esDuplicado) {
                partidosUnicos.add(p);
            }
        }
        List<Partido> todos = new ArrayList<>(partidosUnicos);
        
        // 1. Partidos sin fecha asignada (para el panel de programación)
        List<Partido> sinFecha = todos.stream()
                .filter(p -> p.getFechaHora() == null) 
                .collect(Collectors.toList());
        
        // Asegurar que todos los partidos de eliminatoria sin fecha estén incluidos
        if (torneo.getEliminatoria() != null) {
            // Agregar cuartos sin fecha
            for (Partido cuarto : torneo.getEliminatoria().getPartidosCuartos()) {
                if (cuarto.getFechaHora() == null && !sinFecha.contains(cuarto)) {
                    boolean yaEsta = sinFecha.stream().anyMatch(p -> 
                        p == cuarto || 
                        (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                         cuarto.getEquipoLocal() != null && cuarto.getEquipoVisitante() != null &&
                         p.getEquipoLocal().equals(cuarto.getEquipoLocal()) &&
                         p.getEquipoVisitante().equals(cuarto.getEquipoVisitante()))
                    );
                    if (!yaEsta) {
                        sinFecha.add(cuarto);
                    }
                }
            }
            // Agregar semifinales sin fecha
            for (Partido semi : torneo.getEliminatoria().getPartidosSemifinal()) {
                if (semi.getFechaHora() == null && !sinFecha.contains(semi)) {
                    boolean yaEsta = sinFecha.stream().anyMatch(p -> 
                        p == semi || 
                        (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                         semi.getEquipoLocal() != null && semi.getEquipoVisitante() != null &&
                         p.getEquipoLocal().equals(semi.getEquipoLocal()) &&
                         p.getEquipoVisitante().equals(semi.getEquipoVisitante()))
                    );
                    if (!yaEsta) {
                        sinFecha.add(semi);
                    }
                }
            }
            // Agregar final sin fecha
            if (torneo.getEliminatoria().getPartidoFinal() != null) {
                Partido finalPartido = torneo.getEliminatoria().getPartidoFinal();
                if (finalPartido.getFechaHora() == null && !sinFecha.contains(finalPartido)) {
                    boolean yaEsta = sinFecha.stream().anyMatch(p -> 
                        p == finalPartido || 
                        (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                         finalPartido.getEquipoLocal() != null && finalPartido.getEquipoVisitante() != null &&
                         p.getEquipoLocal().equals(finalPartido.getEquipoLocal()) &&
                         p.getEquipoVisitante().equals(finalPartido.getEquipoVisitante()))
                    );
                    if (!yaEsta) {
                        sinFecha.add(finalPartido);
                    }
                }
            }
            // Agregar tercer puesto sin fecha
            if (torneo.getEliminatoria().getPartidoTercerPuesto() != null) {
                Partido tercerPuesto = torneo.getEliminatoria().getPartidoTercerPuesto();
                if (tercerPuesto.getFechaHora() == null && !sinFecha.contains(tercerPuesto)) {
                    boolean yaEsta = sinFecha.stream().anyMatch(p -> 
                        p == tercerPuesto || 
                        (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                         tercerPuesto.getEquipoLocal() != null && tercerPuesto.getEquipoVisitante() != null &&
                         p.getEquipoLocal().equals(tercerPuesto.getEquipoLocal()) &&
                         p.getEquipoVisitante().equals(tercerPuesto.getEquipoVisitante()))
                    );
                    if (!yaEsta) {
                        sinFecha.add(tercerPuesto);
                    }
                }
            }
        }
                
        // 2. Partidos con fecha asignada y no jugados (para el panel de resultados)
        // Asegurar que todos los partidos de eliminatoria con fecha estén incluidos
        List<Partido> programados = todos.stream()
                .filter(p -> p.getFechaHora() != null && !p.getJugado()) 
                .collect(Collectors.toList());
        
        // Verificar que todos los partidos de eliminatoria con fecha estén en la lista
        if (torneo.getEliminatoria() != null) {
            // Cuartos
            for (Partido cuarto : torneo.getEliminatoria().getPartidosCuartos()) {
                if (cuarto.getFechaHora() != null && !cuarto.getJugado()) {
                    boolean yaEsta = programados.stream().anyMatch(p -> 
                        p == cuarto || 
                        (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                         cuarto.getEquipoLocal() != null && cuarto.getEquipoVisitante() != null &&
                         p.getEquipoLocal().equals(cuarto.getEquipoLocal()) &&
                         p.getEquipoVisitante().equals(cuarto.getEquipoVisitante()))
                    );
                    if (!yaEsta) {
                        programados.add(cuarto);
                    }
                }
            }
            // Semifinales
            for (Partido semi : torneo.getEliminatoria().getPartidosSemifinal()) {
                if (semi.getFechaHora() != null && !semi.getJugado()) {
                    boolean yaEsta = programados.stream().anyMatch(p -> 
                        p == semi || 
                        (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                         semi.getEquipoLocal() != null && semi.getEquipoVisitante() != null &&
                         p.getEquipoLocal().equals(semi.getEquipoLocal()) &&
                         p.getEquipoVisitante().equals(semi.getEquipoVisitante()))
                    );
                    if (!yaEsta) {
                        programados.add(semi);
                    }
                }
            }
            // Final
            if (torneo.getEliminatoria().getPartidoFinal() != null) {
                Partido finalPartido = torneo.getEliminatoria().getPartidoFinal();
                if (finalPartido.getFechaHora() != null && !finalPartido.getJugado()) {
                    boolean yaEsta = programados.stream().anyMatch(p -> 
                        p == finalPartido || 
                        (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                         finalPartido.getEquipoLocal() != null && finalPartido.getEquipoVisitante() != null &&
                         p.getEquipoLocal().equals(finalPartido.getEquipoLocal()) &&
                         p.getEquipoVisitante().equals(finalPartido.getEquipoVisitante()))
                    );
                    if (!yaEsta) {
                        programados.add(finalPartido);
                    }
                }
            }
            // Tercer puesto
            if (torneo.getEliminatoria().getPartidoTercerPuesto() != null) {
                Partido tercerPuesto = torneo.getEliminatoria().getPartidoTercerPuesto();
                if (tercerPuesto.getFechaHora() != null && !tercerPuesto.getJugado()) {
                    boolean yaEsta = programados.stream().anyMatch(p -> 
                        p == tercerPuesto || 
                        (p.getEquipoLocal() != null && p.getEquipoVisitante() != null &&
                         tercerPuesto.getEquipoLocal() != null && tercerPuesto.getEquipoVisitante() != null &&
                         p.getEquipoLocal().equals(tercerPuesto.getEquipoLocal()) &&
                         p.getEquipoVisitante().equals(tercerPuesto.getEquipoVisitante()))
                    );
                    if (!yaEsta) {
                        programados.add(tercerPuesto);
                    }
                }
            }
        }
        
        // 3. Partidos jugados (para el panel de edición)
        // Mostrar solo los partidos de la fase actual
        List<Partido> jugados = obtenerPartidosJugadosFaseActual(todos);

        // Actualizar ComboBoxes
        if (cmbPartidosFecha != null) {
            cmbPartidosFecha.getItems().setAll(sinFecha);
        }
        if (cmbPartidosResultado != null) {
            cmbPartidosResultado.getItems().setAll(programados);
        }
        if (cmbPartidosEditados != null) {
            cmbPartidosEditados.getItems().setAll(jugados);
        }
        
        // Limpiar el Label dinámico si se vacían los combos
        if (programados.isEmpty() && lblGolesLocalNombre != null) {
            lblGolesLocalNombre.setText("Local:");
            lblGolesVisitanteNombre.setText("Visita:");
        }
    }
    
    /**
     * Obtiene los partidos jugados de la fase actual del torneo
     */
    private List<Partido> obtenerPartidosJugadosFaseActual(List<Partido> todos) {
        Eliminatoria elim = torneo.getEliminatoria();
        
        // Si no hay eliminatoria o los cuartos no están generados, mostrar solo partidos de grupos
        if (elim == null || elim.getPartidosCuartos().isEmpty()) {
            return todos.stream()
                    .filter(p -> p.getJugado() && !esPartidoEliminatoria(p))
                    .collect(Collectors.toList());
        }
        
        // Verificar qué fase está activa
        boolean todosCuartosJugados = elim.getPartidosCuartos().size() == 4 && 
                                      elim.getPartidosCuartos().stream().allMatch(Partido::getJugado);
        boolean todasSemisJugadas = !elim.getPartidosSemifinal().isEmpty() && 
                                    elim.getPartidosSemifinal().stream().allMatch(Partido::getJugado);
        
        if (!todosCuartosJugados) {
            // Fase de cuartos: mostrar solo partidos de cuartos jugados
            return elim.getPartidosCuartos().stream()
                    .filter(Partido::getJugado)
                    .collect(Collectors.toList());
        } else if (!todasSemisJugadas) {
            // Fase de semifinales: mostrar solo partidos de semifinales jugados
            return elim.getPartidosSemifinal().stream()
                    .filter(Partido::getJugado)
                    .collect(Collectors.toList());
        } else {
            // Fase de final y tercer puesto: mostrar solo final y tercer puesto jugados
            List<Partido> finales = new ArrayList<>();
            if (elim.getPartidoFinal() != null && elim.getPartidoFinal().getJugado()) {
                finales.add(elim.getPartidoFinal());
            }
            if (elim.getPartidoTercerPuesto() != null && elim.getPartidoTercerPuesto().getJugado()) {
                finales.add(elim.getPartidoTercerPuesto());
            }
            return finales;
        }
    }
    
    /**
     * Verifica si un partido es de eliminatoria (cuartos, semifinales, final o tercer puesto)
     */
    private boolean esPartidoEliminatoria(Partido partido) {
        Eliminatoria elim = torneo.getEliminatoria();
        if (elim == null) {
            return false;
        }
        return elim.getPartidosCuartos().contains(partido) ||
               elim.getPartidosSemifinal().contains(partido) ||
               (elim.getPartidoFinal() != null && partido.equals(elim.getPartidoFinal())) ||
               (elim.getPartidoTercerPuesto() != null && partido.equals(elim.getPartidoTercerPuesto()));
    }
    
    // 📋 Mostrar resumen de partidos
    private void mostrarPartidos() {
        // Si ya se acabó la fase de grupos y hay eliminatoria, mostrar partidos de eliminatoria
        if (torneo.todosPartidosDeGruposJugados() && torneo.getEliminatoria() != null) {
            mostrarPartidosEliminatoria();
        } else {
            mostrarPartidosGrupos();
        }
    }
    
    /**
     * Muestra los partidos organizados por grupos (fase de grupos)
     */
    private void mostrarPartidosGrupos() {
        StringBuilder sb = new StringBuilder("📅 PARTIDOS POR GRUPO:\n\n");
        
        Map<String, List<Equipo>> grupos = controller.obtenerGrupos(); 
        List<Partido> todosLosPartidos = controller.obtenerPartidos();

        if (grupos.isEmpty() || todosLosPartidos.isEmpty()) {
            salida.setText("⚠️ No hay grupos sorteados o partidos generados.");
            return;
        }
        
        for (String nombreGrupo : grupos.keySet()) {
            sb.append("--- ").append(nombreGrupo).append(" ---\n");
            
            for (Partido p : todosLosPartidos) {
                // Asumiendo que el grupo del partido se define por el equipo local
                String grupoLocal = p.getEquipoLocal().getNombreGrupo(); 
                
                if (nombreGrupo.equals(grupoLocal)) {
                    sb.append(p.resumen()).append("\n"); 
                }
            }
            sb.append("\n");
        }
        
        salida.setText(sb.toString());
    }
    
    /**
     * Muestra los partidos de eliminatoria (cuartos, semifinales, final, tercer puesto)
     */
    private void mostrarPartidosEliminatoria() {
        StringBuilder sb = new StringBuilder("🏆 PARTIDOS DE ELIMINATORIA:\n\n");
        
        Eliminatoria elim = torneo.getEliminatoria();
        if (elim == null) {
            salida.setText("⚠️ No hay partidos de eliminatoria generados.");
            return;
        }
        
        // Cuartos de final
        if (!elim.getPartidosCuartos().isEmpty()) {
            sb.append("⚔️ CUARTOS DE FINAL:\n");
            sb.append("═══════════════════════════════════════\n");
            for (int i = 0; i < elim.getPartidosCuartos().size(); i++) {
                Partido p = elim.getPartidosCuartos().get(i);
                sb.append("Cuarto ").append(i + 1).append(": ").append(p.resumen()).append("\n");
                if (p.getJugado() && p.getPenalesLocal() != null) {
                    sb.append("   🏆 Ganador por penales: ").append(p.getGanadorPorPenales().getNombre())
                      .append(" (").append(p.getPenalesLocal()).append("-").append(p.getPenalesVisitante()).append(")\n");
                }
            }
            sb.append("\n");
        }
        
        // Semifinales
        if (!elim.getPartidosSemifinal().isEmpty()) {
            sb.append("⚔️ SEMIFINALES:\n");
            sb.append("═══════════════════════════════════════\n");
            for (int i = 0; i < elim.getPartidosSemifinal().size(); i++) {
                Partido p = elim.getPartidosSemifinal().get(i);
                sb.append("Semifinal ").append(i + 1).append(": ").append(p.resumen()).append("\n");
                if (p.getJugado() && p.getPenalesLocal() != null) {
                    sb.append("   🏆 Ganador por penales: ").append(p.getGanadorPorPenales().getNombre())
                      .append(" (").append(p.getPenalesLocal()).append("-").append(p.getPenalesVisitante()).append(")\n");
                }
            }
            sb.append("\n");
        }
        
        // Tercer puesto
        if (elim.getPartidoTercerPuesto() != null) {
            sb.append("🥉 TERCER PUESTO:\n");
            sb.append("═══════════════════════════════════════\n");
            Partido p = elim.getPartidoTercerPuesto();
            sb.append(p.resumen()).append("\n");
            if (p.getJugado() && p.getPenalesLocal() != null) {
                sb.append("   🏆 Ganador por penales: ").append(p.getGanadorPorPenales().getNombre())
                  .append(" (").append(p.getPenalesLocal()).append("-").append(p.getPenalesVisitante()).append(")\n");
            }
            sb.append("\n");
        }
        
        // Final
        if (elim.getPartidoFinal() != null) {
            sb.append("🏆 FINAL:\n");
            sb.append("═══════════════════════════════════════\n");
            Partido p = elim.getPartidoFinal();
            sb.append(p.resumen()).append("\n");
            if (p.getJugado() && p.getPenalesLocal() != null) {
                sb.append("   🏆 Ganador por penales: ").append(p.getGanadorPorPenales().getNombre())
                  .append(" (").append(p.getPenalesLocal()).append("-").append(p.getPenalesVisitante()).append(")\n");
            }
            sb.append("\n");
        }
        
        salida.setText(sb.toString());
    }

    /**
     * Actualiza las estadísticas de los equipos después de registrar un partido.
     */
    private void actualizarEstadisticasEquiposPartido(Partido partido) {
        Equipo local = partido.getEquipoLocal();
        Equipo visitante = partido.getEquipoVisitante();
        
        int golesLocal = partido.getGolesLocal();
        int golesVisitante = partido.getGolesVisitante();
        
        // Actualizar goles
        local.setGolesFavor(local.getGolesFavor() + golesLocal);
        local.setGolesContra(local.getGolesContra() + golesVisitante);
        visitante.setGolesFavor(visitante.getGolesFavor() + golesVisitante);
        visitante.setGolesContra(visitante.getGolesContra() + golesLocal);
        
        // Actualizar partidos jugados
        local.setPartidosJugados(local.getPartidosJugados() + 1);
        visitante.setPartidosJugados(visitante.getPartidosJugados() + 1);
        
        // Actualizar puntos y resultados
        if (golesLocal > golesVisitante) {
            // Gana local
            local.setPuntos(local.getPuntos() + 3);
            local.setGanados(local.getGanados() + 1);
            visitante.setPerdidos(visitante.getPerdidos() + 1);
        } else if (golesVisitante > golesLocal) {
            // Gana visitante
            visitante.setPuntos(visitante.getPuntos() + 3);
            visitante.setGanados(visitante.getGanados() + 1);
            local.setPerdidos(local.getPerdidos() + 1);
        } else {
            // Empate
            local.setPuntos(local.getPuntos() + 1);
            visitante.setPuntos(visitante.getPuntos() + 1);
            local.setEmpatados(local.getEmpatados() + 1);
            visitante.setEmpatados(visitante.getEmpatados() + 1);
        }
        
        // Verificar si se deben generar cuartos automáticamente
        if (torneo.todosPartidosDeGruposJugados() && !torneo.isCuartosGenerados()) {
            // Esto se maneja automáticamente en el método verificarYGenerarCuartos
            // que se llama desde registrarResultado, pero aquí no lo llamamos directamente
            // porque ya estamos en el flujo de registro
        }
    }
    
    // Métodos auxiliares para mostrar errores e información
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
}