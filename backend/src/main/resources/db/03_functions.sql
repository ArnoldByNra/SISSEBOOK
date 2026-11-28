-- =====================================================
-- 03 - Funciones almacenadas
--
-- QUÉ SE HACE AQUÍ:
-- fn_calcular_dias_retraso(p_fecha_vencimiento, p_fecha_entrega)
--     -> días de retraso de un préstamo (0 si aún no vence o se entrega a tiempo).
-- fn_contar_ejemplares_disponibles(p_id_libro)
--     -> cantidad de ejemplares en estado 'disponible' de un título.
-- Se reutilizan dentro de procedimientos y reportes.
-- =====================================================

USE sissebook;
DELIMITER //

CREATE FUNCTION fn_calcular_dias_retraso(
    p_fecha_vencimiento DATETIME,
    p_fecha_entrega     DATETIME
) RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_referencia DATETIME;
    DECLARE v_dias INT;

    SET v_referencia = IFNULL(p_fecha_entrega, NOW());
    SET v_dias = DATEDIFF(DATE(v_referencia), DATE(p_fecha_vencimiento));

    IF v_dias < 0 THEN
        RETURN 0;
    END IF;

    RETURN v_dias;
END //

CREATE FUNCTION fn_contar_ejemplares_disponibles(
    p_id_libro INT
) RETURNS INT
READS SQL DATA
BEGIN
    DECLARE v_total INT DEFAULT 0;

    SELECT COUNT(*) INTO v_total
    FROM ejemplares
    WHERE id_libro = p_id_libro
      AND estado = 'disponible';

    RETURN v_total;
END //

DELIMITER ;
