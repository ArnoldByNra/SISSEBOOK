package sissepbook.frontend.util;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

/** Mensajes de error, información y confirmación (JOptionPane de Swing). */
public final class Alertas {

    private Alertas() {
    }

    public static void error(String mensaje) {
        JOptionPane.showMessageDialog(Navegador.frame(), mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(String mensaje) {
        JOptionPane.showMessageDialog(Navegador.frame(), mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(String mensaje) {
        JOptionPane.showMessageDialog(Navegador.frame(), mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(Navegador.frame(), mensaje, "Confirmar",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
    }

    /** Muestra un formulario modal con Aceptar/Cancelar; devuelve true si se aceptó. */
    public static boolean formulario(String titulo, JComponent contenido) {
        return JOptionPane.showConfirmDialog(Navegador.frame(), contenido, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }
}
