package sissepbook.frontend.api;

/** Error devuelto por el backend (código + mensaje) para mostrarlo con Alertas. */
public class ApiException extends RuntimeException {

    private final int codigo;

    public ApiException(int codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public ApiException(String mensaje) {
        this(0, mensaje);
    }

    public int getCodigo() {
        return codigo;
    }

    public boolean esNoAutenticado() {
        return codigo == 401;
    }
}
