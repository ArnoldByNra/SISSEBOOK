package sissepbook.frontend.session;

/** Guarda nombre y rol del usuario logueado (para mostrar el menú según el rol). */
public final class Sesion {

    private static int idUsuario;
    private static String nombreUsuario;
    private static String nombreCompleto;
    private static String rol;

    private Sesion() {
    }

    public static void iniciar(int idUsuario, String nombreUsuario, String nombreCompleto, String rol) {
        Sesion.idUsuario = idUsuario;
        Sesion.nombreUsuario = nombreUsuario;
        Sesion.nombreCompleto = nombreCompleto;
        Sesion.rol = rol;
    }

    public static void cerrar() {
        idUsuario = 0;
        nombreUsuario = null;
        nombreCompleto = null;
        rol = null;
    }

    public static boolean activa() {
        return nombreUsuario != null;
    }

    public static int getIdUsuario() { return idUsuario; }
    public static String getNombreUsuario() { return nombreUsuario; }
    public static String getNombreCompleto() { return nombreCompleto; }
    public static String getRol() { return rol; }
}
