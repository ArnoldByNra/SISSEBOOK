-- =====================================================
-- 01 - Esquema
-- Motor: MySQL 8.x (InnoDB, utf8mb4_unicode_ci)
--
-- QUÉ SE HACE AQUÍ:
-- Crea la BD `sissebook` y las tablas del diagrama ER:
--   roles, usuarios_sistema, categorias, libros, ejemplares, lectores,
--   prestamos, detalle_prestamo, movimientos_biblioteca, bitacora_auditoria.
-- Incluye PK, FK, UNIQUE (ejemplares.codigo_barras, usuarios_sistema.nombre_usuario,
-- lectores.matricula, libros.isbn) y CHECK/NOT NULL de integridad.
-- =====================================================

DROP DATABASE IF EXISTS sissebook;
CREATE DATABASE sissebook CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sissebook;

-- 1. Catálogo de roles del sistema
CREATE TABLE roles (
    id_rol        INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol    VARCHAR(30)  NOT NULL UNIQUE,
    descripcion   VARCHAR(150) NOT NULL
) ENGINE=InnoDB;

-- 2. Usuarios del sistema (administrador/operador/consulta)
CREATE TABLE usuarios_sistema (
    id_usuario      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(120) NOT NULL,
    nombre_usuario  VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    id_rol          INT NOT NULL,
    activo          TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
) ENGINE=InnoDB;

-- 3. Categorías temáticas de libros
CREATE TABLE categorias (
    id_categoria     INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(80)  NOT NULL UNIQUE,
    descripcion      VARCHAR(200) NULL
) ENGINE=InnoDB;

-- 4. Títulos (libros)
CREATE TABLE libros (
    id_libro          INT AUTO_INCREMENT PRIMARY KEY,
    isbn              VARCHAR(20)  NOT NULL UNIQUE,
    titulo            VARCHAR(200) NOT NULL,
    autor             VARCHAR(150) NOT NULL,
    editorial         VARCHAR(120) NOT NULL,
    id_categoria      INT NOT NULL,
    anio_publicacion  SMALLINT NOT NULL,
    activo            TINYINT(1) NOT NULL DEFAULT 1,
    fecha_registro    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_libro_categoria FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria),
    CONSTRAINT chk_anio_valido CHECK (anio_publicacion BETWEEN 1450 AND 2100)
) ENGINE=InnoDB;

-- 5. Ejemplares físicos (cada uno con su código de barras UNIQUE)
CREATE TABLE ejemplares (
    id_ejemplar        INT AUTO_INCREMENT PRIMARY KEY,
    id_libro           INT NOT NULL,
    codigo_barras      VARCHAR(64) NOT NULL UNIQUE,
    codigo_interno     VARCHAR(40) NOT NULL UNIQUE,
    estado             ENUM('disponible', 'prestado', 'en_revision', 'baja') NOT NULL DEFAULT 'disponible',
    ubicacion_estante  VARCHAR(50) NOT NULL DEFAULT 'General',
    fecha_alta         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ejemplar_libro FOREIGN KEY (id_libro) REFERENCES libros(id_libro),
    CONSTRAINT chk_codigo_barras_no_vacio CHECK (CHAR_LENGTH(TRIM(codigo_barras)) > 0)
) ENGINE=InnoDB;

-- 6. Lectores (usuarios de la biblioteca)
CREATE TABLE lectores (
    id_lector        INT AUTO_INCREMENT PRIMARY KEY,
    matricula        VARCHAR(40)  NOT NULL UNIQUE,
    nombre_completo  VARCHAR(120) NOT NULL,
    carrera_o_area   VARCHAR(120) NULL,
    correo           VARCHAR(120) NULL,
    estado_lector    ENUM('activo', 'suspendido', 'inactivo') NOT NULL DEFAULT 'activo',
    max_prestamos    TINYINT UNSIGNED NOT NULL DEFAULT 3,
    fecha_registro   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_max_prestamos_pos CHECK (max_prestamos BETWEEN 1 AND 10)
) ENGINE=InnoDB;

-- 7. Cabecera de préstamos
CREATE TABLE prestamos (
    id_prestamo         INT AUTO_INCREMENT PRIMARY KEY,
    id_lector           INT NOT NULL,
    id_usuario_registro INT NOT NULL,
    fecha_prestamo      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento   DATETIME NOT NULL,
    estado_prestamo     ENUM('activo', 'devuelto', 'atrasado') NOT NULL DEFAULT 'activo',
    observaciones       VARCHAR(255) NULL,
    CONSTRAINT fk_prestamo_lector  FOREIGN KEY (id_lector) REFERENCES lectores(id_lector),
    CONSTRAINT fk_prestamo_usuario FOREIGN KEY (id_usuario_registro) REFERENCES usuarios_sistema(id_usuario),
    CONSTRAINT chk_fechas_prestamo CHECK (fecha_vencimiento >= fecha_prestamo)
) ENGINE=InnoDB;

-- 8. Detalle de préstamos (un renglón por ejemplar)
CREATE TABLE detalle_prestamo (
    id_detalle             INT AUTO_INCREMENT PRIMARY KEY,
    id_prestamo            INT NOT NULL,
    id_ejemplar            INT NOT NULL,
    fecha_devolucion_real  DATETIME NULL,
    dias_retraso           INT NOT NULL DEFAULT 0,
    estado_entrega         ENUM('pendiente', 'puntual', 'atrasada') NOT NULL DEFAULT 'pendiente',
    id_usuario_recepcion   INT NULL,
    CONSTRAINT fk_detalle_prestamo    FOREIGN KEY (id_prestamo) REFERENCES prestamos(id_prestamo),
    CONSTRAINT fk_detalle_ejemplar    FOREIGN KEY (id_ejemplar) REFERENCES ejemplares(id_ejemplar),
    CONSTRAINT fk_detalle_usuario_rec FOREIGN KEY (id_usuario_recepcion) REFERENCES usuarios_sistema(id_usuario),
    CONSTRAINT chk_dias_retraso_no_neg CHECK (dias_retraso >= 0)
) ENGINE=InnoDB;

-- 9. Bitácora de movimientos operativos
CREATE TABLE movimientos_biblioteca (
    id_movimiento    INT AUTO_INCREMENT PRIMARY KEY,
    tipo_movimiento  ENUM('prestamo', 'devolucion', 'alta_ejemplar', 'baja_ejemplar', 'ajuste') NOT NULL,
    id_ejemplar      INT NOT NULL,
    id_lector        INT NULL,
    id_usuario       INT NOT NULL,
    fecha_hora       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion      VARCHAR(255) NOT NULL,
    CONSTRAINT fk_mov_ejemplar FOREIGN KEY (id_ejemplar) REFERENCES ejemplares(id_ejemplar),
    CONSTRAINT fk_mov_lector   FOREIGN KEY (id_lector)   REFERENCES lectores(id_lector),
    CONSTRAINT fk_mov_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuarios_sistema(id_usuario)
) ENGINE=InnoDB;

-- 10. Bitácora de auditoría (alimentada por triggers)
CREATE TABLE bitacora_auditoria (
    id_auditoria        BIGINT AUTO_INCREMENT PRIMARY KEY,
    tabla_afectada      VARCHAR(60) NOT NULL,
    tipo_operacion      ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    id_registro         VARCHAR(60) NOT NULL,
    fecha_hora          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_responsable VARCHAR(100) NOT NULL,
    informacion_anterior JSON NULL,
    informacion_nueva    JSON NULL
) ENGINE=InnoDB;
