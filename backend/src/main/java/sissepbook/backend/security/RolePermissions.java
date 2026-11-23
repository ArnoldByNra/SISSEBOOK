package sissepbook.backend.security;

/**
 * Tabla de permisos por rol (Administrador, Operador, Consulta).
 * Evalúa si un rol puede ejecutar (método HTTP + ruta).
 * Ver docs/PANTALLAS.md y docs/ENDPOINTS.md para la matriz.
 */
public final class RolePermissions {

    public static final String ADMIN = "Administrador";
    public static final String OPERADOR = "Operador";
    public static final String CONSULTA = "Consulta";

    private RolePermissions() {
    }

    /**
     * Indica si el rol puede ejecutar la petición.
     *
     * @param rol       nombre del rol del usuario.
     * @param metodo    método HTTP (GET, POST, PUT, DELETE...).
     * @param ruta      ruta solicitada (ej. /api/libros).
     */
    public static boolean permitido(String rol, String metodo, String ruta) {
        if (rol == null || metodo == null || ruta == null) {
            return false;
        }

        String m = metodo.toUpperCase();
        boolean lectura = "GET".equals(m);
        boolean escritura = "POST".equals(m) || "PUT".equals(m) || "PATCH".equals(m) || "DELETE".equals(m);

        // Autenticación: cualquiera puede entrar/salir (login es público en el interceptor)
        if (ruta.startsWith("/api/auth")) {
            return true;
        }

        switch (rol) {
            case ADMIN:
                return true;

            case OPERADOR:
                // No gestiona usuarios ni bitácora; el resto sí.
                if (ruta.startsWith("/api/usuarios") || ruta.startsWith("/api/bitacora")) {
                    return false;
                }
                return true;

            case CONSULTA:
                // Solo lecturas de catálogo/disponibilidad y reportes.
                if (!lectura) {
                    return false;
                }
                return ruta.startsWith("/api/libros")
                        || ruta.startsWith("/api/ejemplares")
                        || ruta.startsWith("/api/lectores")
                        || ruta.startsWith("/api/reportes")
                        || ruta.startsWith("/api/prestamos");

            default:
                return false;
        }
    }
}
