package sissepbook.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sissepbook.backend.dto.UsuarioDTO;
import sissepbook.backend.security.SecurityInterceptor;
import sissepbook.backend.security.TokenStore;
import sissepbook.backend.service.UsuarioService;

import java.util.List;
import java.util.Map;

/** Ruta base /api/usuarios. Solo ADMIN. <b>No</b> contiene SQL. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioDTO> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/roles")
    public List<Map<String, String>> roles() {
        return usuarioService.listarRoles().stream()
                .map(r -> Map.of("idRol", r[0], "nombreRol", r[1]))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO crear(@Valid @RequestBody UsuarioDTO dto,
                            @RequestAttribute(SecurityInterceptor.ATTR_SESION) TokenStore.SesionToken sesion) {
        return usuarioService.crear(dto, sesion.nombreUsuario);
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@PathVariable int id, @Valid @RequestBody UsuarioDTO dto,
                                 @RequestAttribute(SecurityInterceptor.ATTR_SESION) TokenStore.SesionToken sesion) {
        return usuarioService.actualizar(id, dto, sesion.nombreUsuario);
    }
}
