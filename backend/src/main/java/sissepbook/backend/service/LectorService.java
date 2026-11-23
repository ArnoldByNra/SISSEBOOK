package sissepbook.backend.service;

import org.springframework.stereotype.Service;
import sissepbook.backend.dto.LectorDTO;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.LectorRepository;

import java.util.List;

/** Reglas de lectores (matrícula única, suspensión, límite de préstamos). */
@Service
public class LectorService {

    private final LectorRepository lectorRepository;

    public LectorService(LectorRepository lectorRepository) {
        this.lectorRepository = lectorRepository;
    }

    public List<LectorDTO> listar(String filtro) {
        return lectorRepository.buscar(filtro);
    }

    public LectorDTO obtener(int idLector) {
        LectorDTO lector = lectorRepository.buscarPorId(idLector);
        if (lector == null) {
            throw new NegocioException("El lector no existe.");
        }
        return lector;
    }

    public LectorDTO obtenerPorMatricula(String matricula) {
        LectorDTO lector = lectorRepository.buscarPorMatricula(matricula);
        if (lector == null) {
            throw new NegocioException("No existe un lector con la matrícula " + matricula + ".");
        }
        return lector;
    }

    public LectorDTO crear(LectorDTO dto) {
        if (lectorRepository.existeMatricula(dto.getMatricula(), null)) {
            throw new NegocioException("Ya existe un lector con esa matrícula.");
        }
        validarMaxPrestamos(dto);
        int id = lectorRepository.insertar(dto);
        return lectorRepository.buscarPorId(id);
    }

    public LectorDTO actualizar(int idLector, LectorDTO dto) {
        obtener(idLector);
        if (lectorRepository.existeMatricula(dto.getMatricula(), idLector)) {
            throw new NegocioException("Ya existe otro lector con esa matrícula.");
        }
        validarMaxPrestamos(dto);
        dto.setIdLector(idLector);
        lectorRepository.actualizar(dto);
        return lectorRepository.buscarPorId(idLector);
    }

    private void validarMaxPrestamos(LectorDTO dto) {
        if (dto.getMaxPrestamos() == null || dto.getMaxPrestamos() < 1 || dto.getMaxPrestamos() > 10) {
            throw new NegocioException("El máximo de préstamos debe estar entre 1 y 10.");
        }
    }
}
