package sissepbook.backend.controller;

import org.springframework.web.bind.annotation.*;
import sissepbook.backend.dto.ReporteLectorVencidoDTO;
import sissepbook.backend.dto.ReporteLibroSolicitadoDTO;
import sissepbook.backend.service.ReporteService;

import java.util.List;

/** Ruta base /api/reportes. Reportes avanzados. <b>No</b> contiene SQL. */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/libros-mas-solicitados")
    public List<ReporteLibroSolicitadoDTO> librosMasSolicitados(@RequestParam(defaultValue = "20") int limite) {
        return reporteService.librosMasSolicitados(limite);
    }

    @GetMapping("/lectores-vencidos")
    public List<ReporteLectorVencidoDTO> lectoresVencidos() {
        return reporteService.lectoresVencidos();
    }
}
