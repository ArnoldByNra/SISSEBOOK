-- =====================================================
-- 07 - Datos de prueba
--
-- QUÉ SE HACE AQUÍ:
-- Libros y ejemplares con código de barras reales (para imprimir y escanear),
-- lectores, y préstamos de ejemplo (activos, vencidos y devueltos) que permiten
-- demostrar disponibilidad, devoluciones con retraso y reportes.
-- Se ejecuta DESPUÉS de 02_datos_base.sql.
-- =====================================================

USE sissebook;

-- ---------------------------------------------------------------------------
-- Libros (ids 1..20)
-- ---------------------------------------------------------------------------
INSERT INTO libros (isbn, titulo, autor, editorial, id_categoria, anio_publicacion, activo) VALUES
('9780307474728', 'Cien años de soledad',              'Gabriel García Márquez',  'Sudamericana', 1, 1967, 1),
('9780156012195', 'El principito',                     'Antoine de Saint-Exupéry','Salamandra',  1, 1943, 1),
('9781847941831', 'Hábitos atómicos',                  'James Clear',            'Paidós',       2, 2018, 1),
('9788420412146', 'Don Quijote de la Mancha',          'Miguel de Cervantes',    'Alfaguara',    1, 1605, 1),
('9789681603397', 'La tregua',                         'Mario Benedetti',        'Planeta',      1, 1960, 1),
('9780062316097', 'Sapiens: De animales a dioses',     'Yuval Noah Harari',      'Debate',       3, 2011, 1),
('9788437604947', 'Crónica de una muerte anunciada',   'Gabriel García Márquez', 'Sudamericana', 1, 1981, 1),
('9788490664186', 'El poder del ahora',                'Eckhart Tolle',          'Gaia',         2, 1997, 1),
('9788499922348', 'Cómo ganar amigos e influir sobre las personas', 'Dale Carnegie','El Ateneo', 2, 1936, 1),
('9789706077696', 'El arte de la guerra',              'Sun Tzu',                'Tomo',         3, 1981, 1),
('9788491051166', 'Veinte poemas de amor',             'Pablo Neruda',           'Planeta',      1, 1924, 1),
('9780141439518', 'Orgullo y prejuicio',               'Jane Austen',            'Penguin',      1, 1813, 1),
('9786073119459', 'Elon Musk: Tesla, SpaceX',          'Ashlee Vance',           'Paidós',       4, 2015, 1),
('9786077470350', 'Clean Code',                        'Robert C. Martin',       'Prentice Hall',4, 2008, 1),
('9788441532106', 'El universo en una cáscara de nuez','Stephen Hawking',        'Crítica',      4, 2001, 1),
('9788434414543', 'Breve historia del tiempo',         'Stephen Hawking',        'Crítica',      4, 1988, 1),
('9786070737465', 'La sombra del viento',              'Carlos Ruiz Zafón',      'Planeta',      1, 2001, 1),
('9789584288044', 'Padre rico, padre pobre',           'Robert T. Kiyosaki',     'Aguilar',      2, 1997, 1),
('9786073814171', 'Inteligencia emocional',            'Daniel Goleman',         'Kairós',       2, 1995, 1),
('9788498387675', 'Historia de la humanidad',          'VV.AA.',                 'Salvat',       3, 2010, 1);

