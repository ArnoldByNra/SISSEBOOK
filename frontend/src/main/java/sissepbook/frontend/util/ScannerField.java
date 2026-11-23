package sissepbook.frontend.util;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.function.Consumer;

/**
 * Helper para campos de escaneo: el lector USB (teclado HID) escribe el código
 * y envía ENTER. Al presionar ENTER se ejecuta la acción con el código y se
 * limpia el campo, manteniendo el foco para el siguiente escaneo.
 */
public final class ScannerField {

    private ScannerField() {
    }

    public static void vincular(TextField campo, Consumer<String> accion) {
        campo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String codigo = campo.getText();
                campo.clear();
                if (codigo != null && !codigo.isBlank()) {
                    accion.accept(codigo.trim());
                }
                event.consume();
            }
        });
        // Asegura el foco al entrar a la pantalla
        Platform.runLater(campo::requestFocus);
    }

    /** Vuelve a poner el foco en el campo de escaneo. */
    public static void enfocar(TextField campo) {
        Platform.runLater(campo::requestFocus);
    }
}
