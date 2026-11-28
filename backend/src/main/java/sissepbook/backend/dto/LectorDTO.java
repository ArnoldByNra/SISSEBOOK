package sissepbook.backend.dto;


/** Representa un lector (usuario de la biblioteca). */
public class LectorDTO {

    private Integer idLector;

    private String matricula;

    private String nombreCompleto;

    private String carreraOArea;
    private String correo;

    private String estadoLector = "activo";

    private Integer maxPrestamos = 3;

    public Integer getIdLector() { return idLector; }
    public void setIdLector(Integer idLector) { this.idLector = idLector; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCarreraOArea() { return carreraOArea; }
    public void setCarreraOArea(String carreraOArea) { this.carreraOArea = carreraOArea; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getEstadoLector() { return estadoLector; }
    public void setEstadoLector(String estadoLector) { this.estadoLector = estadoLector; }

    public Integer getMaxPrestamos() { return maxPrestamos; }
    public void setMaxPrestamos(Integer maxPrestamos) { this.maxPrestamos = maxPrestamos; }
}
