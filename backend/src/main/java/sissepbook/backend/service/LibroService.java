package sissepbook.backend.service;

import org.springframework.stereotype.Service;
import sissepbook.backend.dto.LibroDTO;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.LibroRepository;

import java.util.List;

/** Reglas de negocio de libros. */
@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public List<LibroDTO> listar(String filtro) {
        return libroRepository.buscar(filtro);
    }

    public LibroDTO obtener(int idLibro) {
        LibroDTO libro = libroRepository.buscarPorId(idLibro);
        if (libro == null) {
            throw new NegocioException("El libro no existe.");
        }
        return libro;
    }

    public LibroDTO crear(LibroDTO dto, String usuarioApp) {
        if (libroRepository.existeIsbn(dto.getIsbn(), null)) {
            throw new NegocioException("Ya existe un libro con ese ISBN.");
        }
        int id = libroRepository.insertar(dto, usuarioApp);
        return libroRepository.buscarPorId(id);
    }

    public LibroDTO actualizar(int idLibro, LibroDTO dto, String usuarioApp) {
        LibroDTO actual = obtener(idLibro);
        if (libroRepository.existeIsbn(dto.getIsbn(), idLibro)) {
            throw new NegocioException("Ya existe otro libro con ese ISBN.");
        }
        dto.setIdLibro(actual.getIdLibro());
        // El trigger trg_bloquear_baja_libro_con_prestamos rechaza la baja si hay ejemplares prestados.
        libroRepository.actualizar(dto, usuarioApp);
        return libroRepository.buscarPorId(idLibro);
    }

    public void eliminar(int idLibro, String usuarioApp) {
        obtener(idLibro);
        libroRepository.eliminar(idLibro, usuarioApp);
    }
}
