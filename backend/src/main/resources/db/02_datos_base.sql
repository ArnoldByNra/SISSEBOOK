-- =====================================================
-- 02 - Datos base
--
-- QUÉ SE HACE AQUÍ:
-- Insertar roles (Administrador, Operador, Consulta), las categorías iniciales
-- y los usuarios del sistema (uno por rol).
-- SEGURIDAD: la contraseña se guarda como hash BCrypt (como exige el backend en
-- security/PasswordService.java). Nunca en texto plano.
--
-- Credenciales de prueba (contraseña en claro -> NO se usan en la BD):
--   amartinez  ->  Admin1234*   (rol Administrador)
--   cherrera   ->  Oper1234*    (rol Operador)
--   lmendoza   ->  Cons1234*    (rol Consulta)
-- =====================================================

USE sissebook;

-- Roles del sistema
INSERT INTO roles (nombre_rol, descripcion) VALUES
('Administrador', 'Control total del catálogo, usuarios, préstamos, devoluciones y reportes'),
('Operador',      'Gestión de préstamos, devoluciones, lectores y consulta de catálogo'),
('Consulta',      'Solo lectura de catálogo, disponibilidad y reportes básicos');

-- Usuarios del sistema (uno por rol) con hash BCrypt verificado
INSERT INTO usuarios_sistema (nombre_completo, nombre_usuario, password_hash, id_rol, activo) VALUES
('Ana Martínez',   'amartinez', '$2a$10$kz0SxPRNKS6Tixt57/kj0.K9CM6v2NawVx5K2rd5oMG7oTI0AXmoa', 1, 1),
('Carlos Herrera', 'cherrera',  '$2a$10$kozYkl0QXBbmr5xxtFdgU.ypkbvGGvKEA/QabwOcBqbJVw/wVQ/4m', 2, 1),
('Lucía Mendoza',  'lmendoza',  '$2a$10$kw1EIm5ycBgv3Mt7opd14eiOBXNt5ei91/Jyzkh.EXmq3hcx2/N1O', 3, 1);

-- Categorías iniciales
INSERT INTO categorias (nombre_categoria, descripcion) VALUES
('Literatura',             'Novela, cuento, poesía y narrativa'),
('Desarrollo Personal',    'Hábitos, productividad y crecimiento'),
('Historia',               'Historia universal, ensayos y divulgación'),
('Ciencia y Tecnología',   'Informática, ciencias y técnica');
