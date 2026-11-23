package sissepbook.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sissepbook.backend.dto.EjemplarDTO;
import sissepbook.backend.security.SecurityInterceptor;
import sissepbook.backend.security.TokenStore;
import sissepbook.backend.service.EjemplarService;

import java.util.List;
import java.util.Map;

/** Ruta base /api/ejemplares. Alta/edición y consulta por código de barras. <b>No</b> contiene SQL. */
@RestController
@RequestMapping("/api/ejemplares")
public class EjemplarController {

    private final EjemplarService ejemplarService;

    public EjemplarController(EjemplarService ejemplarService) {
        this.ejemplarService = ejemplarService;
    }

    @GetMapping("/libro/{idLibro}")
    public List<EjemplarDTO> listarPorLibro(@PathVariable int idLibro) {
        return ejemplarService.listarPorLibro(idLibro);
    }

    /** Consulta por código de barras (lector físico): devuelve el ejemplar completo. */
    @GetMapping("/codigo/{codigoBarras}")
    public EjemplarDTO obtenerPorCodigo(@PathVariable String codigoBarras) {
        return ejemplarService.obtenerPorCodigo(codigoBarras);
    }

    /** Disponibilidad por código: estado + ejemplares disponibles del título (usa el SP con OUT). */
    @GetMapping("/disponibilidad/{codigoBarras}")
    public Map<String, Object> disponibilidad(@PathVariable String codigoBarras) {
        String[] r = ejemplarService.consultarDisponibilidad(codigoBarras);
        return Map.of("estado", r[0], "disponiblesTitulo", Integer.parseInt(r[1]));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EjemplarDTO crear(@Valid @RequestBody EjemplarDTO dto,
                             @RequestAttribute(SecurityInterceptor.ATTR_SESION) TokenStore.SesionToken sesion) {
        return ejemplarService.crear(dto, sesion.nombreUsuario);
    }

    @PutMapping("/{id}")
    public EjemplarDTO actualizar(@PathVariable int id, @Valid @RequestBody EjemplarDTO dto,
                                  @RequestAttribute(SecurityInterceptor.ATTR_SESION) TokenStore.SesionToken sesion) {
        return ejemplarService.actualizar(id, dto, sesion.nombreUsuario);
    }
}
