package sissepbook.backend.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Guarda en memoria las sesiones activas: token -> usuario autenticado.
 * El token viaja en el header Authorization: Bearer <token>.
 */
@Component
public class TokenStore {

    /** Datos de la sesión de un usuario autenticado. */
    public static class SesionToken {
        public final int idUsuario;
        public final String nombreUsuario;
        public final String nombreCompleto;
        public final String rol;

        public SesionToken(int idUsuario, String nombreUsuario, String nombreCompleto, String rol) {
            this.idUsuario = idUsuario;
            this.nombreUsuario = nombreUsuario;
            this.nombreCompleto = nombreCompleto;
            this.rol = rol;
        }
    }

    private final Map<String, SesionToken> sesiones = new ConcurrentHashMap<>();

    /** Crea un token nuevo para la sesión indicada. */
    public String crear(SesionToken sesion) {
        String token = UUID.randomUUID().toString();
        sesiones.put(token, sesion);
        return token;
    }

    /** Obtiene la sesión asociada al token (o null si no existe / expiró). */
    public SesionToken obtener(String token) {
        if (token == null) {
            return null;
        }
        return sesiones.get(token);
    }

    /** Invalida el token (logout). */
    public void invalidar(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }
}
