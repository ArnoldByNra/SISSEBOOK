# Dónde se cumple cada requisito por el profe

| Requisito | Dónde |
|---|---|
| Diagrama ER + modelo relacional | `docs/` (agregar imagen/PDF) + `database/01_schema.sql` |
| Script de creación y población | `01_schema.sql`, `06_datos_prueba.sql` |
| Código de barras UNIQUE | `ejemplares.codigo_barras` en `01_schema.sql` |
| Lector físico | `PrestamoController`, `DevolucionController`, `ConsultaCodigoController` |
| Tabla usuarios + hash | `usuarios_sistema` + `security/PasswordService.java` |
| Roles y permisos | `security/RolePermissions.java` + `SecurityInterceptor.java` + `session/Permisos.java` (front) |
| Transacción START/COMMIT/ROLLBACK | `sp_registrar_prestamo`, `sp_registrar_devolucion` (`04_procedures.sql`) |
| 3+ Stored procedures (IN, OUT, DML) | `04_procedures.sql` → usados desde `repository/*Repository.java` |
| 2+ Funciones | `03_functions.sql` → usadas en procedures y reportes |
| 2+ Triggers validación | `05_triggers_validacion.sql` |
| 2+ Triggers auditoría | `06_triggers_auditoria.sql` |
| Reportes (3+ características SQL) | `08_reportes.sql` → `ReportesController` (front) |
| Mensajes de error/confirmación | `exception/GlobalExceptionHandler.java` + `util/Alertas.java` (front) |
