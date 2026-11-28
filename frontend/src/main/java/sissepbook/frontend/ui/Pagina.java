package sissepbook.frontend.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

/**
 * Base de las pantallas: fondo, margen, título y subtítulo. Los controles fijos se agregan con
 * {@link #arriba(JComponent)} y el contenido que crece (normalmente una tabla) con {@link #centro(JComponent)}.
 */
abstract class Pagina extends JPanel {

    private final JPanel pila = new JPanel();
    private boolean primero = true;

    Pagina(String titulo, String subtitulo) {
        super(new BorderLayout(0, 16));
        setBackground(Tema.FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        pila.setOpaque(false);
        pila.setLayout(new BoxLayout(pila, BoxLayout.Y_AXIS));
        add(pila, BorderLayout.NORTH);

        JComponent t = Comp.etiqueta(titulo, Font.BOLD, 26, Tema.TEXTO);
        JComponent s = Comp.etiqueta(subtitulo, Font.PLAIN, 13, Tema.TEXTO_SUAVE);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        pila.add(t);
        pila.add(Box.createVerticalStrut(2));
        pila.add(s);
    }

    /** Agrega un bloque de ancho completo debajo de lo anterior. */
    final void arriba(JComponent c) {
        pila.add(Box.createVerticalStrut(16));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        pila.add(c);
    }

    /** Contenido que ocupa el espacio restante. */
    final void centro(JComponent c) {
        add(c, BorderLayout.CENTER);
    }

    /** Cambia el contenido central (por ejemplo, otra tabla). */
    final void reemplazarCentro(JComponent c) {
        BorderLayout bl = (BorderLayout) getLayout();
        Component actual = bl.getLayoutComponent(BorderLayout.CENTER);
        if (actual != null) {
            remove(actual);
        }
        add(c, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
