package sissepbook.backend.security;

/**
 * Usuario que inició sesión en esta aplicación. Como el programa es de escritorio y hay un solo
 * usuario a la vez, se guarda en memoria (no hace falta token).
 */
public final class SesionActual {

    /** Datos del usuario autenticado. */
    public record Usuario(int idUsuario, String nombreUsuario, String nombreCompleto, String rol) {
    }

    private static volatile Usuario actual;

    private SesionActual() {
    }

    public static void iniciar(Usuario usuario) {
        actual = usuario;
    }

    public static void cerrar() {
        actual = null;
    }

    /** Usuario autenticado o null si nadie ha iniciado sesión. */
    public static Usuario actual() {
        return actual;
    }
}
