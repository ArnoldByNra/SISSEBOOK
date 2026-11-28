package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.ui.dialogos.FormularioUsuario;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Tablas;

import java.util.function.Function;

/** Usuarios del sistema: alta y edición (solo administrador). */
public class UsuariosView extends Pagina {

    private final Tabla<Modelos.Usuario> tabla;

    public UsuariosView() {
        super("Usuarios del sistema", "Administra las cuentas y los roles de acceso.");
        Boton nuevo = Comp.boton("Nuevo usuario", true);
        nuevo.addActionListener(e -> nuevo());
        Boton editar = Comp.boton("Editar", false);
        editar.addActionListener(e -> editar());
        arriba(Comp.fila(10, nuevo, editar));

        tabla = new Tabla<>(
                c("ID", u -> u.idUsuario), c("Nombre completo", u -> u.nombreCompleto),
                c("Usuario", u -> u.nombreUsuario), c("Rol", u -> u.nombreRol),
                c("Activo", u -> u.activo ? "Sí" : "No"));
        centro(tabla.componente());
        cargar();
    }

    private static Tablas.Col<Modelos.Usuario> c(String t, Function<Modelos.Usuario, Object> f) {
        return Tablas.colDe(t, f);
    }

    private void cargar() {
        try {
            tabla.setItems(Datos.usuarios());
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void nuevo() {
        Modelos.Usuario usuario = FormularioUsuario.mostrar(null);
        if (usuario == null) {
            return;
        }
        try {
            Datos.crearUsuario(usuario);
            cargar();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void editar() {
        Modelos.Usuario sel = tabla.seleccionado();
        if (sel == null) {
            Alertas.advertencia("Selecciona un usuario para editar.");
            return;
        }
        sel.contrasena = null;
        Modelos.Usuario editado = FormularioUsuario.mostrar(sel);
        if (editado == null) {
            return;
        }
        try {
            Datos.actualizarUsuario(sel.idUsuario, editado);
            cargar();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }
}
