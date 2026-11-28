package sissepbook.frontend.util;

import sissepbook.frontend.ui.ShellView;
import sissepbook.frontend.ui.Vistas;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Dimension;

/**
 * Cambia de pantalla: ir("LibrosView"). Todas las pantallas están hechas en código (ui/Vistas).
 * "MainView" crea el marco con menú lateral; con sesión iniciada las demás pantallas se muestran dentro de él.
 */
public final class Navegador {

    private static JFrame frame;

    private Navegador() {
    }

    public static void init() {
        frame = new JFrame("SISSE - Biblioteca");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 760);
        frame.setMinimumSize(new Dimension(960, 600));
        frame.setLocationRelativeTo(null);
    }

    public static JFrame frame() {
        return frame;
    }

    public static void ir(String nombreVista) {
        if ("MainView".equals(nombreVista) && ShellView.mostrarEnShell("DashboardView")) {
            return;
        }
        if (!"MainView".equals(nombreVista) && !"LoginView".equals(nombreVista)
                && !"ConexionView".equals(nombreVista) && ShellView.mostrarEnShell(nombreVista)) {
            return;
        }
        if ("LoginView".equals(nombreVista) || "ConexionView".equals(nombreVista)) {
            ShellView.limpiar();
        }
        try {
            JComponent raiz = "MainView".equals(nombreVista) ? ShellView.crear() : Vistas.crear(nombreVista);
            frame.setContentPane(raiz);
            frame.revalidate();
            frame.repaint();
            if (!frame.isVisible()) {
                frame.setVisible(true);
            }
        } catch (RuntimeException e) {
            Alertas.error("No se pudo abrir la pantalla '" + nombreVista + "': " + e.getMessage());
        }
    }
}
