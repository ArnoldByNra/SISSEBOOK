package sissepbook.backend.service;

import org.springframework.stereotype.Service;
import sissepbook.backend.dto.BitacoraDTO;
import sissepbook.backend.repository.BitacoraRepository;

import java.util.List;

/** Consultas de auditoría. */
@Service
public class BitacoraService {

    private final BitacoraRepository bitacoraRepository;

    public BitacoraService(BitacoraRepository bitacoraRepository) {
        this.bitacoraRepository = bitacoraRepository;
    }

    public List<BitacoraDTO> consultar(String tabla, String usuario, String desde, String hasta, int limite) {
        return bitacoraRepository.consultar(tabla, usuario, desde, hasta, limite);
    }
}
