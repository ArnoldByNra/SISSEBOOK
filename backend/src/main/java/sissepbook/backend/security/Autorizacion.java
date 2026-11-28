package sissepbook.backend.security;

import sissepbook.backend.exception.NegocioException;

/**
 * Permisos por rol, revisados dentro de los servicios (ya no hay API ni interceptor).
 * - Administrador: todo.
 * - Operador: catálogo, lectores, préstamos, devoluciones, historial y reportes (no usuarios ni bitácora).
 * - Consulta: solo lectura de catálogo, disponibilidad, atrasados y reportes.
 */
public final class Autorizacion {

    public static final String ADMIN = "Administrador";
    public static final String OPERADOR = "Operador";
    public static final String CONSULTA = "Consulta";

    private Autorizacion() {
    }

    /** Cualquier usuario con sesión iniciada. */
    public static SesionActual.Usuario exigirSesion() {
        SesionActual.Usuario u = SesionActual.actual();
        if (u == null) {
            throw new NegocioException("Debes iniciar sesión para realizar esta operación.");
        }
        return u;
    }

    /** Administrador u Operador. */
    public static SesionActual.Usuario exigirGestion() {
        SesionActual.Usuario u = exigirSesion();
        if (!ADMIN.equals(u.rol()) && !OPERADOR.equals(u.rol())) {
            throw new NegocioException("No tienes permisos para realizar esta operación.");
        }
        return u;
    }

    /** Solo Administrador. */
    public static SesionActual.Usuario exigirAdmin() {
        SesionActual.Usuario u = exigirSesion();
        if (!ADMIN.equals(u.rol())) {
            throw new NegocioException("Solo un administrador puede realizar esta operación.");
        }
        return u;
    }
}
