package sissepbook.backend.dto;

import java.time.LocalDateTime;

/** Registro de la bitácora de auditoría. */
public class BitacoraDTO {

    private Long idAuditoria;
    private String tablaAfectada;
    private String tipoOperacion;
    private String idRegistro;
    private LocalDateTime fechaHora;
    private String usuarioResponsable;
    private String informacionAnterior;
    private String informacionNueva;

    public Long getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(Long idAuditoria) { this.idAuditoria = idAuditoria; }

    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public String getIdRegistro() { return idRegistro; }
    public void setIdRegistro(String idRegistro) { this.idRegistro = idRegistro; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getUsuarioResponsable() { return usuarioResponsable; }
    public void setUsuarioResponsable(String usuarioResponsable) { this.usuarioResponsable = usuarioResponsable; }

    public String getInformacionAnterior() { return informacionAnterior; }
    public void setInformacionAnterior(String informacionAnterior) { this.informacionAnterior = informacionAnterior; }

    public String getInformacionNueva() { return informacionNueva; }
    public void setInformacionNueva(String informacionNueva) { this.informacionNueva = informacionNueva; }
}
