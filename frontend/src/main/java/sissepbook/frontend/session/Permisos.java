package sissepbook.frontend.session;

/**
 * Según el rol, qué módulos del menú se muestran/habilitan.
 * (El backend es quien realmente bloquea; esto es solo UX).
 */
public final class Permisos {

    public static final String ADMIN = "Administrador";
    public static final String OPERADOR = "Operador";
    public static final String CONSULTA = "Consulta";

    private Permisos() {
    }

    public static boolean esAdmin() {
        return ADMIN.equals(Sesion.getRol());
    }

    /** ¿Puede gestionar libros/ejemplares/lectores/préstamos/devoluciones? */
    public static boolean puedeGestionar() {
        return esAdmin() || OPERADOR.equals(Sesion.getRol());
    }

    /** ¿Puede administrar usuarios del sistema? */
    public static boolean puedeAdministrarUsuarios() {
        return esAdmin();
    }

    /** ¿Puede ver la bitácora de auditoría? */
    public static boolean puedeVerBitacora() {
        return esAdmin();
    }

    /** Todos los roles autenticados pueden consultar disponibilidad y reportes. */
    public static boolean puedeConsultar() {
        return Sesion.activa();
    }
}
