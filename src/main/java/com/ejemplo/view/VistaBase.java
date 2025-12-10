package com.ejemplo.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class VistaBase extends StackPane {

    public VistaBase(String titulo) {
        Label header = new Label(titulo);
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        VBox box = new VBox(15, header);
        box.setAlignment(Pos.TOP_CENTER);
        getChildren().add(box);
    }
}
