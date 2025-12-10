package com.ejemplo.view;

import com.ejemplo.model.Eliminatoria;
import com.ejemplo.model.Torneo;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class VistaEliminatoria extends VistaBase {

    private final Eliminatoria eliminatoria = new Eliminatoria();

    public VistaEliminatoria(Torneo torneo) {
        super("🏁 Fase Eliminatoria");

        Button btnSortearCuartos = new Button("Sortear Cuartos");
        Button btnMostrarEliminatoria = new Button("Mostrar Eliminatoria");

        btnSortearCuartos.setOnAction(e -> eliminatoria.sortearCuartos(torneo.clasificarOchoMejores()));
        btnMostrarEliminatoria.setOnAction(e -> eliminatoria.mostrarEliminatoria());

        VBox box = new VBox(10, btnSortearCuartos, btnMostrarEliminatoria);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        getChildren().add(box);
    }
}
