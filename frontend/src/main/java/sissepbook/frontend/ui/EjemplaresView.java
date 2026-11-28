package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.ui.dialogos.FormularioEjemplar;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Tablas;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.util.function.Function;

/** Ejemplares: elegir libro, ver sus ejemplares, alta con código de barras y edición. */
public class EjemplaresView extends Pagina {

    private final JComboBox<Modelos.Libro> cbLibro = new JComboBox<>();
    private final Tabla<Modelos.Ejemplar> tabla;
    private boolean cargandoLibros;

    public EjemplaresView() {
        super("Ejemplares", "Administra los ejemplares físicos de cada libro y sus códigos de barras.");
        cbLibro.setPreferredSize(new Dimension(380, 32));
        cbLibro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean sel, boolean foco) {
                super.getListCellRendererComponent(list, value, index, sel, foco);
                if (value instanceof Modelos.Libro l) {
                    setText(l.titulo + " (" + l.isbn + ")");
                }
                return this;
            }
        });
        cbLibro.addActionListener(e -> cargarEjemplares());
        Boton nuevo = Comp.boton("Nuevo ejemplar", true);
        nuevo.addActionListener(e -> nuevo());
        Boton editar = Comp.boton("Editar (estado/ubicación)", false);
        editar.addActionListener(e -> editar());
        JPanel izq = Comp.fila(10, new JLabel("Libro:"), cbLibro);
        arriba(Comp.barra(izq, Comp.fila(10, nuevo, editar)));

        tabla = new Tabla<>(
                c("ID", x -> x.idEjemplar), c("Código de barras", x -> x.codigoBarras),
                c("Código interno", x -> x.codigoInterno), c("Estado", x -> x.estado),
                c("Ubicación", x -> x.ubicacionEstante));
        centro(tabla.componente());
        cargarLibros();
    }

    private static Tablas.Col<Modelos.Ejemplar> c(String t, Function<Modelos.Ejemplar, Object> f) {
        return Tablas.colDe(t, f);
    }

    private void cargarLibros() {
        cargandoLibros = true;
        try {
            cbLibro.removeAllItems();
            for (Modelos.Libro l : Datos.libros(null)) {
                cbLibro.addItem(l);
            }
            cbLibro.setSelectedIndex(-1);
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        } finally {
            cargandoLibros = false;
        }
    }

    private void cargarEjemplares() {
        Modelos.Libro libro = (Modelos.Libro) cbLibro.getSelectedItem();
        if (cargandoLibros || libro == null) {
            return;
        }
        try {
            tabla.setItems(Datos.ejemplaresPorLibro(libro.idLibro));
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void nuevo() {
        Modelos.Libro libro = (Modelos.Libro) cbLibro.getSelectedItem();
        if (libro == null) {
            Alertas.advertencia("Selecciona primero un libro.");
            return;
        }
        Modelos.Ejemplar ejemplar = FormularioEjemplar.mostrar(null, libro.idLibro);
        if (ejemplar == null) {
            return;
        }
        try {
            Datos.crearEjemplar(ejemplar);
            cargarEjemplares();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void editar() {
        Modelos.Ejemplar sel = tabla.seleccionado();
        if (sel == null) {
            Alertas.advertencia("Selecciona un ejemplar para editar.");
            return;
        }
        Modelos.Ejemplar editado = FormularioEjemplar.mostrar(sel, sel.idLibro);
        if (editado == null) {
            return;
        }
        try {
            Datos.actualizarEjemplar(sel.idEjemplar, editado);
            cargarEjemplares();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }
}
