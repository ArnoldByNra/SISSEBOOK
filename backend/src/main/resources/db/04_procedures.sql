-- =====================================================
-- 04 - Stored procedures (con transacciones)
--
-- QUÉ SE HACE AQUÍ:
-- sp_registrar_prestamo_codigo  -> *** TRANSACCIÓN ***: registra préstamo + detalle,
--     marca ejemplar como 'prestado', registra movimiento y hace COMMIT / ROLLBACK.
-- sp_registrar_devolucion_codigo -> *** TRANSACCIÓN ***: cierra el detalle, calcula
--     retraso con fn_calcular_dias_retraso, deja el ejemplar 'disponible', actualiza
--     el estado del préstamo, registra movimiento y hace COMMIT / ROLLBACK.
-- sp_registrar_prestamo_multiple -> *** TRANSACCIÓN ***: UN préstamo con varios ejemplares
--     (códigos separados por coma). Si uno falla, no se registra ninguno (ROLLBACK).
-- sp_consultar_disponibilidad_codigo -> devuelve estado del ejemplar + disponibles del título.
-- Incluye parámetros IN y OUT (usados desde repository/*Repository.java con CallableStatement).
-- =====================================================

USE sissebook;
DELIMITER //

CREATE PROCEDURE sp_registrar_prestamo_codigo(
    IN  p_codigo_barras      VARCHAR(64),
    IN  p_id_lector          INT,
    IN  p_id_usuario         INT,
    IN  p_dias_plazo         INT,
    OUT p_id_prestamo_generado INT
)
BEGIN
    DECLARE v_id_ejemplar INT;
    DECLARE v_plazo INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET v_plazo = IFNULL(p_dias_plazo, 14);

    START TRANSACTION;

    SELECT id_ejemplar INTO v_id_ejemplar
    FROM ejemplares
    WHERE codigo_barras = p_codigo_barras
    FOR UPDATE;

    IF v_id_ejemplar IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Código de barras no encontrado en ejemplares.';
    END IF;

    INSERT INTO prestamos (id_lector, id_usuario_registro, fecha_prestamo, fecha_vencimiento, estado_prestamo)
    VALUES (p_id_lector, p_id_usuario, NOW(), DATE_ADD(NOW(), INTERVAL v_plazo DAY), 'activo');

    SET p_id_prestamo_generado = LAST_INSERT_ID();

    INSERT INTO detalle_prestamo (id_prestamo, id_ejemplar, estado_entrega)
    VALUES (p_id_prestamo_generado, v_id_ejemplar, 'pendiente');

    UPDATE ejemplares
    SET estado = 'prestado'
    WHERE id_ejemplar = v_id_ejemplar;

    INSERT INTO movimientos_biblioteca (tipo_movimiento, id_ejemplar, id_lector, id_usuario, descripcion)
    VALUES ('prestamo', v_id_ejemplar, p_id_lector, p_id_usuario,
            CONCAT('Préstamo #', p_id_prestamo_generado, ' por ', v_plazo, ' días.'));

    COMMIT;
END //

-- Un solo préstamo con N ejemplares, todo o nada. p_codigos: códigos de barras separados por coma.
CREATE PROCEDURE sp_registrar_prestamo_multiple(
    IN  p_codigos              TEXT,
    IN  p_id_lector            INT,
    IN  p_id_usuario           INT,
    IN  p_dias_plazo           INT,
    OUT p_id_prestamo_generado INT,
    OUT p_total_ejemplares     INT
)
BEGIN
    DECLARE v_resto TEXT;
    DECLARE v_codigo VARCHAR(64);
    DECLARE v_pos INT;
    DECLARE v_id_ejemplar INT;
    DECLARE v_estado VARCHAR(20);
    DECLARE v_plazo INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SET v_plazo = IFNULL(p_dias_plazo, 14);
    SET p_total_ejemplares = 0;
    SET v_resto = CONCAT(IFNULL(p_codigos, ''), ',');

    START TRANSACTION;

    INSERT INTO prestamos (id_lector, id_usuario_registro, fecha_prestamo, fecha_vencimiento, estado_prestamo)
    VALUES (p_id_lector, p_id_usuario, NOW(), DATE_ADD(NOW(), INTERVAL v_plazo DAY), 'activo');

    SET p_id_prestamo_generado = LAST_INSERT_ID();

    WHILE LOCATE(',', v_resto) > 0 DO
        SET v_pos = LOCATE(',', v_resto);
        SET v_codigo = TRIM(SUBSTRING(v_resto, 1, v_pos - 1));
        SET v_resto = SUBSTRING(v_resto, v_pos + 1);

        IF v_codigo <> '' THEN
            SET v_id_ejemplar = NULL;
            SET v_estado = NULL;

            SELECT id_ejemplar, estado INTO v_id_ejemplar, v_estado
            FROM ejemplares
            WHERE codigo_barras = v_codigo
            FOR UPDATE;

            IF v_id_ejemplar IS NULL THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Código de barras no encontrado en ejemplares.';
            END IF;

            IF v_estado <> 'disponible' THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Uno de los ejemplares no está disponible para préstamo.';
            END IF;

            INSERT INTO detalle_prestamo (id_prestamo, id_ejemplar, estado_entrega)
            VALUES (p_id_prestamo_generado, v_id_ejemplar, 'pendiente');

            UPDATE ejemplares
            SET estado = 'prestado'
            WHERE id_ejemplar = v_id_ejemplar;

            INSERT INTO movimientos_biblioteca (tipo_movimiento, id_ejemplar, id_lector, id_usuario, descripcion)
            VALUES ('prestamo', v_id_ejemplar, p_id_lector, p_id_usuario,
                    CONCAT('Préstamo #', p_id_prestamo_generado, ' por ', v_plazo, ' días.'));

            SET p_total_ejemplares = p_total_ejemplares + 1;
        END IF;
    END WHILE;

    IF p_total_ejemplares = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Debes indicar al menos un código de barras.';
    END IF;

    COMMIT;
END //

CREATE PROCEDURE sp_registrar_devolucion_codigo(
    IN  p_codigo_barras VARCHAR(64),
    IN  p_id_usuario     INT,
    OUT p_dias_retraso   INT
)
BEGIN
    DECLARE v_id_ejemplar INT;
    DECLARE v_id_detalle INT;
    DECLARE v_id_prestamo INT;
    DECLARE v_id_lector INT;
    DECLARE v_fecha_vencimiento DATETIME;
    DECLARE v_pendientes_restantes INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    SELECT id_ejemplar INTO v_id_ejemplar
    FROM ejemplares
    WHERE codigo_barras = p_codigo_barras
    FOR UPDATE;

    IF v_id_ejemplar IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Código de barras no registrado.';
    END IF;

    SELECT dp.id_detalle, dp.id_prestamo, p.id_lector, p.fecha_vencimiento
    INTO v_id_detalle, v_id_prestamo, v_id_lector, v_fecha_vencimiento
    FROM detalle_prestamo dp
    INNER JOIN prestamos p ON p.id_prestamo = dp.id_prestamo
    WHERE dp.id_ejemplar = v_id_ejemplar
      AND dp.fecha_devolucion_real IS NULL
    LIMIT 1;

    IF v_id_detalle IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El ejemplar no tiene un préstamo activo pendiente de devolución.';
    END IF;

    SET p_dias_retraso = fn_calcular_dias_retraso(v_fecha_vencimiento, NOW());

    UPDATE detalle_prestamo
    SET fecha_devolucion_real = NOW(),
        dias_retraso = p_dias_retraso,
        estado_entrega = IF(p_dias_retraso > 0, 'atrasada', 'puntual'),
        id_usuario_recepcion = p_id_usuario
    WHERE id_detalle = v_id_detalle;

    UPDATE ejemplares
    SET estado = 'disponible'
    WHERE id_ejemplar = v_id_ejemplar;

    SELECT COUNT(*) INTO v_pendientes_restantes
    FROM detalle_prestamo
    WHERE id_prestamo = v_id_prestamo
      AND fecha_devolucion_real IS NULL;

    IF v_pendientes_restantes = 0 THEN
        UPDATE prestamos
        SET estado_prestamo = 'devuelto'
        WHERE id_prestamo = v_id_prestamo;
    END IF;

    INSERT INTO movimientos_biblioteca (tipo_movimiento, id_ejemplar, id_lector, id_usuario, descripcion)
    VALUES ('devolucion', v_id_ejemplar, v_id_lector, p_id_usuario,
            CONCAT('Devolución de préstamo #', v_id_prestamo, ' con ', p_dias_retraso, ' día(s) de retraso.'));

    COMMIT;
END //

CREATE PROCEDURE sp_consultar_disponibilidad_codigo(
    IN  p_codigo_barras      VARCHAR(64),
    OUT p_estado_ejemplar    VARCHAR(20),
    OUT p_disponibles_titulo INT
)
BEGIN
    DECLARE v_id_libro INT;

    SELECT e.estado, e.id_libro
    INTO p_estado_ejemplar, v_id_libro
    FROM ejemplares e
    WHERE e.codigo_barras = p_codigo_barras;

    IF v_id_libro IS NULL THEN
        SET p_estado_ejemplar = 'no_encontrado';
        SET p_disponibles_titulo = 0;
    ELSE
        SET p_disponibles_titulo = fn_contar_ejemplares_disponibles(v_id_libro);
    END IF;
END //

-- Parámetro OUT de ejemplo: total de ejemplares disponibles de un título
CREATE PROCEDURE sp_total_disponibles_libro(
    IN  p_id_libro INT,
    OUT p_total    INT
)
BEGIN
    SET p_total = fn_contar_ejemplares_disponibles(p_id_libro);
END //

-- Historial de préstamos de un ejemplar
CREATE PROCEDURE sp_historial_ejemplar(
    IN p_id_ejemplar INT
)
BEGIN
    SELECT dp.id_detalle,
           dp.id_prestamo,
           p.fecha_prestamo,
           p.fecha_vencimiento,
           dp.fecha_devolucion_real,
           dp.dias_retraso,
           dp.estado_entrega,
           l.nombre_completo AS lector
    FROM detalle_prestamo dp
    INNER JOIN prestamos p ON p.id_prestamo = dp.id_prestamo
    INNER JOIN lectores l  ON l.id_lector = p.id_lector
    WHERE dp.id_ejemplar = p_id_ejemplar
    ORDER BY p.fecha_prestamo DESC;
END //

DELIMITER ;
