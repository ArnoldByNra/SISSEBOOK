package sissepbook.backend.service;

import sissepbook.backend.security.Autorizacion;
import sissepbook.backend.dto.BitacoraDTO;
import sissepbook.backend.repository.BitacoraRepository;

import java.util.List;

/** Consultas de auditoría. */
public class BitacoraService {

    private final BitacoraRepository bitacoraRepository;

    public BitacoraService(BitacoraRepository bitacoraRepository) {
        this.bitacoraRepository = bitacoraRepository;
    }

    public List<BitacoraDTO> consultar(String tabla, String usuario, String desde, String hasta, int limite) {
        Autorizacion.exigirAdmin();
        return bitacoraRepository.consultar(tabla, usuario, desde, hasta, limite);
    }
}
