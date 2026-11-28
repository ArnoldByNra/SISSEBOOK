package sissepbook.frontend.ui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Botón redondeado pintado a mano: se ve igual en cualquier sistema operativo. */
public final class Boton extends JButton {

    public enum Estilo { PRIMARIO, SECUNDARIO, PELIGRO, NAV, NAV_ACTIVO }

    private Estilo estilo;
    private boolean encima;

    public Boton(String texto, Estilo estilo) {
        super(texto);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }
        });
        setEstilo(estilo);
    }

    public void setEstilo(Estilo nuevo) {
        this.estilo = nuevo;
        boolean negrita = nuevo == Estilo.PRIMARIO || nuevo == Estilo.PELIGRO || nuevo == Estilo.NAV_ACTIVO;
        setFont(Tema.fuente(negrita ? Font.BOLD : Font.PLAIN, 13));
        switch (nuevo) {
            case PRIMARIO, PELIGRO -> setForeground(Color.WHITE);
            case SECUNDARIO -> setForeground(Tema.AZUL_BARRA);
            case NAV -> setForeground(new Color(0xdbe4f5));
            case NAV_ACTIVO -> setForeground(Tema.AZUL_OSCURO);
        }
        if (nuevo == Estilo.NAV || nuevo == Estilo.NAV_ACTIVO) {
            setHorizontalAlignment(LEFT);
        }
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        boolean nav = estilo == Estilo.NAV || estilo == Estilo.NAV_ACTIVO;
        return new Dimension(d.width + (nav ? 28 : 32), d.height + (nav ? 14 : 12));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fondo = switch (estilo) {
            case PRIMARIO -> encima ? Tema.AZUL_HOVER : Tema.AZUL;
            case PELIGRO -> encima ? Tema.ERROR.darker() : Tema.ERROR;
            case SECUNDARIO -> encima ? new Color(0xeaf0f6) : Color.WHITE;
            case NAV -> encima ? Tema.MENU_HOVER : null;
            case NAV_ACTIVO -> Tema.VERDE;
        };
        int arco = (estilo == Estilo.NAV || estilo == Estilo.NAV_ACTIVO) ? 12 : 10;
        if (fondo != null) {
            g2.setColor(isEnabled() ? fondo : fondo.brighter());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);
        }
        if (estilo == Estilo.SECUNDARIO) {
            g2.setColor(new Color(0x9bb6d0));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
