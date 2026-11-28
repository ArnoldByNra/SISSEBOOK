package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.session.Permisos;
import sissepbook.frontend.session.Sesion;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;
import sissepbook.frontend.util.Tablas;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.Map;

/** Dashboard: resumen de la biblioteca (consulta agregada) y accesos rápidos. */
public class DashboardView extends Pagina {

    private final JLabel lblEjemplares = new JLabel("0");
    private final JLabel lblDisponibles = new JLabel("0");
    private final JLabel lblPorcentaje = new JLabel(" ");
    private final JLabel lblPrestados = new JLabel("0");
    private final JLabel lblAtrasados = new JLabel("0");

    public DashboardView() {
        super("Dashboard", "Bienvenido, " + (Sesion.getNombreCompleto() != null ? Sesion.getNombreCompleto() : "usuario"));

        arriba(Comp.columnas(16,
                Comp.stat("Libros registrados", lblEjemplares, null),
                Comp.stat("Libros disponibles", lblDisponibles, lblPorcentaje),
                Comp.stat("Libros prestados", lblPrestados, null),
                Comp.stat("Libros atrasados", lblAtrasados, null)));

        arriba(Comp.seccion("Accesos rápidos"));
        JPanel acciones = Comp.fila(12);
        if (Permisos.puedeGestionar()) {
            Boton prestamo = Comp.boton("Registrar un préstamo", true);
            prestamo.addActionListener(e -> Navegador.ir("PrestamoView"));
            Boton devolucion = Comp.boton("Registrar una devolución", true);
            devolucion.addActionListener(e -> Navegador.ir("DevolucionView"));
            acciones.add(prestamo);
            acciones.add(devolucion);
        }
        Boton disp = Comp.boton("Consultar disponibilidad", false);
        disp.addActionListener(e -> Navegador.ir("DisponibilidadView"));
        Boton atr = Comp.boton("Ver libros atrasados", false);
        atr.addActionListener(e -> Navegador.ir("AtrasadosView"));
        acciones.add(disp);
        acciones.add(atr);
        arriba(acciones);
        cargar();
    }

    private void cargar() {
        try {
            Map<String, Object> d = Datos.dashboard();
            int total = Tablas.entero(d, "ejemplares");
            int disp = Tablas.entero(d, "disponibles");
            lblEjemplares.setText(String.valueOf(total));
            lblDisponibles.setText(String.valueOf(disp));
            lblPorcentaje.setText(total == 0 ? "0%" : String.format("%.1f%% del catálogo", disp * 100.0 / total));
            lblPrestados.setText(String.valueOf(Tablas.entero(d, "prestados")));
            lblAtrasados.setText(String.valueOf(Tablas.entero(d, "atrasados")));
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }
}
