-- =====================================================
-- 08 - Reportes (vistas y consultas)
--
-- QUÉ SE HACE AQUÍ:
-- Reporte 1: libros más solicitados -> JOIN de 3+ tablas, COUNT, GROUP BY, ORDER BY.
-- Reporte 2: lectores con préstamos vencidos -> subconsulta/EXISTS, HAVING, fn_calcular_dias_retraso.
-- Vistas reutilizables v_reporte_libros_mas_solicitados y v_reporte_lectores_vencidos
-- para que el backend (ReporteRepository) y la pantalla Reportes solo hagan SELECT.
-- =====================================================

USE sissebook;

-- ===========================================================
-- REPORTE 1: Libros más solicitados
-- JOIN ejemplares + detalle_prestamo + libros + categorias
-- COUNT de préstamos, GROUP BY, ORDER BY descendente.
-- ===========================================================
CREATE OR REPLACE VIEW v_reporte_libros_mas_solicitados AS
SELECT
    l.id_libro,
    l.titulo,
    l.autor,
    c.nombre_categoria            AS categoria,
    COUNT(dp.id_detalle)          AS total_prestamos,
    COUNT(DISTINCT p.id_lector)   AS lectores_distintos,
    fn_contar_ejemplares_disponibles(l.id_libro) AS ejemplares_disponibles
FROM libros l
INNER JOIN categorias        c  ON c.id_categoria = l.id_categoria
INNER JOIN ejemplares        e  ON e.id_libro = l.id_libro
INNER JOIN detalle_prestamo  dp ON dp.id_ejemplar = e.id_ejemplar
INNER JOIN prestamos         p  ON p.id_prestamo = dp.id_prestamo
GROUP BY l.id_libro, l.titulo, l.autor, c.nombre_categoria
ORDER BY total_prestamos DESC, l.titulo ASC;

-- Versión con filtro por rango de fechas (opcional, usada por el backend):
-- SELECT ... WHERE p.fecha_prestamo BETWEEN ? AND ? GROUP BY ...

-- ===========================================================
-- REPORTE 2: Lectores con préstamos vencidos
-- Subconsulta EXISTS + HAVING + fn_calcular_dias_retraso.
-- ===========================================================
CREATE OR REPLACE VIEW v_reporte_lectores_vencidos AS
SELECT
    l.id_lector,
    l.matricula,
    l.nombre_completo,
    l.correo,
    COUNT(dp.id_detalle)                                                     AS prestamos_pendientes,
    MAX(fn_calcular_dias_retraso(p.fecha_vencimiento, NULL))                 AS max_dias_retraso,
    SUM(fn_calcular_dias_retraso(p.fecha_vencimiento, NULL))                 AS total_dias_retraso
FROM lectores l
INNER JOIN prestamos        p  ON p.id_lector = l.id_lector
INNER JOIN detalle_prestamo dp ON dp.id_prestamo = p.id_prestamo
WHERE dp.fecha_devolucion_real IS NULL
  AND EXISTS (
        SELECT 1
        FROM detalle_prestamo dpx
        INNER JOIN prestamos px ON px.id_prestamo = dpx.id_prestamo
        WHERE px.id_lector = l.id_lector
          AND dpx.fecha_devolucion_real IS NULL
          AND px.fecha_vencimiento < NOW()
  )
GROUP BY l.id_lector, l.matricula, l.nombre_completo, l.correo
HAVING max_dias_retraso > 0
ORDER BY max_dias_retraso DESC, l.nombre_completo ASC;

-- ===========================================================
-- Consultas de demostración (ejecutar manualmente si se desea)
-- ===========================================================
-- SELECT * FROM v_reporte_libros_mas_solicitados LIMIT 10;
-- SELECT * FROM v_reporte_lectores_vencidos;
