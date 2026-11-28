package sissepbook.backend.dto;

/** Fila del reporte "lectores con préstamos vencidos". */
public class ReporteLectorVencidoDTO {

    private int idLector;
    private String matricula;
    private String nombreCompleto;
    private String correo;
    private int prestamosPendientes;
    private int maxDiasRetraso;
    private int totalDiasRetraso;

    public int getIdLector() { return idLector; }
    public void setIdLector(int idLector) { this.idLector = idLector; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public int getPrestamosPendientes() { return prestamosPendientes; }
    public void setPrestamosPendientes(int prestamosPendientes) { this.prestamosPendientes = prestamosPendientes; }

    public int getMaxDiasRetraso() { return maxDiasRetraso; }
    public void setMaxDiasRetraso(int maxDiasRetraso) { this.maxDiasRetraso = maxDiasRetraso; }

    public int getTotalDiasRetraso() { return totalDiasRetraso; }
    public void setTotalDiasRetraso(int totalDiasRetraso) { this.totalDiasRetraso = totalDiasRetraso; }
}
