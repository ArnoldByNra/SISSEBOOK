package sissepbook.frontend.session;

/** Visibilidad de botones por rol (solo UX; el backend es quien bloquea). */
public final class Permisos {
    private Permisos() {}

    public static boolean esAdmin() { return "ADMINISTRADOR".equals(Sesion.rol); }
    public static boolean puedeOperar() { return esAdmin() || "OPERADOR".equals(Sesion.rol); }
}