-- ---------------------------------------------------------------------------
-- Ejemplares (40 ejemplares, 2 por libro)
-- Códigos de barras EAN-13: 750100000XXYY
-- ---------------------------------------------------------------------------
INSERT INTO ejemplares (id_libro, codigo_barras, codigo_interno, estado, ubicacion_estante) VALUES
(1,  '7501000001011', 'EJ-CAS-001', 'disponible', 'Estante A1'),
(1,  '7501000001012', 'EJ-CAS-002', 'disponible', 'Estante A1'),
(2,  '7501000002011', 'EJ-PRI-001', 'disponible', 'Estante A2'),
(2,  '7501000002012', 'EJ-PRI-002', 'disponible', 'Estante A2'),
(3,  '7501000003011', 'EJ-HAB-001', 'disponible', 'Estante B1'),
(3,  '7501000003012', 'EJ-HAB-002', 'disponible', 'Estante B1'),
(4,  '7501000004011', 'EJ-QUI-001', 'disponible', 'Estante A3'),
(4,  '7501000004012', 'EJ-QUI-002', 'disponible', 'Estante A3'),
(5,  '7501000005011', 'EJ-TRE-001', 'disponible', 'Estante A4'),
(5,  '7501000005012', 'EJ-TRE-002', 'disponible', 'Estante A4'),
(6,  '7501000006011', 'EJ-SAP-001', 'disponible', 'Estante C1'),
(6,  '7501000006012', 'EJ-SAP-002', 'disponible', 'Estante C1'),
(7,  '7501000007011', 'EJ-CRO-001', 'disponible', 'Estante A1'),
(7,  '7501000007012', 'EJ-CRO-002', 'disponible', 'Estante A1'),
(8,  '7501000008011', 'EJ-POD-001', 'disponible', 'Estante B2'),
(8,  '7501000008012', 'EJ-POD-002', 'disponible', 'Estante B2'),
(9,  '7501000009011', 'EJ-COM-001', 'disponible', 'Estante B2'),
(9,  '7501000009012', 'EJ-COM-002', 'disponible', 'Estante B2'),
(10, '7501000010011', 'EJ-ART-001', 'disponible', 'Estante C2'),
(10, '7501000010012', 'EJ-ART-002', 'disponible', 'Estante C2'),
(11, '7501000011011', 'EJ-VEI-001', 'disponible', 'Estante A4'),
(11, '7501000011012', 'EJ-VEI-002', 'disponible', 'Estante A4'),
(12, '7501000012011', 'EJ-ORG-001', 'disponible', 'Estante A5'),
(12, '7501000012012', 'EJ-ORG-002', 'disponible', 'Estante A5'),
(13, '7501000013011', 'EJ-MUS-001', 'disponible', 'Estante D1'),
(13, '7501000013012', 'EJ-MUS-002', 'disponible', 'Estante D1'),
(14, '7501000014011', 'EJ-CLE-001', 'disponible', 'Estante D1'),
(14, '7501000014012', 'EJ-CLE-002', 'disponible', 'Estante D1'),
(15, '7501000015011', 'EJ-UNI-001', 'disponible', 'Estante D2'),
(15, '7501000015012', 'EJ-UNI-002', 'disponible', 'Estante D2'),
(16, '7501000016011', 'EJ-BRE-001', 'disponible', 'Estante D2'),
(16, '7501000016012', 'EJ-BRE-002', 'disponible', 'Estante D2'),
(17, '7501000017011', 'EJ-SOM-001', 'disponible', 'Estante A5'),
(17, '7501000017012', 'EJ-SOM-002', 'disponible', 'Estante A5'),
(18, '7501000018011', 'EJ-PAD-001', 'disponible', 'Estante B3'),
(18, '7501000018012', 'EJ-PAD-002', 'disponible', 'Estante B3'),
(19, '7501000019011', 'EJ-INT-001', 'disponible', 'Estante B3'),
(19, '7501000019012', 'EJ-INT-002', 'disponible', 'Estante B3'),
(20, '7501000020011', 'EJ-HIS-001', 'disponible', 'Estante C3'),
(20, '7501000020012', 'EJ-HIS-002', 'disponible', 'Estante C3');

-- ---------------------------------------------------------------------------
-- Lectores (10)
-- ---------------------------------------------------------------------------
INSERT INTO lectores (matricula, nombre_completo, carrera_o_area, correo, estado_lector, max_prestamos) VALUES
('MAT-2026-001', 'Sofía Ramírez López',   'Ingeniería en Sistemas', 'sofia.ramirez@ejemplo.edu',  'activo',     3),
('MAT-2026-002', 'Diego Morales Vega',    'Administración',         'diego.morales@ejemplo.edu',  'activo',     3),
('MAT-2026-003', 'Valeria Cruz Ortiz',    'Industrial',             'valeria.cruz@ejemplo.edu',   'activo',     3),
('MAT-2026-004', 'Héctor Núñez Paredes',  'Contabilidad',           'hector.nunez@ejemplo.edu',   'activo',     2),
('MAT-2026-005', 'Camila Rojas Fuentes',  'Derecho',                'camila.rojas@ejemplo.edu',   'activo',     3),
('MAT-2026-006', 'Luis Ángel Domínguez',  'Ingeniería en Sistemas', 'luis.dominguez@ejemplo.edu', 'suspendido', 3),
('MAT-2026-007', 'Paola Whites Torres',   'Diseño Gráfico',         'paola.whites@ejemplo.edu',   'activo',     3),
('MAT-2026-008', 'Ricardo Salinas Bravo', 'Mecánica',               'ricardo.salinas@ejemplo.edu','activo',     2),
('MAT-2026-009', 'Fernanda Cano Ríos',    'Psicología',             'fernanda.cano@ejemplo.edu',  'inactivo',   3),
('MAT-2026-010', 'Jorge Estrada Lira',    'Arquitectura',           'jorge.estrada@ejemplo.edu',  'activo',     3);

