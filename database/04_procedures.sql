-- =====================================================
-- 04 - Stored procedures
-- QUÉ SE VA A HACER AQUÍ:
-- sp_registrar_prestamo(IN p_id_lector, IN p_id_usuario, IN p_codigo_barras, IN p_dias, OUT p_id_prestamo, OUT p_mensaje)
--     *** AQUÍ VA LA TRANSACCIÓN ***
--     START TRANSACTION; insertar prestamos + detalle_prestamo,
--     ejemplares.estado='PRESTADO', insertar movimientos_biblioteca;
--     COMMIT; ante error (EXIT HANDLER) -> ROLLBACK.
-- sp_registrar_devolucion(IN p_codigo_barras, IN p_id_usuario, OUT p_dias_retraso, OUT p_mensaje)
--     *** TRANSACCIÓN ***: cierra detalle_prestamo, calcula retraso con fn_dias_retraso,
--     ejemplares.estado='DISPONIBLE', actualiza prestamos.estado_prestamo, movimiento.
-- sp_consultar_ejemplar_por_codigo(IN p_codigo_barras)
--     devuelve libro + estado + ubicación del ejemplar (usa el lector).
-- sp_total_disponibles(IN p_id_libro, OUT p_total)  -> ejemplo de parámetro OUT.
-- sp_historial_ejemplar(IN p_id_ejemplar)

-- =====================================================

USE biblioteca;
DELIMITER $$
-- TODO
DELIMITER ;
