-- =====================================================
-- 05 - Triggers de validación
--
-- QUÉ SE HACE AQUÍ:
-- trg_validar_antes_insertar_detalle_prestamo (BEFORE INSERT detalle_prestamo):
--     rechaza si el ejemplar no existe o no está 'disponible', si el lector no está
--     'activo' o si alcanzó su límite de préstamos (max_prestamos).
-- trg_bloquear_baja_libro_con_prestamos (BEFORE UPDATE libros):
--     impide dar de baja (activo 1 -> 0) un título que aún tiene ejemplares prestados.
-- trg_validar_codigo_barras_ejemplar (BEFORE INSERT/UPDATE ejemplares):
--     rechaza un código de barras vacío.
-- Todos usan SIGNAL SQLSTATE '45000' con mensaje claro.
-- =====================================================

USE sissebook;
DELIMITER //

-- Valida que el código de barras del ejemplar no esté vacío
CREATE TRIGGER trg_validar_codigo_barras_ejemplar_insert
BEFORE INSERT ON ejemplares
FOR EACH ROW
BEGIN
    IF CHAR_LENGTH(TRIM(NEW.codigo_barras)) = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El código de barras del ejemplar no puede estar vacío.';
    END IF;
END //

CREATE TRIGGER trg_validar_codigo_barras_ejemplar_update
BEFORE UPDATE ON ejemplares
FOR EACH ROW
BEGIN
    IF CHAR_LENGTH(TRIM(NEW.codigo_barras)) = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El código de barras del ejemplar no puede estar vacío.';
    END IF;
END //

-- Valida disponibilidad del ejemplar, estado del lector y límite de préstamos
CREATE TRIGGER trg_validar_antes_insertar_detalle_prestamo
BEFORE INSERT ON detalle_prestamo
FOR EACH ROW
BEGIN
    DECLARE v_estado_ejemplar VARCHAR(20);
    DECLARE v_id_lector INT;
    DECLARE v_estado_lector VARCHAR(20);
    DECLARE v_max_prestamos INT;
    DECLARE v_prestamos_activos INT;

    SELECT estado INTO v_estado_ejemplar
    FROM ejemplares
    WHERE id_ejemplar = NEW.id_ejemplar;

    IF v_estado_ejemplar IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El ejemplar indicado no existe.';
    END IF;

    IF v_estado_ejemplar <> 'disponible' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El ejemplar no está disponible para préstamo.';
    END IF;

    SELECT p.id_lector, l.estado_lector, l.max_prestamos
    INTO v_id_lector, v_estado_lector, v_max_prestamos
    FROM prestamos p
    INNER JOIN lectores l ON l.id_lector = p.id_lector
    WHERE p.id_prestamo = NEW.id_prestamo;

    IF v_estado_lector <> 'activo' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El lector no se encuentra activo para recibir préstamos.';
    END IF;

    SELECT COUNT(*) INTO v_prestamos_activos
    FROM detalle_prestamo dp
    INNER JOIN prestamos p ON p.id_prestamo = dp.id_prestamo
    WHERE p.id_lector = v_id_lector
      AND dp.fecha_devolucion_real IS NULL;

    IF v_prestamos_activos >= v_max_prestamos THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El lector alcanzó el límite máximo de préstamos activos.';
    END IF;
END //

-- Bloquea la baja de un título con ejemplares prestados
CREATE TRIGGER trg_bloquear_baja_libro_con_prestamos
BEFORE UPDATE ON libros
FOR EACH ROW
BEGIN
    DECLARE v_en_prestamo INT DEFAULT 0;

    IF NEW.activo = 0 AND OLD.activo = 1 THEN
        SELECT COUNT(*) INTO v_en_prestamo
        FROM ejemplares
        WHERE id_libro = OLD.id_libro
          AND estado = 'prestado';

        IF v_en_prestamo > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede dar de baja el título: tiene ejemplares prestados.';
        END IF;
    END IF;
END //

DELIMITER ;
