package sissepbook.frontend.api;

/**
 * QUÉ SE VA A HACER AQUÍ:
 * - Error devuelto por el backend (código + mensaje) para mostrarlo con Alertas.
 */
public class ApiException extends RuntimeException {
    public ApiException(String m) { super(m); }
}