-- ---------------------------------------------------------------------------
-- Préstamos de ejemplo
-- id_usuario: 1=Admin, 2=Operador
-- ---------------------------------------------------------------------------

-- Préstamo 1: Sofía, activo y vigente (ejemplar 1 y 3)
INSERT INTO prestamos (id_lector, id_usuario_registro, fecha_prestamo, fecha_vencimiento, estado_prestamo) VALUES
(1, 2, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 11 DAY), 'activo');
INSERT INTO detalle_prestamo (id_prestamo, id_ejemplar, estado_entrega) VALUES
(1, 1, 'pendiente'), (1, 3, 'pendiente');
UPDATE ejemplares SET estado = 'prestado' WHERE id_ejemplar IN (1, 3);

-- Préstamo 2: Diego, activo y VENCIDO (ejemplar 5) -> para reporte de vencidos
INSERT INTO prestamos (id_lector, id_usuario_registro, fecha_prestamo, fecha_vencimiento, estado_prestamo) VALUES
(2, 2, DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), 'atrasado');
INSERT INTO detalle_prestamo (id_prestamo, id_ejemplar, estado_entrega) VALUES
(2, 5, 'pendiente');
UPDATE ejemplares SET estado = 'prestado' WHERE id_ejemplar = 5;

-- Préstamo 3: Valeria, ya DEVUELTO a tiempo (ejemplar 7)
INSERT INTO prestamos (id_lector, id_usuario_registro, fecha_prestamo, fecha_vencimiento, estado_prestamo) VALUES
(3, 2, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), 'devuelto');
INSERT INTO detalle_prestamo (id_prestamo, id_ejemplar, fecha_devolucion_real, dias_retraso, estado_entrega, id_usuario_recepcion) VALUES
(3, 7, DATE_SUB(NOW(), INTERVAL 18 DAY), 0, 'puntual', 2);

-- Préstamo 4: Héctor, DEVUELTO con retraso (ejemplar 9)
INSERT INTO prestamos (id_lector, id_usuario_registro, fecha_prestamo, fecha_vencimiento, estado_prestamo) VALUES
(4, 2, DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY), 'devuelto');
INSERT INTO detalle_prestamo (id_prestamo, id_ejemplar, fecha_devolucion_real, dias_retraso, estado_entrega, id_usuario_recepcion) VALUES
(4, 9, DATE_SUB(NOW(), INTERVAL 20 DAY), 6, 'atrasada', 1);

-- Préstamo 5: Camila, activo y VENCIDO (ejemplar 11 y 13)
INSERT INTO prestamos (id_lector, id_usuario_registro, fecha_prestamo, fecha_vencimiento, estado_prestamo) VALUES
(5, 1, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), 'atrasado');
INSERT INTO detalle_prestamo (id_prestamo, id_ejemplar, estado_entrega) VALUES
(5, 11, 'pendiente'), (5, 13, 'pendiente');
UPDATE ejemplares SET estado = 'prestado' WHERE id_ejemplar IN (11, 13);

-- Movimientos operativos derivados de los préstamos anteriores
INSERT INTO movimientos_biblioteca (tipo_movimiento, id_ejemplar, id_lector, id_usuario, descripcion) VALUES
('prestamo',   1,  1, 2, 'Préstamo #1 (ejemplar EJ-CAS-001).'),
('prestamo',   3,  1, 2, 'Préstamo #1 (ejemplar EJ-PRI-001).'),
('prestamo',   5,  2, 2, 'Préstamo #2 (ejemplar EJ-HAB-001).'),
('prestamo',   7,  3, 2, 'Préstamo #3 (ejemplar EJ-QUI-001).'),
('devolucion', 7,  3, 2, 'Devolución de préstamo #3 sin retraso.'),
('prestamo',   9,  4, 2, 'Préstamo #4 (ejemplar EJ-TRE-001).'),
('devolucion', 9,  4, 1, 'Devolución de préstamo #4 con 6 día(s) de retraso.'),
('prestamo',   11, 5, 1, 'Préstamo #5 (ejemplar EJ-VEI-001).'),
('prestamo',   13, 5, 1, 'Préstamo #5 (ejemplar EJ-CRO-001).');
