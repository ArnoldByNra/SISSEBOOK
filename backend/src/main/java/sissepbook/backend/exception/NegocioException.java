package sissepbook.backend.exception;

/**
 * QUÉ SE VA A HACER AQUÍ:
 * - Error de regla de negocio (ej. 'ejemplar ya prestado', lector suspendido).
 * - Se lanza cuando un trigger o SP devuelve mensaje de error.
 */
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) { super(mensaje); }
}
