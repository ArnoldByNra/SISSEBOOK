package sissepbook.frontend;

import javafx.application.Application;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.stage.Stage;
import sissepbook.frontend.util.Navegador;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("SisseBook");
        Navegador.init(stage);
        Navegador.ir("LoginView");
    }

    public static void main(String[] args) { launch(args); }
}
