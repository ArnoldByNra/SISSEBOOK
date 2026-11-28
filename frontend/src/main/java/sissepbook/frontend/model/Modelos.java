package sissepbook.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * POJOs que reflejan los DTO del backend.
 * @JsonIgnoreProperties(ignoreUnknown = true) para tolerar campos extra.
 */
public final class Modelos {

    private Modelos() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoginRequest {
        public String usuario;
        public String contrasena;

        public LoginRequest() { }
        public LoginRequest(String usuario, String contrasena) {
            this.usuario = usuario;
            this.contrasena = contrasena;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoginResponse {
        public String token;
        public int idUsuario;
        public String nombreUsuario;
        public String nombreCompleto;
        public String rol;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Libro {
        public Integer idLibro;
        public String isbn;
        public String titulo;
        public String autor;
        public String editorial;
        public Integer idCategoria;
        public String nombreCategoria;
        public Integer anioPublicacion;
        public boolean activo = true;
        public int ejemplaresDisponibles;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ejemplar {
        public Integer idEjemplar;
        public Integer idLibro;
        public String tituloLibro;
        public String codigoBarras;
        public String codigoInterno;
        public String estado;
        public String ubicacionEstante;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Lector {
        public Integer idLector;
        public String matricula;
        public String nombreCompleto;
        public String carreraOArea;
        public String correo;
        public String estadoLector = "activo";
        public Integer maxPrestamos = 3;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usuario {
        public Integer idUsuario;
        public String nombreCompleto;
        public String nombreUsuario;
        public String contrasena;
        public Integer idRol;
        public String nombreRol;
        public boolean activo = true;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrestamoRequest {
        public Integer idLector;
        public List<String> codigosBarras;
        public Integer diasPrestamo;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrestamoResponse {
        public List<Integer> idsPrestamo;
        public String fechaVencimiento;
        public List<String> codigosPrestados;
        public String mensaje;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DevolucionRequest {
        public String codigoBarras;
        public DevolucionRequest() { }
        public DevolucionRequest(String codigoBarras) { this.codigoBarras = codigoBarras; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DevolucionResponse {
        public String codigoBarras;
        public int diasRetraso;
        public String mensaje;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bitacora {
        public Long idAuditoria;
        public String tablaAfectada;
        public String tipoOperacion;
        public String idRegistro;
        public String fechaHora;
        public String usuarioResponsable;
        public String informacionAnterior;
        public String informacionNueva;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReporteLibroSolicitado {
        public int idLibro;
        public String titulo;
        public String autor;
        public String categoria;
        public int totalPrestamos;
        public int lectoresDistintos;
        public int ejemplaresDisponibles;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReporteLectorVencido {
        public int idLector;
        public String matricula;
        public String nombreCompleto;
        public String correo;
        public int prestamosPendientes;
        public int maxDiasRetraso;
        public int totalDiasRetraso;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rol {
        public String idRol;
        public String nombreRol;
    }
}
