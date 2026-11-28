package sissepbook.backend.dto;

/** Respuesta del login: datos del usuario autenticado. */
public class LoginResponse {

    private int idUsuario;
    private String nombreUsuario;
    private String nombreCompleto;
    private String rol;

    public LoginResponse() {
    }

    public LoginResponse(int idUsuario, String nombreUsuario, String nombreCompleto, String rol) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
