package sissepbook.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sissepbook.backend.dto.LibroDTO;
import sissepbook.backend.security.SecurityInterceptor;
import sissepbook.backend.security.TokenStore;
import sissepbook.backend.service.LibroService;

import java.util.List;

/** Ruta base /api/libros. CRUD de libros. <b>No</b> contiene SQL. */
@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public List<LibroDTO> listar(@RequestParam(required = false) String q) {
        return libroService.listar(q);
    }

    @GetMapping("/{id}")
    public LibroDTO obtener(@PathVariable int id) {
        return libroService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibroDTO crear(@Valid @RequestBody LibroDTO dto,
                          @RequestAttribute(SecurityInterceptor.ATTR_SESION) TokenStore.SesionToken sesion) {
        return libroService.crear(dto, sesion.nombreUsuario);
    }

    @PutMapping("/{id}")
    public LibroDTO actualizar(@PathVariable int id, @Valid @RequestBody LibroDTO dto,
                               @RequestAttribute(SecurityInterceptor.ATTR_SESION) TokenStore.SesionToken sesion) {
        return libroService.actualizar(id, dto, sesion.nombreUsuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id,
                                         @RequestAttribute(SecurityInterceptor.ATTR_SESION) TokenStore.SesionToken sesion) {
        libroService.eliminar(id, sesion.nombreUsuario);
        return ResponseEntity.noContent().build();
    }
}
