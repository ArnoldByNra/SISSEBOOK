package sissepbook.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sissepbook.backend.dto.LectorDTO;
import sissepbook.backend.service.LectorService;

import java.util.List;

/** Ruta base /api/lectores. CRUD y búsqueda por matrícula. <b>No</b> contiene SQL. */
@RestController
@RequestMapping("/api/lectores")
public class LectorController {

    private final LectorService lectorService;

    public LectorController(LectorService lectorService) {
        this.lectorService = lectorService;
    }

    @GetMapping
    public List<LectorDTO> listar(@RequestParam(required = false) String q) {
        return lectorService.listar(q);
    }

    @GetMapping("/{id}")
    public LectorDTO obtener(@PathVariable int id) {
        return lectorService.obtener(id);
    }

    @GetMapping("/matricula/{matricula}")
    public LectorDTO obtenerPorMatricula(@PathVariable String matricula) {
        return lectorService.obtenerPorMatricula(matricula);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LectorDTO crear(@Valid @RequestBody LectorDTO dto) {
        return lectorService.crear(dto);
    }

    @PutMapping("/{id}")
    public LectorDTO actualizar(@PathVariable int id, @Valid @RequestBody LectorDTO dto) {
        return lectorService.actualizar(id, dto);
    }
}
