package sissepbook.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Representa un ejemplar físico de un libro. */
public class EjemplarDTO {

    private Integer idEjemplar;

    @NotNull(message = "El libro es obligatorio.")
    private Integer idLibro;

    private String tituloLibro;

    @NotBlank(message = "El código de barras es obligatorio.")
    private String codigoBarras;

    private String codigoInterno;

    private String estado;

    private String ubicacionEstante;

    public Integer getIdEjemplar() { return idEjemplar; }
    public void setIdEjemplar(Integer idEjemplar) { this.idEjemplar = idEjemplar; }

    public Integer getIdLibro() { return idLibro; }
    public void setIdLibro(Integer idLibro) { this.idLibro = idLibro; }

    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getCodigoInterno() { return codigoInterno; }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUbicacionEstante() { return ubicacionEstante; }
    public void setUbicacionEstante(String ubicacionEstante) { this.ubicacionEstante = ubicacionEstante; }
}
