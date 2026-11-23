package sissepbook.backend.service;

import org.springframework.stereotype.Service;
import sissepbook.backend.dto.EjemplarDTO;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.EjemplarRepository;

import java.util.List;

/** Reglas de ejemplares y búsqueda por código de barras. */
@Service
public class EjemplarService {

    private final EjemplarRepository ejemplarRepository;

    public EjemplarService(EjemplarRepository ejemplarRepository) {
        this.ejemplarRepository = ejemplarRepository;
    }

    public List<EjemplarDTO> listarPorLibro(int idLibro) {
        return ejemplarRepository.listarPorLibro(idLibro);
    }

    public EjemplarDTO obtenerPorCodigo(String codigoBarras) {
        EjemplarDTO ejemplar = ejemplarRepository.buscarPorCodigoBarras(codigoBarras);
        if (ejemplar == null) {
            throw new NegocioException("No existe un ejemplar con el código de barras " + codigoBarras + ".");
        }
        return ejemplar;
    }

    public EjemplarDTO crear(EjemplarDTO dto, String usuarioApp) {
        if (ejemplarRepository.existeCodigoBarras(dto.getCodigoBarras())) {
            throw new NegocioException("Ya existe un ejemplar con ese código de barras.");
        }
        if (dto.getCodigoInterno() == null || dto.getCodigoInterno().isBlank()) {
            throw new NegocioException("El código interno es obligatorio.");
        }
        if (ejemplarRepository.existeCodigoInterno(dto.getCodigoInterno())) {
            throw new NegocioException("Ya existe un ejemplar con ese código interno.");
        }
        int id = ejemplarRepository.insertar(dto, usuarioApp);
        return ejemplarRepository.buscarPorId(id);
    }

    public EjemplarDTO actualizar(int idEjemplar, EjemplarDTO dto, String usuarioApp) {
        EjemplarDTO actual = ejemplarRepository.buscarPorId(idEjemplar);
        if (actual == null) {
            throw new NegocioException("El ejemplar no existe.");
        }
        dto.setIdEjemplar(idEjemplar);
        if (dto.getCodigoInterno() == null || dto.getCodigoInterno().isBlank()) {
            dto.setCodigoInterno(actual.getCodigoInterno());
        }
        ejemplarRepository.actualizar(dto, usuarioApp);
        return ejemplarRepository.buscarPorId(idEjemplar);
    }

    /** Devuelve estado del ejemplar + ejemplares disponibles del título (SP con OUT). */
    public String[] consultarDisponibilidad(String codigoBarras) {
        return ejemplarRepository.consultarDisponibilidad(codigoBarras);
    }
}
