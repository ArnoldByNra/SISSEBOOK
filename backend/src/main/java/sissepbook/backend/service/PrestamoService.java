package sissepbook.backend.service;

import sissepbook.backend.dto.PrestamoRequest;
import sissepbook.backend.dto.PrestamoResponse;
import sissepbook.backend.exception.Errores;
import sissepbook.backend.exception.NegocioException;
import sissepbook.backend.exception.Validar;
import sissepbook.backend.repository.PrestamoRepository;
import sissepbook.backend.security.Autorizacion;
import sissepbook.backend.security.SesionActual;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registra UN préstamo con todos los ejemplares escaneados, llamando al SP transaccional
 * sp_registrar_prestamo_multiple: si un ejemplar falla (no existe, no disponible, lector
 * suspendido o sin cupo) no se registra ninguno.
 */
public class PrestamoService {

    private static final int DIAS_DEFAULT = 14;

    private final PrestamoRepository prestamoRepository;

    public PrestamoService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    public PrestamoResponse registrar(PrestamoRequest request) {
        SesionActual.Usuario usuario = Autorizacion.exigirGestion();
        Validar.objeto(request.getIdLector(), "El lector es obligatorio.");

        Set<String> unicos = new LinkedHashSet<>();
        if (request.getCodigosBarras() != null) {
            for (String codigo : request.getCodigosBarras()) {
                String cb = codigo == null ? "" : codigo.trim();
                if (cb.contains(",")) {
                    throw new NegocioException("El código de barras «" + cb + "» no puede contener comas.");
                }
                if (!cb.isEmpty()) {
                    unicos.add(cb);
                }
            }
        }
        if (unicos.isEmpty()) {
            throw new NegocioException("Debes escanear al menos un ejemplar.");
        }
        int dias = (request.getDiasPrestamo() != null && request.getDiasPrestamo() > 0)
                ? request.getDiasPrestamo() : DIAS_DEFAULT;

        List<String> codigos = new ArrayList<>(unicos);
        PrestamoRepository.PrestamoMultiple r;
        try {
            r = prestamoRepository.registrarPrestamoMultiple(
                    codigos, request.getIdLector(), usuario.idUsuario(), dias, usuario.nombreUsuario());
        } catch (RuntimeException ex) {
            throw new NegocioException(Errores.mensaje(ex));
        }

        String mensaje = "Préstamo #" + r.idPrestamo() + " registrado para " + r.totalEjemplares()
                + " ejemplar(es). Fecha de vencimiento: "
                + (r.vencimiento() != null ? r.vencimiento().toLocalDate() : "-") + ".";
        return new PrestamoResponse(List.of(r.idPrestamo()), r.vencimiento(), codigos, mensaje);
    }

    public List<Map<String, Object>> vencidos() {
        Autorizacion.exigirSesion();
        return prestamoRepository.prestamosVencidos();
    }
}
