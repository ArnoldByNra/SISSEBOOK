-- =====================================================
-- 05 - Triggers de validación
-- QUÉ SE VA A HACER AQUÍ:
-- trg_ejemplar_codigo_no_vacio (BEFORE INSERT/UPDATE ejemplares): rechaza codigo_barras vacío.
-- trg_detalle_prestamo_ejemplar_disponible (BEFORE INSERT detalle_prestamo): rechaza si el ejemplar no está DISPONIBLE.
-- trg_prestamo_limite_lector (BEFORE INSERT prestamos): rechaza si el lector alcanzó max_prestamos o está SUSPENDIDO.
-- Usar SIGNAL SQLSTATE '45000' con mensaje claro.

-- =====================================================

USE biblioteca;
DELIMITER $$
-- TODO
DELIMITER ;
