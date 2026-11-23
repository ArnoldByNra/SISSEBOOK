package sissepbook.frontend;

import javafx.application.Application;
import javafx.stage.Stage;
import sissepbook.frontend.util.Navegador;

/** Arranca JavaFX, guarda el Stage en Navegador y muestra LoginView. */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Navegador.init(stage);
        Navegador.ir("LoginView");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
