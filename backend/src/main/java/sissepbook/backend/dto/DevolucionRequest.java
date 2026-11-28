package sissepbook.backend.dto;


/** Petición de devolución. El idUsuario se toma del token de la sesión. */
public class DevolucionRequest {

    private String codigoBarras;

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
}
