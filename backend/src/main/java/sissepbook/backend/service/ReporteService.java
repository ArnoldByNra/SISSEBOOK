package sissepbook.backend.service;

import sissepbook.backend.security.Autorizacion;
import sissepbook.backend.dto.ReporteLectorVencidoDTO;
import sissepbook.backend.dto.ReporteLibroSolicitadoDTO;
import sissepbook.backend.repository.ReporteRepository;

import java.util.List;

/** Obtiene los datos de reportes desde el repositorio. */
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    public List<ReporteLibroSolicitadoDTO> librosMasSolicitados(int limite) {
        Autorizacion.exigirSesion();
        int lim = (limite > 0 && limite <= 100) ? limite : 20;
        return reporteRepository.librosMasSolicitados(lim);
    }

    public List<ReporteLectorVencidoDTO> lectoresVencidos() {
        Autorizacion.exigirSesion();
        return reporteRepository.lectoresVencidos();
    }

    /** Resumen del periodo [desde, hasta] (ambos inclusive) comparado contra el periodo anterior de igual duración. */
    public java.util.Map<String, Object> resumenPeriodo(String desdeTxt, String hastaTxt) {
        Autorizacion.exigirSesion();
        java.time.LocalDate hasta;
        java.time.LocalDate desde;
        try {
            hasta = (hastaTxt == null || hastaTxt.isBlank()) ? java.time.LocalDate.now() : java.time.LocalDate.parse(hastaTxt);
            desde = (desdeTxt == null || desdeTxt.isBlank()) ? hasta.minusDays(29) : java.time.LocalDate.parse(desdeTxt);
        } catch (java.time.format.DateTimeParseException e) {
            throw new sissepbook.backend.exception.NegocioException("Fecha inválida. Use el formato AAAA-MM-DD.");
        }
        if (desde.isAfter(hasta)) {
            throw new sissepbook.backend.exception.NegocioException("La fecha inicial no puede ser posterior a la final.");
        }
        java.time.LocalDate hastaExcl = hasta.plusDays(1);
        long dias = java.time.temporal.ChronoUnit.DAYS.between(desde, hastaExcl);
        java.time.LocalDate antDesde = desde.minusDays(dias);

        java.util.Map<String, Object> r = new java.util.LinkedHashMap<>();
        r.put("desde", desde.toString());
        r.put("hasta", hasta.toString());
        r.put("prestamos", reporteRepository.prestamosEnPeriodo(desde, hastaExcl));
        r.put("prestamosAnterior", reporteRepository.prestamosEnPeriodo(antDesde, desde));
        r.put("devoluciones", reporteRepository.devolucionesEnPeriodo(desde, hastaExcl));
        r.put("puntuales", reporteRepository.puntualesEnPeriodo(desde, hastaExcl));
        r.put("porCategoria", reporteRepository.prestamosPorCategoria(desde, hastaExcl));
        r.put("topLibros", reporteRepository.topLibrosEnPeriodo(desde, hastaExcl));
        return r;
    }
}
