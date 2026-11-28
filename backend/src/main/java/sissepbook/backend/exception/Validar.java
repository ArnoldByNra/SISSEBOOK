package sissepbook.backend.exception;

/** Validaciones de campos obligatorios (antes las hacía @Valid en los controllers). */
public final class Validar {

    private Validar() {
    }

    public static void texto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new NegocioException(mensaje);
        }
    }

    public static void objeto(Object valor, String mensaje) {
        if (valor == null) {
            throw new NegocioException(mensaje);
        }
    }
}
