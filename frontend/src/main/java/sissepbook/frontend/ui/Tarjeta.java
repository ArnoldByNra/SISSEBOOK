package sissepbook.frontend.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/** Panel blanco con esquinas redondeadas y una sombra suave (tarjetas del diseño). */
public class Tarjeta extends JPanel {

    private final int arco;

    public Tarjeta(LayoutManager layout) {
        this(layout, 14);
    }

    public Tarjeta(LayoutManager layout, int arco) {
        super(layout);
        this.arco = arco;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 14));
        g2.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 3, arco, arco);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, arco, arco);
        g2.dispose();
        super.paintComponent(g);
    }
}
