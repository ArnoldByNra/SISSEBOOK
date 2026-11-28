package sissepbook.frontend.ui;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

/** Colores y fuentes del diseño SISSE (equivalente al antiguo estilos.css). */
public final class Tema {

    public static final Color FONDO = new Color(0xf4f6f8);
    public static final Color AZUL_OSCURO = new Color(0x0f1f3d);
    public static final Color AZUL_BARRA = new Color(0x1e3a5f);
    public static final Color AZUL = new Color(0x2f5d8a);
    public static final Color AZUL_HOVER = new Color(0x3a6ea5);
    public static final Color MENU_HOVER = new Color(0x1b2f57);
    public static final Color VERDE = new Color(0x4ade80);
    public static final Color TEXTO = new Color(0x1f2937);
    public static final Color TEXTO_SUAVE = new Color(0x6b7280);
    public static final Color BORDE = new Color(0xdfe6ee);
    public static final Color ENCABEZADO_TABLA = new Color(0xeaf0f6);
    public static final Color OK = new Color(0x1e8449);
    public static final Color ALERTA = new Color(0xb9770e);
    public static final Color ERROR = new Color(0xc0392b);
    public static final Color BANNER = new Color(0xfef3c7);
    public static final Color BANNER_TEXTO = new Color(0x92400e);

    private static final String FAMILIA = "SansSerif";

    private Tema() {
    }

    public static Font fuente(int estilo, float tamano) {
        return new Font(FAMILIA, estilo, 1).deriveFont(estilo, tamano);
    }

    /** Aplica el aspecto general y los textos en español de los diálogos. */
    public static void instalar() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // se queda con el aspecto por defecto
        }
        UIManager.put("OptionPane.okButtonText", "Aceptar");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.messageFont", fuente(Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", fuente(Font.PLAIN, 13));
        UIManager.put("Label.font", fuente(Font.PLAIN, 13));
        UIManager.put("TextField.font", fuente(Font.PLAIN, 13));
        UIManager.put("PasswordField.font", fuente(Font.PLAIN, 13));
        UIManager.put("TextArea.font", fuente(Font.PLAIN, 13));
        UIManager.put("ComboBox.font", fuente(Font.PLAIN, 13));
        UIManager.put("CheckBox.font", fuente(Font.PLAIN, 13));
        UIManager.put("Spinner.font", fuente(Font.PLAIN, 13));
        UIManager.put("FileChooser.openButtonText", "Abrir");
        UIManager.put("FileChooser.saveButtonText", "Guardar");
        UIManager.put("FileChooser.cancelButtonText", "Cancelar");
    }
}
