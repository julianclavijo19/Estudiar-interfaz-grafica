package com.ejemplo.view;

import com.ejemplo.model.Torneo;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PanelPrincipal extends BorderPane {

    private final Torneo torneo;

    public PanelPrincipal(Torneo torneo) {
        this.torneo = torneo;
        setPadding(new Insets(10));

        VBox menu = new VBox(15);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #2e3b4e;");
        menu.setPrefWidth(200);

        Button btnEquipos = new Button("🏟 Equipos");
        Button btnPartidos = new Button("📅 Partidos");
        Button btnTabla = new Button("📊 Tabla");
        Button btnEliminatoria = new Button("🏆 Eliminatoria");

        // Cambiar vista central al hacer clic
        btnEquipos.setOnAction(e -> setCenter(new VistaEquipos(torneo)));
        btnPartidos.setOnAction(e -> setCenter(new VistaPartidos(torneo)));
        btnTabla.setOnAction(e -> setCenter(new VistaTabla(torneo)));
        btnEliminatoria.setOnAction(e -> setCenter(new VistaEliminatoria(torneo)));

        menu.getChildren().addAll(btnEquipos, btnPartidos, btnTabla, btnEliminatoria);

        setLeft(menu);
    }
}
