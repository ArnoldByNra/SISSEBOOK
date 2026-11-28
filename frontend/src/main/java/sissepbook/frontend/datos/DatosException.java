package sissepbook.frontend.datos;

/** Error al leer o guardar datos (regla de negocio, permisos, base de datos o conexión). El mensaje es apto para mostrarse al usuario. */
public class DatosException extends RuntimeException {

    public DatosException(String mensaje) {
        super(mensaje);
    }
}
