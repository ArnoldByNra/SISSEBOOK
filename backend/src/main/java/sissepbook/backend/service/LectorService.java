package sissepbook.backend.service;

import sissepbook.backend.exception.Validar;
import sissepbook.backend.security.Autorizacion;
import sissepbook.backend.dto.LectorDTO;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.LectorRepository;

import java.util.List;

/** Reglas de lectores (matrícula única, suspensión, límite de préstamos). */
public class LectorService {

    private final LectorRepository lectorRepository;

    public LectorService(LectorRepository lectorRepository) {
        this.lectorRepository = lectorRepository;
    }

    public List<LectorDTO> listar(String filtro) {
        Autorizacion.exigirGestion();
        return lectorRepository.buscar(filtro);
    }

    public LectorDTO obtener(int idLector) {
        Autorizacion.exigirGestion();
        LectorDTO lector = lectorRepository.buscarPorId(idLector);
        if (lector == null) {
            throw new NegocioException("El lector no existe.");
        }
        return lector;
    }

    public LectorDTO obtenerPorMatricula(String matricula) {
        Autorizacion.exigirGestion();
        LectorDTO lector = lectorRepository.buscarPorMatricula(matricula);
        if (lector == null) {
            throw new NegocioException("No existe un lector con la matrícula " + matricula + ".");
        }
        return lector;
    }

    public LectorDTO crear(LectorDTO dto) {
        Autorizacion.exigirGestion();
        validarBasico(dto);
        if (lectorRepository.existeMatricula(dto.getMatricula(), null)) {
            throw new NegocioException("Ya existe un lector con esa matrícula.");
        }
        validarMaxPrestamos(dto);
        int id = lectorRepository.insertar(dto);
        return lectorRepository.buscarPorId(id);
    }

    public LectorDTO actualizar(int idLector, LectorDTO dto) {
        Autorizacion.exigirGestion();
        validarBasico(dto);
        obtener(idLector);
        if (lectorRepository.existeMatricula(dto.getMatricula(), idLector)) {
            throw new NegocioException("Ya existe otro lector con esa matrícula.");
        }
        validarMaxPrestamos(dto);
        dto.setIdLector(idLector);
        lectorRepository.actualizar(dto);
        return lectorRepository.buscarPorId(idLector);
    }

    private void validarBasico(LectorDTO dto) {
        Validar.texto(dto.getMatricula(), "La matrícula es obligatoria.");
        Validar.texto(dto.getNombreCompleto(), "El nombre completo es obligatorio.");
    }

    private void validarMaxPrestamos(LectorDTO dto) {
        if (dto.getMaxPrestamos() == null || dto.getMaxPrestamos() < 1 || dto.getMaxPrestamos() > 10) {
            throw new NegocioException("El máximo de préstamos debe estar entre 1 y 10.");
        }
    }
}
