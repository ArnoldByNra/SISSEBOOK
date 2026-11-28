package sissepbook.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Respuesta del registro de préstamo. */
public class PrestamoResponse {

    private List<Integer> idsPrestamo;
    private LocalDateTime fechaVencimiento;
    private List<String> codigosPrestados;
    private String mensaje;

    public PrestamoResponse() {
    }

    public PrestamoResponse(List<Integer> idsPrestamo, LocalDateTime fechaVencimiento,
                            List<String> codigosPrestados, String mensaje) {
        this.idsPrestamo = idsPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.codigosPrestados = codigosPrestados;
        this.mensaje = mensaje;
    }

    public List<Integer> getIdsPrestamo() { return idsPrestamo; }
    public void setIdsPrestamo(List<Integer> idsPrestamo) { this.idsPrestamo = idsPrestamo; }

    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public List<String> getCodigosPrestados() { return codigosPrestados; }
    public void setCodigosPrestados(List<String> codigosPrestados) { this.codigosPrestados = codigosPrestados; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
