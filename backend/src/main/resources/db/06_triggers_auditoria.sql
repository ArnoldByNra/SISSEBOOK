-- =====================================================
-- 06 - Triggers de auditoría
--
-- QUÉ SE HACE AQUÍ:
-- Registran cada cambio en bitacora_auditoria (tabla, operación, registro,
-- usuario responsable y JSON con info anterior/nueva).
-- Cubren ejemplares (INSERT/UPDATE/DELETE), libros (UPDATE/DELETE),
-- prestamos (UPDATE) y usuarios_sistema (UPDATE, SIN guardar password_hash).
-- El usuario responsable se toma de la variable de sesión @usuario_app que el
-- backend establece antes de cada operación (si es NULL, se usa CURRENT_USER()).
-- =====================================================

USE sissebook;
DELIMITER //

-- EJEMPLARES: alta
CREATE TRIGGER trg_aud_ejemplares_insert
AFTER INSERT ON ejemplares
FOR EACH ROW
BEGIN
    INSERT INTO bitacora_auditoria (
        tabla_afectada, tipo_operacion, id_registro, usuario_responsable, informacion_anterior, informacion_nueva
    ) VALUES (
        'ejemplares', 'INSERT', CAST(NEW.id_ejemplar AS CHAR), IFNULL(@usuario_app, CURRENT_USER()), NULL,
        JSON_OBJECT('id_ejemplar', NEW.id_ejemplar, 'id_libro', NEW.id_libro,
                    'codigo_barras', NEW.codigo_barras, 'estado', NEW.estado)
    );
END //

-- EJEMPLARES: cambio de estado / ubicación
CREATE TRIGGER trg_aud_ejemplares_update
AFTER UPDATE ON ejemplares
FOR EACH ROW
BEGIN
    INSERT INTO bitacora_auditoria (
        tabla_afectada, tipo_operacion, id_registro, usuario_responsable, informacion_anterior, informacion_nueva
    ) VALUES (
        'ejemplares', 'UPDATE', CAST(NEW.id_ejemplar AS CHAR), IFNULL(@usuario_app, CURRENT_USER()),
        JSON_OBJECT('codigo_barras', OLD.codigo_barras, 'estado', OLD.estado, 'ubicacion_estante', OLD.ubicacion_estante),
        JSON_OBJECT('codigo_barras', NEW.codigo_barras, 'estado', NEW.estado, 'ubicacion_estante', NEW.ubicacion_estante)
    );
END //

-- EJEMPLARES: baja
CREATE TRIGGER trg_aud_ejemplares_delete
AFTER DELETE ON ejemplares
FOR EACH ROW
BEGIN
    INSERT INTO bitacora_auditoria (
        tabla_afectada, tipo_operacion, id_registro, usuario_responsable, informacion_anterior, informacion_nueva
    ) VALUES (
        'ejemplares', 'DELETE', CAST(OLD.id_ejemplar AS CHAR), IFNULL(@usuario_app, CURRENT_USER()),
        JSON_OBJECT('id_ejemplar', OLD.id_ejemplar, 'id_libro', OLD.id_libro,
                    'codigo_barras', OLD.codigo_barras, 'estado', OLD.estado),
        NULL
    );
END //

-- LIBROS: modificación (incluye activar/desactivar)
CREATE TRIGGER trg_aud_libros_update
AFTER UPDATE ON libros
FOR EACH ROW
BEGIN
    INSERT INTO bitacora_auditoria (
        tabla_afectada, tipo_operacion, id_registro, usuario_responsable, informacion_anterior, informacion_nueva
    ) VALUES (
        'libros', 'UPDATE', CAST(NEW.id_libro AS CHAR), IFNULL(@usuario_app, CURRENT_USER()),
        JSON_OBJECT('titulo', OLD.titulo, 'autor', OLD.autor, 'activo', OLD.activo),
        JSON_OBJECT('titulo', NEW.titulo, 'autor', NEW.autor, 'activo', NEW.activo)
    );
END //

-- LIBROS: eliminación
CREATE TRIGGER trg_aud_libros_delete
AFTER DELETE ON libros
FOR EACH ROW
BEGIN
    INSERT INTO bitacora_auditoria (
        tabla_afectada, tipo_operacion, id_registro, usuario_responsable, informacion_anterior, informacion_nueva
    ) VALUES (
        'libros', 'DELETE', CAST(OLD.id_libro AS CHAR), IFNULL(@usuario_app, CURRENT_USER()),
        JSON_OBJECT('isbn', OLD.isbn, 'titulo', OLD.titulo, 'autor', OLD.autor),
        NULL
    );
END //

-- PRESTAMOS: cambio de estado / vencimiento
CREATE TRIGGER trg_aud_prestamos_update
AFTER UPDATE ON prestamos
FOR EACH ROW
BEGIN
    INSERT INTO bitacora_auditoria (
        tabla_afectada, tipo_operacion, id_registro, usuario_responsable, informacion_anterior, informacion_nueva
    ) VALUES (
        'prestamos', 'UPDATE', CAST(NEW.id_prestamo AS CHAR), IFNULL(@usuario_app, CURRENT_USER()),
        JSON_OBJECT('estado_prestamo', OLD.estado_prestamo, 'fecha_vencimiento', OLD.fecha_vencimiento),
        JSON_OBJECT('estado_prestamo', NEW.estado_prestamo, 'fecha_vencimiento', NEW.fecha_vencimiento)
    );
END //

-- USUARIOS_SISTEMA: modificación (NUNCA se guarda password_hash)
CREATE TRIGGER trg_aud_usuarios_update
AFTER UPDATE ON usuarios_sistema
FOR EACH ROW
BEGIN
    INSERT INTO bitacora_auditoria (
        tabla_afectada, tipo_operacion, id_registro, usuario_responsable, informacion_anterior, informacion_nueva
    ) VALUES (
        'usuarios_sistema', 'UPDATE', CAST(NEW.id_usuario AS CHAR), IFNULL(@usuario_app, CURRENT_USER()),
        JSON_OBJECT('nombre_usuario', OLD.nombre_usuario, 'id_rol', OLD.id_rol, 'activo', OLD.activo),
        JSON_OBJECT('nombre_usuario', NEW.nombre_usuario, 'id_rol', NEW.id_rol, 'activo', NEW.activo)
    );
END //

DELIMITER ;
