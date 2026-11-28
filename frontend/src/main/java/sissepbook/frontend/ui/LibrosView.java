package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.session.Permisos;
import sissepbook.frontend.ui.dialogos.FormularioLibro;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Tablas;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.util.function.Function;

/** Registro de libros: tabla, búsqueda y alta/edición/baja. */
public class LibrosView extends Pagina {

    private final JTextField campoBusqueda = new JTextField();
    private final Tabla<Modelos.Libro> tabla;

    public LibrosView() {
        super("Registro de libros", "Organiza, consulta y administra tu catálogo bibliográfico.");
        campoBusqueda.setToolTipText("Buscar título/autor/ISBN");
        campoBusqueda.setPreferredSize(new Dimension(280, 32));
        campoBusqueda.addActionListener(e -> cargar());
        Boton buscar = Comp.boton("Buscar", false);
        buscar.addActionListener(e -> cargar());
        JPanel izq = Comp.fila(10, campoBusqueda, buscar);
        JPanel der = Comp.fila(10);
        if (Permisos.puedeGestionar()) {
            Boton nuevo = Comp.boton("Nuevo", true);
            nuevo.addActionListener(e -> nuevo());
            Boton editar = Comp.boton("Editar", false);
            editar.addActionListener(e -> editar());
            Boton baja = Comp.peligro("Dar de baja");
            baja.addActionListener(e -> desactivar());
            der.add(nuevo);
            der.add(editar);
            der.add(baja);
        }
        arriba(Comp.barra(izq, der));

        tabla = new Tabla<>(
                c("ID", l -> l.idLibro), c("Título", l -> l.titulo), c("Autor", l -> l.autor),
                c("Editorial", l -> l.editorial), c("ISBN", l -> l.isbn), c("Categoría", l -> l.nombreCategoria),
                c("Año", l -> l.anioPublicacion), c("Disponibles", l -> l.ejemplaresDisponibles),
                c("Activo", l -> l.activo ? "Sí" : "No"));
        centro(tabla.componente());
        cargar();
    }

    private static Tablas.Col<Modelos.Libro> c(String t, Function<Modelos.Libro, Object> f) {
        return Tablas.colDe(t, f);
    }

    private void cargar() {
        try {
            tabla.setItems(Datos.libros(campoBusqueda.getText()));
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void nuevo() {
        Modelos.Libro libro = FormularioLibro.mostrar(null);
        if (libro == null) {
            return;
        }
        try {
            Datos.crearLibro(libro);
            cargar();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void editar() {
        Modelos.Libro sel = tabla.seleccionado();
        if (sel == null) {
            Alertas.advertencia("Selecciona un libro para editar.");
            return;
        }
        Modelos.Libro editado = FormularioLibro.mostrar(sel);
        if (editado == null) {
            return;
        }
        try {
            Datos.actualizarLibro(sel.idLibro, editado);
            cargar();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void desactivar() {
        Modelos.Libro sel = tabla.seleccionado();
        if (sel == null) {
            Alertas.advertencia("Selecciona un libro para dar de baja.");
            return;
        }
        if (!Alertas.confirmar("¿Dar de baja el libro \"" + sel.titulo + "\"?")) {
            return;
        }
        try {
            Datos.eliminarLibro(sel.idLibro);
            cargar();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }
}
