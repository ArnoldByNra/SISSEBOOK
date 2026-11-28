package sissepbook.backend.service;

import sissepbook.backend.security.Autorizacion;
import sissepbook.backend.repository.PanelRepository;

import java.util.List;
import java.util.Map;

/** Datos del dashboard, disponibilidad e historial. */
public class PanelService {

    private final PanelRepository panelRepository;

    public PanelService(PanelRepository panelRepository) {
        this.panelRepository = panelRepository;
    }

    public Map<String, Object> resumen() {
        Autorizacion.exigirSesion();
        return panelRepository.resumen();
    }

    public List<Map<String, Object>> disponibilidad(String q) {
        Autorizacion.exigirSesion();
        return panelRepository.disponibilidad(q == null ? "" : q.trim());
    }

    public List<Map<String, Object>> historial(int limite) {
        Autorizacion.exigirGestion();
        int lim = (limite > 0 && limite <= 1000) ? limite : 200;
        return panelRepository.historial(lim);
    }
}
