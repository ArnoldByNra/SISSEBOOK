package sissepbook.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sissepbook.backend.dto.LoginRequest;
import sissepbook.backend.dto.LoginResponse;
import sissepbook.backend.service.AuthService;

import java.util.Map;

/** Ruta base /api/auth. Login y logout. <b>No</b> contiene SQL. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            authService.logout(header.substring("Bearer ".length()).trim());
        }
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada."));
    }
}
