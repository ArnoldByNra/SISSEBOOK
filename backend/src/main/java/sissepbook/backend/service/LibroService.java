package sissepbook.backend.service;

import sissepbook.backend.exception.Validar;
import sissepbook.backend.security.Autorizacion;
import sissepbook.backend.dto.LibroDTO;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.LibroRepository;

import java.util.List;

/** Reglas de negocio de libros. */
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public List<LibroDTO> listar(String filtro) {
        Autorizacion.exigirSesion();
        return libroRepository.buscar(filtro);
    }

    public LibroDTO obtener(int idLibro) {
        Autorizacion.exigirSesion();
        LibroDTO libro = libroRepository.buscarPorId(idLibro);
        if (libro == null) {
            throw new NegocioException("El libro no existe.");
        }
        return libro;
    }

    public LibroDTO crear(LibroDTO dto) {
        String usuarioApp = Autorizacion.exigirGestion().nombreUsuario();
        validar(dto);
        if (libroRepository.existeIsbn(dto.getIsbn(), null)) {
            throw new NegocioException("Ya existe un libro con ese ISBN.");
        }
        int id = libroRepository.insertar(dto, usuarioApp);
        return libroRepository.buscarPorId(id);
    }

    public LibroDTO actualizar(int idLibro, LibroDTO dto) {
        String usuarioApp = Autorizacion.exigirGestion().nombreUsuario();
        validar(dto);
        LibroDTO actual = obtener(idLibro);
        if (libroRepository.existeIsbn(dto.getIsbn(), idLibro)) {
            throw new NegocioException("Ya existe otro libro con ese ISBN.");
        }
        dto.setIdLibro(actual.getIdLibro());
        // El trigger trg_bloquear_baja_libro_con_prestamos rechaza la baja si hay ejemplares prestados.
        libroRepository.actualizar(dto, usuarioApp);
        return libroRepository.buscarPorId(idLibro);
    }

    public void eliminar(int idLibro) {
        String usuarioApp = Autorizacion.exigirGestion().nombreUsuario();
        obtener(idLibro);
        libroRepository.eliminar(idLibro, usuarioApp);
    }

    private void validar(LibroDTO dto) {
        Validar.texto(dto.getIsbn(), "El ISBN es obligatorio.");
        Validar.texto(dto.getTitulo(), "El título es obligatorio.");
        Validar.texto(dto.getAutor(), "El autor es obligatorio.");
        Validar.texto(dto.getEditorial(), "La editorial es obligatoria.");
        Validar.objeto(dto.getIdCategoria(), "La categoría es obligatoria.");
        Validar.objeto(dto.getAnioPublicacion(), "El año de publicación es obligatorio.");
    }
}
