package sissepbook.backend.service;

import sissepbook.backend.dto.LoginRequest;
import sissepbook.backend.dto.LoginResponse;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.UsuarioRepository;
import sissepbook.backend.security.PasswordService;

public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;

        this.usuarioRepository = usuarioRepository;
        this.passwordService = passwordService;
    }

    public LoginResponse login(LoginRequest request) {
                .orElseThrow(() -> new NegocioException("Usuario o contraseña incorrectos."));

        if (!usuario.activo()) {
            throw new NegocioException("La cuenta está desactivada. Contacta al administrador.");
        }
        if (!passwordService.matches(request.getContrasena(), usuario.passwordHash())) {
            throw new NegocioException("Usuario o contraseña incorrectos.");
        }

                usuario.nombreCompleto(), usuario.rol());
    }

    }
}
