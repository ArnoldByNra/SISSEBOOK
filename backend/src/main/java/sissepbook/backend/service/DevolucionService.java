package sissepbook.backend.service;

import org.springframework.stereotype.Service;
import sissepbook.backend.dto.DevolucionRequest;
import sissepbook.backend.dto.DevolucionResponse;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.PrestamoRepository;

/**
 * Orquesta la devolución llamando al SP transaccional sp_registrar_devolucion_codigo
 * y devuelve los días de retraso calculados en la base de datos.
 */
@Service
public class DevolucionService {

    private final PrestamoRepository prestamoRepository;

    public DevolucionService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    public DevolucionResponse registrar(DevolucionRequest request, int idUsuario, String usuarioApp) {
        String codigo = request.getCodigoBarras() == null ? "" : request.getCodigoBarras().trim();
        if (codigo.isEmpty()) {
            throw new NegocioException("El código de barras es obligatorio.");
        }
        int diasRetraso;
        try {
            diasRetraso = prestamoRepository.registrarDevolucion(codigo, idUsuario, usuarioApp);
        } catch (RuntimeException ex) {
            throw new NegocioException(ex.getMessage());
        }

        String mensaje = diasRetraso > 0
                ? "Devolución registrada con " + diasRetraso + " día(s) de retraso."
                : "Devolución registrada a tiempo. ¡Gracias!";
        return new DevolucionResponse(codigo, diasRetraso, mensaje);
    }
}
