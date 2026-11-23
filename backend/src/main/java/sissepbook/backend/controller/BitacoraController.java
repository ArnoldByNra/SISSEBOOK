package sissepbook.backend.controller;

import org.springframework.web.bind.annotation.*;
import sissepbook.backend.dto.BitacoraDTO;
import sissepbook.backend.service.BitacoraService;

import java.util.List;

/** Ruta base /api/bitacora. Solo ADMIN. Consulta con filtros. <b>No</b> contiene SQL. */
@RestController
@RequestMapping("/api/bitacora")
public class BitacoraController {

    private final BitacoraService bitacoraService;

    public BitacoraController(BitacoraService bitacoraService) {
        this.bitacoraService = bitacoraService;
    }

    @GetMapping
    public List<BitacoraDTO> consultar(@RequestParam(required = false) String tabla,
                                       @RequestParam(required = false) String usuario,
                                       @RequestParam(required = false) String desde,
                                       @RequestParam(required = false) String hasta,
                                       @RequestParam(defaultValue = "200") int limite) {
        return bitacoraService.consultar(tabla, usuario, desde, hasta, limite);
    }
}
