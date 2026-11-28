package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.ui.dialogos.FormularioLector;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Tablas;

import javax.swing.JTextField;
import java.awt.Dimension;
import java.util.function.Function;

/** Lectores: tabla, búsqueda, alta y edición. */
public class LectoresView extends Pagina {

    private final JTextField campoBusqueda = new JTextField();
    private final Tabla<Modelos.Lector> tabla;

    public LectoresView() {
        super("Lectores", "Registra y consulta a las personas que usan la biblioteca.");
        campoBusqueda.setToolTipText("Buscar matrícula/nombre");
        campoBusqueda.setPreferredSize(new Dimension(280, 32));
        campoBusqueda.addActionListener(e -> cargar());
        Boton buscar = Comp.boton("Buscar", false);
        buscar.addActionListener(e -> cargar());
        Boton nuevo = Comp.boton("Nuevo", true);
        nuevo.addActionListener(e -> nuevo());
        Boton editar = Comp.boton("Editar", false);
        editar.addActionListener(e -> editar());
        arriba(Comp.barra(Comp.fila(10, campoBusqueda, buscar), Comp.fila(10, nuevo, editar)));

        tabla = new Tabla<>(
                c("ID", l -> l.idLector), c("Matrícula", l -> l.matricula), c("Nombre", l -> l.nombreCompleto),
                c("Carrera/Área", l -> l.carreraOArea), c("Correo", l -> l.correo),
                c("Estado", l -> l.estadoLector), c("Máx. préstamos", l -> l.maxPrestamos));
        centro(tabla.componente());
        cargar();
    }

    private static Tablas.Col<Modelos.Lector> c(String t, Function<Modelos.Lector, Object> f) {
        return Tablas.colDe(t, f);
    }

    private void cargar() {
        try {
            tabla.setItems(Datos.lectores(campoBusqueda.getText()));
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void nuevo() {
        Modelos.Lector lector = FormularioLector.mostrar(null);
        if (lector == null) {
            return;
        }
        try {
            Datos.crearLector(lector);
            cargar();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void editar() {
        Modelos.Lector sel = tabla.seleccionado();
        if (sel == null) {
            Alertas.advertencia("Selecciona un lector para editar.");
            return;
        }
        Modelos.Lector editado = FormularioLector.mostrar(sel);
        if (editado == null) {
            return;
        }
        try {
            Datos.actualizarLector(sel.idLector, editado);
            cargar();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }
}
