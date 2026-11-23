package sissepbook.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sissepbook.backend.dto.PrestamoRequest;
import sissepbook.backend.dto.PrestamoResponse;
import sissepbook.backend.security.SecurityInterceptor;
import sissepbook.backend.security.TokenStore;
import sissepbook.backend.service.PrestamoService;

import java.util.List;
import java.util.Map;

/**
 * Ruta base /api/prestamos.
 * Registro de préstamo (usa sp_registrar_prestamo_codigo = TRANSACCIÓN) y listado de vencidos.
 * <b>No</b> contiene SQL.
 */
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoResponse registrar(@Valid @RequestBody PrestamoRequest request,
                                      @RequestAttribute(SecurityInterceptor.ATTR_SESION) TokenStore.SesionToken sesion) {
        return prestamoService.registrar(request, sesion.idUsuario, sesion.nombreUsuario);
    }

    @GetMapping("/vencidos")
    public List<Map<String, Object>> vencidos() {
        return prestamoService.vencidos();
    }
}
