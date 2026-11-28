package sissepbook.frontend.ui;

import javax.swing.JPanel;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;

/** Fondo en degradado azul de las pantallas de conexión y login; centra su contenido. */
class Fondo extends JPanel {

    Fondo() {
        super(new GridBagLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, Tema.AZUL_BARRA, getWidth(), getHeight(), Tema.AZUL));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
