-- =====================================================
-- 06 - Triggers de auditoría
-- QUÉ SE VA A HACER AQUÍ:
-- Registrar en bitacora_auditoria (tabla, operación, fecha, usuario, info anterior/nueva en JSON)
-- trg_aud_ejemplares_update / _insert / _delete  (ej. cambio de ubicacion_estante o estado)
-- trg_aud_libros_update / _delete
-- trg_aud_usuarios_update  (NO guardar password_hash en la bitácora)
-- Usuario responsable: variable de sesión @usuario_app que el backend establece antes de cada operación.

-- =====================================================

USE biblioteca;
DELIMITER $$
-- TODO
DELIMITER ;
