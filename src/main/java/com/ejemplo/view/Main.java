package com.ejemplo.view;

import com.ejemplo.DatosIniciales;
import com.ejemplo.model.Torneo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Torneo torneo = new Torneo("Copa Universitaria");
            DatosIniciales.cargarEquiposYJugadores(torneo);

        PanelPrincipal panelPrincipal = new PanelPrincipal(torneo);

        Scene scene = new Scene(panelPrincipal, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/com/ejemplo/view/style.css").toExternalForm());

        stage.setTitle("⚽ Gestor de Torneos - JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
