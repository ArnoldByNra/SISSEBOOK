package sissepbook.backend.service;

import org.springframework.stereotype.Service;
import sissepbook.backend.dto.PrestamoRequest;
import sissepbook.backend.dto.PrestamoResponse;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.PrestamoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Orquesta el préstamo llamando al SP transaccional sp_registrar_prestamo_codigo.
 * Un préstamo puede incluir varios ejemplares; el SP valida y aplica COMMIT/ROLLBACK
 * por cada ejemplar. Si un ejemplar falla, se informa el error y se listan los ya registrados.
 */
@Service
public class PrestamoService {

    private static final int DIAS_DEFAULT = 14;

    private final PrestamoRepository prestamoRepository;

    public PrestamoService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    public PrestamoResponse registrar(PrestamoRequest request, int idUsuario, String usuarioApp) {
        List<String> codigos = request.getCodigosBarras();
        if (codigos == null || codigos.isEmpty()) {
            throw new NegocioException("Debes escanear al menos un ejemplar.");
        }
        int dias = (request.getDiasPrestamo() != null && request.getDiasPrestamo() > 0)
                ? request.getDiasPrestamo() : DIAS_DEFAULT;

        List<Integer> ids = new ArrayList<>();
        List<String> codigosPrestados = new ArrayList<>();
        LocalDateTime vencimiento = null;

        for (String codigo : codigos) {
            String cb = codigo == null ? "" : codigo.trim();
            if (cb.isEmpty()) {
                continue;
            }
            try {
                int idPrestamo = prestamoRepository.registrarPrestamo(cb, request.getIdLector(), idUsuario, dias, usuarioApp);
                ids.add(idPrestamo);
                codigosPrestados.add(cb);
                vencimiento = prestamoRepository.fechaVencimiento(idPrestamo);
            } catch (RuntimeException ex) {
                String base = "Error al prestar el ejemplar " + cb + ": " + ex.getMessage();
                if (!ids.isEmpty()) {
                    base += " (Sí se registraron: " + String.join(", ", codigosPrestados) + ").";
                }
                throw new NegocioException(base);
            }
        }

        String mensaje = "Préstamo registrado para " + codigosPrestados.size() + " ejemplar(es). " +
                "Fecha de vencimiento: " + (vencimiento != null ? vencimiento.toLocalDate() : "-") + ".";
        return new PrestamoResponse(ids, vencimiento, codigosPrestados, mensaje);
    }

    public List<Map<String, Object>> vencidos() {
        return prestamoRepository.prestamosVencidos();
    }
}
