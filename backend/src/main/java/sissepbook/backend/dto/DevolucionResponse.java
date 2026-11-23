package sissepbook.backend.dto;

/** Respuesta del registro de devolución. */
public class DevolucionResponse {

    private String codigoBarras;
    private int diasRetraso;
    private String mensaje;

    public DevolucionResponse() {
    }

    public DevolucionResponse(String codigoBarras, int diasRetraso, String mensaje) {
        this.codigoBarras = codigoBarras;
        this.diasRetraso = diasRetraso;
        this.mensaje = mensaje;
    }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public int getDiasRetraso() { return diasRetraso; }
    public void setDiasRetraso(int diasRetraso) { this.diasRetraso = diasRetraso; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
