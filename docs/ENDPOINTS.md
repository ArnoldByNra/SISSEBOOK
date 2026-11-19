# API REST

| Método | Ruta | Descripción | Rol mínimo | Objeto SQL usado |
|---|---|---|---|---|
| POST | /api/auth/login | Valida usuario/contraseña, devuelve token + rol | público | SELECT usuarios_sistema (BCrypt en Java) |
| POST | /api/auth/logout | Invalida token | cualquiera | - |
| GET | /api/libros | Lista/busca libros | CONSULTA | SELECT |
| POST/PUT/DELETE | /api/libros | CRUD libros | OPERADOR / ADMIN | INSERT/UPDATE (triggers auditoría) |
| GET | /api/ejemplares/codigo/{cb} | Busca ejemplar por código de barras | CONSULTA | `sp_consultar_ejemplar_por_codigo` |
| POST/PUT | /api/ejemplares | Alta/edición ejemplar | OPERADOR | INSERT/UPDATE |
| GET/POST/PUT | /api/lectores | CRUD lectores | OPERADOR | SELECT/INSERT/UPDATE |
| POST | /api/prestamos | Registra préstamo (varios ejemplares) | OPERADOR | `sp_registrar_prestamo` (TRANSACCIÓN) |
| POST | /api/devoluciones | Registra devolución de un ejemplar | OPERADOR | `sp_registrar_devolucion` (TRANSACCIÓN) |
| GET | /api/prestamos/vencidos | Préstamos con retraso | OPERADOR | `fn_dias_retraso` |
| GET | /api/reportes/libros-mas-solicitados | Reporte 1 | OPERADOR | consulta avanzada |
| GET | /api/reportes/lectores-vencidos | Reporte 2 | OPERADOR | consulta avanzada |
| GET/POST/PUT | /api/usuarios | Gestión de usuarios | ADMIN | INSERT (hash BCrypt) |
| GET | /api/bitacora | Consulta auditoría | ADMIN | SELECT bitacora_auditoria |
