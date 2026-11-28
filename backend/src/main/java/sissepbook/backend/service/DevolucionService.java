package sissepbook.backend.service;

import sissepbook.backend.dto.DevolucionRequest;
import sissepbook.backend.dto.DevolucionResponse;
import sissepbook.backend.exception.Errores;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.repository.PrestamoRepository;
import sissepbook.backend.security.Autorizacion;
import sissepbook.backend.security.SesionActual;

/**
 * Orquesta la devolución llamando al SP transaccional sp_registrar_devolucion_codigo
 * y devuelve los días de retraso calculados en la base de datos.
 */
public class DevolucionService {

    private final PrestamoRepository prestamoRepository;

    public DevolucionService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    public DevolucionResponse registrar(DevolucionRequest request) {
        SesionActual.Usuario usuario = Autorizacion.exigirGestion();
        String codigo = request.getCodigoBarras() == null ? "" : request.getCodigoBarras().trim();
        if (codigo.isEmpty()) {
            throw new NegocioException("El código de barras es obligatorio.");
        }
        int diasRetraso;
        try {
            diasRetraso = prestamoRepository.registrarDevolucion(codigo, usuario.idUsuario(), usuario.nombreUsuario());
        } catch (RuntimeException ex) {
            throw new NegocioException(Errores.mensaje(ex));
        }

        String mensaje = diasRetraso > 0
                ? "Devolución registrada con " + diasRetraso + " día(s) de retraso."
                : "Devolución registrada a tiempo. ¡Gracias!";
        return new DevolucionResponse(codigo, diasRetraso, mensaje);
    }
}
