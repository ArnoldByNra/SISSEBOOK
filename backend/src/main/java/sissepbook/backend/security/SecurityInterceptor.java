package sissepbook.backend.security;

/**
 * QUÉ SE VA A HACER AQUÍ:
 * - Interceptor que corre antes de cada controller:
 * -   1) valida el token (excepto /api/auth/login)
 * -   2) consulta RolePermissions y responde 403 si el rol no puede
 * -   3) guarda el usuario en el request para que la capa repository establezca @usuario_app (auditoría).
 */
public class SecurityInterceptor {
}
