package sissepbook.backend.service;

import org.springframework.stereotype.Service;
import sissepbook.backend.dto.LoginRequest;
import sissepbook.backend.dto.LoginResponse;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.UsuarioRepository;
import sissepbook.backend.security.PasswordService;
import sissepbook.backend.security.TokenStore;

/** Verifica credenciales, cuenta activa y genera el token de sesión. */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;
    private final TokenStore tokenStore;

    public AuthService(UsuarioRepository usuarioRepository, PasswordService passwordService, TokenStore tokenStore) {
        this.usuarioRepository = usuarioRepository;
        this.passwordService = passwordService;
        this.tokenStore = tokenStore;
    }

    public LoginResponse login(LoginRequest request) {
        UsuarioRepository.UsuarioLogin usuario = usuarioRepository.buscarParaLogin(request.getUsuario())
                .orElseThrow(() -> new NegocioException("Usuario o contraseña incorrectos."));

        if (!usuario.activo()) {
            throw new NegocioException("La cuenta está desactivada. Contacta al administrador.");
        }

        if (!passwordService.matches(request.getContrasena(), usuario.passwordHash())) {
            throw new NegocioException("Usuario o contraseña incorrectos.");
        }

        TokenStore.SesionToken sesion = new TokenStore.SesionToken(
                usuario.idUsuario(), usuario.nombreUsuario(), usuario.nombreCompleto(), usuario.rol());
        String token = tokenStore.crear(sesion);

        return new LoginResponse(token, usuario.idUsuario(), usuario.nombreUsuario(),
                usuario.nombreCompleto(), usuario.rol());
    }

    public void logout(String token) {
        tokenStore.invalidar(token);
    }
}
