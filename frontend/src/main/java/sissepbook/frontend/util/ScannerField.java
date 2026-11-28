package sissepbook.frontend.util;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.util.function.Consumer;

/**
 * Helper para campos de escaneo: el lector USB (teclado HID) escribe el código y envía ENTER.
 * Al presionar ENTER se ejecuta la acción con el código y se limpia el campo, manteniendo el foco
 * para el siguiente escaneo.
 */
public final class ScannerField {

    private ScannerField() {
    }

    public static void vincular(JTextField campo, Consumer<String> accion) {
        campo.addActionListener(e -> {
            String codigo = campo.getText();
            campo.setText("");
            if (codigo != null && !codigo.isBlank()) {
                accion.accept(codigo.trim());
            }
        });
        // Asegura el foco al entrar a la pantalla
        campo.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                enfocar(campo);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    /** Vuelve a poner el foco en el campo de escaneo. */
    public static void enfocar(JTextField campo) {
        SwingUtilities.invokeLater(campo::requestFocusInWindow);
    }
}
