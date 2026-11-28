package sissepbook.frontend.ui;

import javax.swing.JLabel;

/** Etiqueta que ajusta su texto a un ancho fijo (varias líneas). */
final class Parrafo extends JLabel {

    private final int ancho;

    Parrafo(int ancho) {
        this.ancho = ancho;
    }

    void setTexto(String texto) {
        if (texto == null || texto.isEmpty()) {
            setText(" ");
            return;
        }
        String html = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
        setText("<html><body style='width:" + ancho + "px'>" + html + "</body></html>");
    }
}
