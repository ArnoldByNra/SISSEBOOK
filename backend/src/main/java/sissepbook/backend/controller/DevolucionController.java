package sissepbook.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sissepbook.backend.dto.DevolucionRequest;
import sissepbook.backend.dto.DevolucionResponse;
import sissepbook.backend.security.SecurityInterceptor;
import sissepbook.backend.security.TokenStore;
import sissepbook.backend.service.DevolucionService;

/**
 * Ruta base /api/devoluciones.
 * Registro de devolución (usa sp_registrar_devolucion_codigo = TRANSACCIÓN).
 * <b>No</b> contiene SQL.
 */
@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {

    private final DevolucionService devolucionService;

    public DevolucionController(DevolucionService devolucionService) {
        this.devolucionService = devolucionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DevolucionResponse registrar(@Valid @RequestBody DevolucionRequest request,
                                        @RequestAttribute(SecurityInterceptor.ATTR_SESION) TokenStore.SesionToken sesion) {
        return devolucionService.registrar(request, sesion.idUsuario, sesion.nombreUsuario);
    }
}
