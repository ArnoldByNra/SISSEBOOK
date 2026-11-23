package sissepbook.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor que corre antes de cada controller:
 *   1) valida el token (excepto /api/auth/**)
 *   2) consulta RolePermissions y responde 403 si el rol no puede
 *   3) deja la sesión como atributo del request para que el controller/repository
 *      conozca el usuario (para setear @usuario_app en la auditoría).
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    public static final String ATTR_SESION = "sesion";
    public static final String ATTR_TOKEN = "token";

    private final TokenStore tokenStore;

    public SecurityInterceptor(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ruta = request.getRequestURI();

        // Login/logout pasan sin token
        if (ruta.startsWith("/api/auth/login") || ruta.startsWith("/api/auth/logout")) {
            return true;
        }

        String token = extraerToken(request);
        TokenStore.SesionToken sesion = tokenStore.obtener(token);

        if (sesion == null) {
            escribirError(response, 401, "No autenticado: token inválido o expirado.");
            return false;
        }

        if (!RolePermissions.permitido(sesion.rol, request.getMethod(), ruta)) {
            escribirError(response, 403, "No tienes permisos para realizar esta operación.");
            return false;
        }

        request.setAttribute(ATTR_SESION, sesion);
        request.setAttribute(ATTR_TOKEN, token);
        return true;
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length()).trim();
        }
        return null;
    }

    private void escribirError(HttpServletResponse response, int status, String mensaje) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"codigo\":" + status + ",\"mensaje\":\"" + mensaje + "\"}");
    }
}
