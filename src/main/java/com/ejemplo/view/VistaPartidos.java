package com.ejemplo.view;

import com.ejemplo.controller.PartidosController;
import com.ejemplo.model.*;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    
    // 🆕 Botón para gestión de tarjetas
    private Button btnGestionarTarjetas;
    
    // ⚽ Botón para registrar goles por jugador
    private Button btnRegistrarGoles;
    
    // Controles generales
    private Button btnSortear;
    private TextArea salida;

    public VistaPartidos(Torneo torneo) {
        super("📅 Gestión de Partidos");
        this.torneo = torneo;
        this.controller = new PartidosController(torneo);
        
        // 1. Inicializar todos los componentes (incluyendo etiquetas dinámicas)
        inicializarComponentes();
        
        // 2. Comprobar estado inicial del sorteo y cargar datos
        if (torneo.isSorteoRealizado()) {
              btnSortear.setDisable(true);
              actualizarCombos();
        } else {
              actualizarCombos(); // Limpiar combos si no hay sorteo
        }

        // 3. Crear los paneles de interfaz
        VBox boxFecha = crearPanelAsignacionFecha();
        VBox boxResultado = crearPanelRegistroResultado();
        
        // HBox para contener los dos paneles de acción
        HBox paneles = new HBox(20, boxFecha, boxResultado);
        paneles.setAlignment(Pos.CENTER);
        paneles.setPadding(new Insets(10));
        
        // Botón para mostrar todos los partidos
        Button btnMostrar = new Button("👀 Mostrar Partidos");
        btnMostrar.setOnAction(e -> mostrarPartidos());

        // Layout principal que contiene los paneles y el área de salida
        VBox boxPrincipal = new VBox(20, 
                new Text("Gestión de Partidos de Grupo"),
                btnSortear,
                paneles, 
                salida,
                btnMostrar);
        
        boxPrincipal.setPadding(new Insets(15));
        boxPrincipal.setAlignment(Pos.TOP_CENTER);

        getChildren().add(boxPrincipal);
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
        
        // 🆕 Inicializar botón de tarjetas
        btnGestionarTarjetas = new Button("🟨/🟥 Añadir Tarjetas a Jugadores");
        btnGestionarTarjetas.setOnAction(e -> mostrarDialogoTarjetas());
        
        // ⚽ Inicializar botón de goles
        btnRegistrarGoles = new Button("⚽ Registrar Goles por Jugador");
        btnRegistrarGoles.setOnAction(e -> mostrarDialogoGoles());

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
        
        // Desactivar los botones al inicio
        btnGestionarTarjetas.setDisable(true);
        btnRegistrarGoles.setDisable(true);
    }
    
    // ====================================================================
    // 2. CREACIÓN DE PANELES DE LAYOUT
    // ====================================================================
    
    private VBox crearPanelAsignacionFecha() {
        Button btnProgramar = new Button("📅 Asignar Fecha");
        btnProgramar.setOnAction(e -> handleProgramar());
        
        VBox panel = new VBox(10, 
                new Text("1. PROGRAMACIÓN DE PARTIDOS"),
                cmbPartidosFecha,
                txtFecha,
                btnProgramar);
        
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setMinWidth(350);
        panel.setStyle("-fx-border-color: #5555ff; -fx-border-radius: 5; -fx-padding: 15;");
        return panel;
    }

    private VBox crearPanelRegistroResultado() {
        Button btnRegistrar = new Button("✅ Registrar Resultado");
        btnRegistrar.setOnAction(e -> handleRegistrar());

        // HBox para los inputs de goles (Muestra Nombre: [Input])
        HBox golesInputBox = new HBox(10);
        golesInputBox.setAlignment(Pos.CENTER);
        golesInputBox.getChildren().addAll(
            lblGolesLocalNombre, txtGolesLocal,
            lblGolesVisitanteNombre, txtGolesVisitante
        );
        
        // Contenedor principal de inputs y acciones
        VBox inputGroup = new VBox(10, golesInputBox, btnRegistrarGoles, btnGestionarTarjetas);
        inputGroup.setAlignment(Pos.CENTER);

        VBox panel = new VBox(15, 
                new Text("2. REGISTRO DE RESULTADO"),
                cmbPartidosResultado,
                inputGroup,
                btnRegistrar);
        
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setMinWidth(350);
        panel.setStyle("-fx-border-color: #55ff55; -fx-border-radius: 5; -fx-padding: 15;");
        return panel;
    }

    // ====================================================================
    // 3. MANEJADORES DE EVENTOS
    // ====================================================================

    private void handleSortear() {
        try {
            if (!torneo.isSorteoRealizado()) {
                controller.sortearFaseGrupos();
                mostrarInfo("✅ Fase de grupos sorteada y partidos generados.");
                btnSortear.setDisable(true); 
            } else {
                mostrarInfo("ℹ️ El sorteo ya se había realizado.");
            }
            actualizarCombos();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
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
            
            salida.setText("✅ Fecha asignada para " + partido.getEquipoLocal().getNombre() +
                            " vs " + partido.getEquipoVisitante().getNombre() + 
                            " el " + fecha + ".\n");
            txtFecha.clear();
            
            actualizarCombos(); // Mueve el partido al combo de Resultados
            
        } catch (Exception ex) {
            mostrarError("Error al asignar fecha: " + ex.getMessage());
        }
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

            // Verificar si ya se registraron goles por jugador
            int golesRegistradosLocal = partido.getGolesLocal();
            int golesRegistradosVisitante = partido.getGolesVisitante();
            
            // Si no hay goles registrados por jugador, abrir diálogo primero
            if (golesRegistradosLocal == 0 && golesRegistradosVisitante == 0) {
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
                controller.registrarResultado(partido, golesLocal, golesVisitante);
                
                salida.setText("✅ Resultado registrado: " + 
                                partido.getEquipoLocal().getNombre() + " " + golesLocal + 
                                " - " + golesVisitante + " " + partido.getEquipoVisitante().getNombre() + 
                                ".\nLa tabla de posiciones se ha actualizado.");
                                
                // Limpiar y actualizar
                txtGolesLocal.clear();
                txtGolesVisitante.clear();
                actualizarCombos();
            }
            
        } catch (NumberFormatException ex) {
            mostrarError("Debe ingresar números válidos para los goles.");
        } catch (Exception ex) {
            mostrarError("Error al registrar resultado: " + ex.getMessage());
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
                    return false;
                }
                
                // Los goles por jugador ya están registrados, solo mostrar resumen
                mostrarResumenGolesRegistrados(partido, golesLocalTotal, golesVisitanteTotal);
                return true;
            }
            return false;
        });
        
        // Mostrar diálogo
        dialog.showAndWait().ifPresent(registrado -> {
            if (registrado) {
                // Actualizar los campos de goles en la interfaz principal
                txtGolesLocal.setText(String.valueOf(partido.getGolesLocal()));
                txtGolesVisitante.setText(String.valueOf(partido.getGolesVisitante()));
            }
        });
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
        
        // Crear paneles para cada equipo con sus jugadores
        VBox panelLocal = crearPanelGolesEquipo(equipoLocal, partido, "🔵 " + equipoLocal.getNombre());
        VBox panelVisitante = crearPanelGolesEquipo(equipoVisitante, partido, "🔴 " + equipoVisitante.getNombre());
        
        HBox equiposBox = new HBox(20, panelLocal, panelVisitante);
        equiposBox.setAlignment(Pos.CENTER);
        
        // Mostrar resumen de goles actuales
        Label lblResumen = new Label();
        Label lblTotalGoles = new Label();
        actualizarResumenGoles(partido, lblResumen, lblTotalGoles);
        
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
            
            filaJugador.getChildren().addAll(lblJugador, btnAgregarGol, lblGoles);
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
    private VBox crearPanelGolesEquipo(Equipo equipo, Partido partido, String titulo) {
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
            
            // Contador de goles del jugador en este partido
            Label lblGoles = new Label("Goles: " + obtenerGolesJugador(partido, jugador));
            lblGoles.setPrefWidth(80);
            
            // Acción del botón: agregar gol y actualizar el contador de este jugador
            btnAgregarGol.setOnAction(e -> {
                partido.agregarGol(equipo, jugador);
                // Actualizar el contador de este jugador
                lblGoles.setText("Goles: " + obtenerGolesJugador(partido, jugador));
            });
            
            filaJugador.getChildren().addAll(lblJugador, btnAgregarGol, lblGoles);
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
            cmbPartidosFecha.getItems().clear();
            cmbPartidosResultado.getItems().clear();
            return;
        }
        
        List<Partido> todos = controller.obtenerPartidos();
        
        // 1. Partidos sin fecha asignada (para el panel de programación)
        List<Partido> sinFecha = todos.stream()
                // FIX: La comprobación de LocalDateTime con null es correcta.
                .filter(p -> p.getFechaHora() == null ) 
                .collect(Collectors.toList());
                
        // 2. Partidos con fecha asignada y no jugados (para el panel de resultados)
        List<Partido> programados = todos.stream()
                // Comprobar que NO sea null Y que NO se haya jugado.
                .filter(p -> p.getFechaHora() != null &&  !p.getJugado()) 
                .collect(Collectors.toList());

        // Actualizar ComboBoxes
        cmbPartidosFecha.getItems().setAll(sinFecha);
        cmbPartidosResultado.getItems().setAll(programados);
        
        // Limpiar el Label dinámico si se vacían los combos
        if (programados.isEmpty()) {
            lblGolesLocalNombre.setText("Local:");
            lblGolesVisitanteNombre.setText("Visita:");
        }
    }
    
    // 📋 Mostrar resumen de partidos
    private void mostrarPartidos() {
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