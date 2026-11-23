package sissepbook.backend.dto;

import jakarta.validation.constraints.NotBlank;

/** Petición de devolución. El idUsuario se toma del token de la sesión. */
public class DevolucionRequest {

    @NotBlank(message = "El código de barras es obligatorio.")
    private String codigoBarras;

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
}
