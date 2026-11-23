package sissepbook.backend.service;

import org.springframework.stereotype.Service;
import sissepbook.backend.dto.ReporteLectorVencidoDTO;
import sissepbook.backend.dto.ReporteLibroSolicitadoDTO;
import sissepbook.backend.repository.ReporteRepository;

import java.util.List;

/** Obtiene los datos de reportes desde el repositorio. */
@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    public List<ReporteLibroSolicitadoDTO> librosMasSolicitados(int limite) {
        int lim = (limite > 0 && limite <= 100) ? limite : 20;
        return reporteRepository.librosMasSolicitados(lim);
    }

    public List<ReporteLectorVencidoDTO> lectoresVencidos() {
        return reporteRepository.lectoresVencidos();
    }
}
