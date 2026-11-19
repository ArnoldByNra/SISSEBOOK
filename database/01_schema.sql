-- =====================================================
-- 01 - Esquema
-- QUÉ SE VA A HACER AQUÍ:
-- Crear BD `biblioteca` y las tablas del diagrama ER:
--   roles, usuarios_sistema, categorias, libros, ejemplares, lectores,
--   prestamos, detalle_prestamo, movimientos_biblioteca, bitacora_auditoria.
-- Incluir PK, FK, UNIQUE (ejemplares.codigo_barras, usuarios_sistema.nombre_usuario,
-- lectores.matricula, libros.isbn) y CHECK/NOT NULL de integridad.

-- =====================================================

CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

-- TODO: CREATE TABLE ...
