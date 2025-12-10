package com.ejemplo.view;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Pair;
import java.util.Optional;

/**
 * Clase auxiliar para centralizar diálogos JavaFX.
 */
public class DialogosFX {

    // ✅ Mostrar alerta simple
    public static void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ⚠️ Mostrar advertencia
    public static void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // 🧍‍♂️ Diálogo para agregar jugador
    public static Optional<Pair<String, String>> pedirJugador() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Agregar Jugador");
        dialog.setHeaderText("Introduce el nombre y posición del jugador");

        ButtonType okButtonType = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre");
        TextField posicionField = new TextField();
        posicionField.setPromptText("Posición");

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Posición:"), 0, 1);
        grid.add(posicionField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(nombreField.getText(), posicionField.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    // ⚽ Diálogo para registrar resultado de un partido
    public static Optional<Pair<Integer, Integer>> pedirResultado(String equipoLocal, String equipoVisitante) {
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Registrar Resultado");
        dialog.setHeaderText("Resultado del partido entre " + equipoLocal + " y " + equipoVisitante);

        ButtonType okButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField golesLocal = new TextField();
        golesLocal.setPromptText("Goles " + equipoLocal);
        TextField golesVisitante = new TextField();
        golesVisitante.setPromptText("Goles " + equipoVisitante);

        grid.add(new Label(equipoLocal + ":"), 0, 0);
        grid.add(golesLocal, 1, 0);
        grid.add(new Label(equipoVisitante + ":"), 0, 1);
        grid.add(golesVisitante, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    int gl = Integer.parseInt(golesLocal.getText());
                    int gv = Integer.parseInt(golesVisitante.getText());
                    return new Pair<>(gl, gv);
                } catch (NumberFormatException ex) {
                    mostrarError("Error", "Debe ingresar números válidos.");
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
