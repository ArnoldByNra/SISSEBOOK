
package sissepbook.frontend.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navegador {

    private static final String BASE = "/sissepbook/frontend/";
    private static Stage stage;

    private Navegador() {}

    public static void init(Stage s) {
        stage = s;
    }

    public static Parent nodo(String vista) {
        String ruta = BASE + "view/" + vista + ".fxml";

        URL url = Navegador.class.getResource(ruta);

        if (url == null) {
            throw new IllegalArgumentException(
                    "No se encontró el archivo FXML: " + ruta
            );
        }

        try {
            return FXMLLoader.load(url);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void ir(String vista) {
        Scene scene = new Scene(nodo(vista));

        URL css = Navegador.class.getResource(
                BASE + "css/estilos.css"
        );

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setScene(scene);
        stage.show();
    }
}
