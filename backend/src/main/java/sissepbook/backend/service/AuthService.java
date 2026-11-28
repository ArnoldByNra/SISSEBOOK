package sissepbook.backend.service;

import sissepbook.backend.dto.LoginRequest;
import sissepbook.backend.dto.LoginResponse;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.exception.Validar;
import sissepbook.backend.repository.UsuarioRepository;
import sissepbook.backend.security.PasswordService;
import sissepbook.backend.security.SesionActual;

/** Verifica credenciales y cuenta activa, y deja al usuario como sesión actual. */
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordService passwordService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordService = passwordService;
    }

    public LoginResponse login(LoginRequest request) {
        Validar.texto(request.getUsuario(), "El usuario es obligatorio.");
        Validar.texto(request.getContrasena(), "La contraseña es obligatoria.");

        UsuarioRepository.UsuarioLogin usuario = usuarioRepository.buscarParaLogin(request.getUsuario().trim())
                .orElseThrow(() -> new NegocioException("Usuario o contraseña incorrectos."));

        if (!usuario.activo()) {
            throw new NegocioException("La cuenta está desactivada. Contacta al administrador.");
        }
        if (!passwordService.matches(request.getContrasena(), usuario.passwordHash())) {
            throw new NegocioException("Usuario o contraseña incorrectos.");
        }

        SesionActual.iniciar(new SesionActual.Usuario(
                usuario.idUsuario(), usuario.nombreUsuario(), usuario.nombreCompleto(), usuario.rol()));
        return new LoginResponse(usuario.idUsuario(), usuario.nombreUsuario(),
                usuario.nombreCompleto(), usuario.rol());
    }

    public void logout() {
        SesionActual.cerrar();
    }
}
