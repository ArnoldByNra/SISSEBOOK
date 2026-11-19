package sissepbook.frontend.session;

/** Datos del usuario logueado. */
public final class Sesion {
    public static String token;
    public static String nombre;
    public static String rol; // ADMINISTRADOR | OPERADOR | CONSULTA

    private Sesion() {}

    public static void cerrar() { token = null; nombre = null; rol = null; }
}
