package sissepbook.frontend.session;

/** Guarda token, nombre y rol del usuario logueado. */
public final class Sesion {

    private static String token;
    private static int idUsuario;
    private static String nombreUsuario;
    private static String nombreCompleto;
    private static String rol;

    private Sesion() {
    }

    public static void iniciar(String token, int idUsuario, String nombreUsuario, String nombreCompleto, String rol) {
        Sesion.token = token;
        Sesion.idUsuario = idUsuario;
        Sesion.nombreUsuario = nombreUsuario;
        Sesion.nombreCompleto = nombreCompleto;
        Sesion.rol = rol;
    }

    public static void cerrar() {
        token = null;
        idUsuario = 0;
        nombreUsuario = null;
        nombreCompleto = null;
        rol = null;
    }

    public static boolean activa() {
        return token != null;
    }

    public static String getToken() { return token; }
    public static int getIdUsuario() { return idUsuario; }
    public static String getNombreUsuario() { return nombreUsuario; }
    public static String getNombreCompleto() { return nombreCompleto; }
    public static String getRol() { return rol; }
}
