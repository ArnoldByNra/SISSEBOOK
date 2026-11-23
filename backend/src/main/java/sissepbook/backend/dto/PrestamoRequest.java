package sissepbook.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Petición de registro de préstamo.
 * El idUsuario NO viaja aquí: se obtiene del token de la sesión.
 */
public class PrestamoRequest {

    @NotNull(message = "El lector es obligatorio.")
    private Integer idLector;

    private List<String> codigosBarras;

    private Integer diasPrestamo;

    public Integer getIdLector() { return idLector; }
    public void setIdLector(Integer idLector) { this.idLector = idLector; }

    public List<String> getCodigosBarras() { return codigosBarras; }
    public void setCodigosBarras(List<String> codigosBarras) { this.codigosBarras = codigosBarras; }

    public Integer getDiasPrestamo() { return diasPrestamo; }
    public void setDiasPrestamo(Integer diasPrestamo) { this.diasPrestamo = diasPrestamo; }
}
