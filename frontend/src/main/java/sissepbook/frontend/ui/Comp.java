package sissepbook.frontend.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

/** Componentes y acomodos reutilizables del diseño (los colores viven en {@link Tema}). */
final class Comp {

    private Comp() {
    }

    static Boton boton(String texto, boolean primario) {
        return new Boton(texto, primario ? Boton.Estilo.PRIMARIO : Boton.Estilo.SECUNDARIO);
    }

    static Boton peligro(String texto) {
        return new Boton(texto, Boton.Estilo.PELIGRO);
    }

    // ---------------------------------------------------------------- etiquetas

    static JLabel etiqueta(String texto, int estilo, float tamano, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.fuente(estilo, tamano));
        l.setForeground(color);
        return l;
    }

    static JLabel seccion(String texto) {
        return etiqueta(texto, Font.BOLD, 16, Tema.TEXTO);
    }

    static JLabel suave(String texto) {
        return etiqueta(texto, Font.PLAIN, 12, Tema.TEXTO_SUAVE);
    }

    static JLabel normal(String texto) {
        return etiqueta(texto, Font.PLAIN, 13, Tema.TEXTO);
    }

    static void estiloEstado(JLabel l, Color color, boolean negrita) {
        l.setForeground(color);
        l.setFont(Tema.fuente(negrita ? Font.BOLD : Font.PLAIN, l.getFont().getSize2D()));
    }

    // ---------------------------------------------------------------- acomodos

    /** Fila horizontal alineada a la izquierda con separación entre elementos. */
    static JPanel fila(int espacio, Component... partes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, espacio, 0));
        p.setOpaque(false);
        // FlowLayout también deja el hueco al borde izquierdo: se compensa para alinear con el título
        p.setBorder(BorderFactory.createEmptyBorder(0, -espacio, 0, 0));
        for (Component c : partes) {
            p.add(c);
        }
        return p;
    }

    /** Barra con elementos a la izquierda y a la derecha (el espacio sobrante queda en medio). */
    static JPanel barra(JComponent izquierda, JComponent derecha) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        if (izquierda != null) {
            p.add(izquierda, BorderLayout.WEST);
        }
        if (derecha != null) {
            p.add(derecha, BorderLayout.EAST);
        }
        return p;
    }

    /** Columnas del mismo ancho (tarjetas de estadística). */
    static JPanel columnas(int separacion, Component... partes) {
        JPanel p = new JPanel(new GridLayout(1, partes.length, separacion, 0));
        p.setOpaque(false);
        for (Component c : partes) {
            p.add(c);
        }
        return p;
    }

    /** Columna vertical sin fondo. */
    static JPanel columna(int separacion, Component... partes) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        boolean primero = true;
        for (Component c : partes) {
            if (!primero && separacion > 0) {
                p.add(Box.createVerticalStrut(separacion));
            }
            ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(c);
            primero = false;
        }
        return p;
    }

    /** Tarjeta de estadística. {@code sub} puede ser null. */
    static Tarjeta stat(String etiqueta, JLabel valor, JLabel sub) {
        Tarjeta c = new Tarjeta(null);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createEmptyBorder(18, 18, 20, 18));
        JLabel e = etiqueta(etiqueta, Font.PLAIN, 13, Tema.TEXTO_SUAVE);
        valor.setFont(Tema.fuente(Font.BOLD, 30));
        valor.setForeground(Tema.AZUL_OSCURO);
        e.setAlignmentX(Component.LEFT_ALIGNMENT);
        valor.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(e);
        c.add(Box.createVerticalStrut(4));
        c.add(valor);
        if (sub != null) {
            sub.setFont(Tema.fuente(Font.PLAIN, 12));
            sub.setForeground(Tema.TEXTO_SUAVE);
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.add(Box.createVerticalStrut(4));
            c.add(sub);
        }
        return c;
    }

    /** Tarjeta vacía con relleno, para armar paneles propios. */
    static Tarjeta tarjeta(int relleno) {
        Tarjeta c = new Tarjeta(null);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createEmptyBorder(relleno, relleno, relleno + 2, relleno));
        return c;
    }

    /** Formulario de dos columnas: etiqueta y campo. */
    static JPanel formulario(String[] etiquetas, JComponent[] campos) {
        JPanel grid = new JPanel(new GridBagLayout()) {
            @Override
            public java.awt.Dimension getMaximumSize() {
                return new java.awt.Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < etiquetas.length; i++) {
            c.gridy = i;
            c.gridx = 0;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            if (etiquetas[i] != null) {
                grid.add(new JLabel(etiquetas[i]), c);
            }
            c.gridx = etiquetas[i] == null ? 0 : 1;
            c.gridwidth = etiquetas[i] == null ? 2 : 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            grid.add(campos[i], c);
            c.gridwidth = 1;
        }
        return grid;
    }
}
