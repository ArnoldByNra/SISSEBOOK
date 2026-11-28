package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.session.Permisos;
import sissepbook.frontend.session.Sesion;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Marco principal: menú lateral + barra superior + contenido.
 * Navegador.ir(...) muestra las pantallas aquí dentro mientras haya sesión.
 */
public final class ShellView {

    private static ShellView actual;

    private final JPanel contenido = new JPanel(new BorderLayout());
    private final Map<Boton, String> botones = new LinkedHashMap<>();

    private ShellView() {
    }

    public static JComponent crear() {
        actual = new ShellView();
        return actual.construir();
    }

    public static boolean mostrarEnShell(String vista) {
        if (actual == null) {
            return false;
        }
        actual.mostrar(vista);
        return true;
    }

    public static void limpiar() {
        actual = null;
    }

    private JComponent construir() {
        boolean gestion = Permisos.puedeGestionar();

        JPanel menu = new JPanel();
        menu.setBackground(Tema.AZUL_OSCURO);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(24, 14, 24, 14));
        menu.setPreferredSize(new Dimension(250, 10));
        menu.add(izquierda(Comp.etiqueta("BIBLIO", Font.BOLD, 20, java.awt.Color.WHITE)));
        JComponent sub = Comp.etiqueta("Sistema de gestión", Font.PLAIN, 11, new java.awt.Color(0xb8c2d8));
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        menu.add(izquierda(sub));
        menu.add(seccion("PRINCIPAL"));
        item(menu, "Dashboard", "DashboardView");
        item(menu, "Registro de libros", "LibrosView");
        if (gestion) {
            item(menu, "Préstamos", "PrestamoView");
            item(menu, "Devoluciones", "DevolucionView");
        }
        item(menu, "Disponibilidad", "DisponibilidadView");
        item(menu, "Libros atrasados", "AtrasadosView");
        item(menu, "Reportes", "ReportesView");
        item(menu, "Consultar código", "ConsultaCodigoView");
        if (gestion) {
            menu.add(seccion("CATÁLOGO"));
            item(menu, "Ejemplares", "EjemplaresView");
            item(menu, "Lectores", "LectoresView");
            menu.add(seccion("ADMINISTRACIÓN"));
            item(menu, "Historial", "HistorialView");
        }
        if (Permisos.puedeAdministrarUsuarios()) {
            item(menu, "Usuarios", "UsuariosView");
        }
        if (Permisos.puedeVerBitacora()) {
            item(menu, "Bitácora", "BitacoraView");
        }
        menu.add(Box.createVerticalGlue());
        Boton salir = new Boton("Cerrar sesión", Boton.Estilo.NAV);
        salir.addActionListener(e -> cerrarSesion());
        ancho(salir);
        menu.add(salir);

        JLabel t1 = Comp.etiqueta("Biblioteca Central", Font.BOLD, 18, Tema.TEXTO);
        JLabel t2 = Comp.etiqueta("Sistema de Gestión de Biblioteca", Font.PLAIN, 11, Tema.TEXTO_SUAVE);
        JLabel usuario = Comp.etiqueta(Sesion.getNombreCompleto() != null
                ? Sesion.getNombreCompleto() + " · " + Sesion.getRol() : "Usuario", Font.PLAIN, 13, Tema.TEXTO_SUAVE);
        JPanel titulos = Comp.columna(0, t1, t2);
        JPanel barra = new JPanel(new BorderLayout(16, 0));
        barra.setBackground(java.awt.Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE),
                BorderFactory.createEmptyBorder(12, 22, 12, 22)));
        barra.add(titulos, BorderLayout.WEST);
        barra.add(usuario, BorderLayout.EAST);

        contenido.setBackground(Tema.FONDO);
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(menu, BorderLayout.WEST);
        raiz.add(barra, BorderLayout.NORTH);
        raiz.add(contenido, BorderLayout.CENTER);
        mostrar("DashboardView");
        return raiz;
    }


    private static JComponent izquierda(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private static JComponent seccion(String texto) {
        JLabel l = Comp.etiqueta(texto, Font.PLAIN, 11, new java.awt.Color(0x8ea2c6));
        l.setBorder(BorderFactory.createEmptyBorder(14, 4, 4, 0));
        return izquierda(l);
    }

    private static void ancho(Boton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, b.getPreferredSize().height));
    }

    private void item(JPanel menu, String texto, String vista) {
        Boton b = new Boton(texto, Boton.Estilo.NAV);
        b.addActionListener(e -> mostrar(vista));
        ancho(b);
        botones.put(b, vista);
        menu.add(b);
        menu.add(Box.createVerticalStrut(4));
    }

    private void mostrar(String vista) {
        try {
            JComponent nodo = Vistas.crear(vista);
            contenido.removeAll();
            contenido.add(nodo, BorderLayout.CENTER);
            contenido.revalidate();
            contenido.repaint();
            botones.forEach((b, v) -> b.setEstilo(v.equals(vista) ? Boton.Estilo.NAV_ACTIVO : Boton.Estilo.NAV));
        } catch (RuntimeException e) {
            Alertas.error("No se pudo abrir la pantalla '" + vista + "': " + e.getMessage());
        }
    }

    private void cerrarSesion() {
        if (Alertas.confirmar("¿Deseas cerrar la sesión?")) {
            Datos.logout();
            Sesion.cerrar();
            Navegador.ir("LoginView");
        }
    }
}
