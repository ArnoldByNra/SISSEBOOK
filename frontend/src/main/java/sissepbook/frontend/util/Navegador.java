package sissepbook.frontend.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Cambia de pantalla: ir("LibrosView") carga el FXML y su CSS.
 * Guarda el Stage principal.
 */
public final class Navegador {

    private static Stage stage;
    private static final String FXML_BASE = "/sissepbook/frontend/view/";
    private static final String CSS = "/sissepbook/frontend/css/estilos.css";
    private static final double ANCHO = 1100;
    private static final double ALTO = 720;

    private Navegador() {
    }

    public static void init(Stage stagePrincipal) {
        stage = stagePrincipal;
        stage.setTitle("SISSE - Biblioteca");
        stage.setWidth(ANCHO);
        stage.setHeight(ALTO);
    }

    public static Stage stage() {
        return stage;
    }

    /** Carga la vista indicada (nombre sin extensión) y la muestra. */
    public static void ir(String nombreVista) {
        try {
            FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(FXML_BASE + nombreVista + ".fxml"));
            Parent root = loader.load();

            Scene scene;
            if (stage.getScene() == null) {
                scene = new Scene(root, ANCHO, ALTO);
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
                scene = stage.getScene();
            }
            var css = Navegador.class.getResource(CSS);
            if (css != null && !scene.getStylesheets().contains(css.toExternalForm())) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            if (!stage.isShowing()) {
                stage.show();
            }
        } catch (IOException e) {
            Alertas.error("No se pudo abrir la pantalla '" + nombreVista + "': " + e.getMessage());
        }
    }
}
