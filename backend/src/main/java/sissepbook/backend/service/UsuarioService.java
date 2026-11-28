package sissepbook.backend.service;

import sissepbook.backend.exception.Validar;
import sissepbook.backend.security.Autorizacion;
import sissepbook.backend.dto.UsuarioDTO;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.UsuarioRepository;
import sissepbook.backend.security.PasswordService;

import java.util.List;

/** Alta/edición de usuarios del sistema con hash BCrypt. */
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordService passwordService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordService = passwordService;
    }

    public List<UsuarioDTO> listar() {
        Autorizacion.exigirAdmin();
        return usuarioRepository.listar();
    }

    public List<String[]> listarRoles() {
        Autorizacion.exigirAdmin();
        return usuarioRepository.listarRoles();
    }

    public UsuarioDTO crear(UsuarioDTO dto) {
        String usuarioApp = Autorizacion.exigirAdmin().nombreUsuario();
        validar(dto);
        if (usuarioRepository.existeNombreUsuario(dto.getNombreUsuario(), null)) {
            throw new NegocioException("Ya existe un usuario con ese nombre de usuario.");
        }
        if (dto.getContrasena() == null || dto.getContrasena().isBlank()) {
            throw new NegocioException("La contraseña es obligatoria para crear un usuario.");
        }
        String hash = passwordService.hash(dto.getContrasena());
        int id = usuarioRepository.insertar(dto, hash, usuarioApp);
        dto.setIdUsuario(id);
        dto.setContrasena(null);
        return dto;
    }

    public UsuarioDTO actualizar(int idUsuario, UsuarioDTO dto) {
        String usuarioApp = Autorizacion.exigirAdmin().nombreUsuario();
        validar(dto);
        if (usuarioRepository.existeNombreUsuario(dto.getNombreUsuario(), idUsuario)) {
            throw new NegocioException("Ya existe otro usuario con ese nombre de usuario.");
        }
        dto.setIdUsuario(idUsuario);
        String hash = (dto.getContrasena() != null && !dto.getContrasena().isBlank())
                ? passwordService.hash(dto.getContrasena())
                : null;
        usuarioRepository.actualizar(dto, hash, usuarioApp);
        dto.setContrasena(null);
        return dto;
    }

    private void validar(UsuarioDTO dto) {
        Validar.texto(dto.getNombreCompleto(), "El nombre completo es obligatorio.");
        Validar.texto(dto.getNombreUsuario(), "El nombre de usuario es obligatorio.");
        Validar.objeto(dto.getIdRol(), "El rol es obligatorio.");
    }
}
