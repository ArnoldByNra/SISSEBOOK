package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import sissepbook.backend.dto.ReporteLectorVencidoDTO;
import sissepbook.backend.dto.ReporteLibroSolicitadoDTO;

import java.util.List;

/** Ejecuta las consultas de database/08_reportes.sql (vistas). Única capa que escribe SQL de reportes. */
public class ReporteRepository {

    private final JdbcTemplate jdbc;

    public ReporteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ReporteLibroSolicitadoDTO> librosMasSolicitados(int limite) {
        String sql = "SELECT id_libro, titulo, autor, categoria, total_prestamos, " +
                "lectores_distintos, ejemplares_disponibles " +
                "FROM v_reporte_libros_mas_solicitados ORDER BY total_prestamos DESC LIMIT ?";
        return jdbc.query(sql, (rs, i) -> {
            ReporteLibroSolicitadoDTO dto = new ReporteLibroSolicitadoDTO();
            dto.setIdLibro(rs.getInt("id_libro"));
            dto.setTitulo(rs.getString("titulo"));
            dto.setAutor(rs.getString("autor"));
            dto.setCategoria(rs.getString("categoria"));
            dto.setTotalPrestamos(rs.getInt("total_prestamos"));
            dto.setLectoresDistintos(rs.getInt("lectores_distintos"));
            dto.setEjemplaresDisponibles(rs.getInt("ejemplares_disponibles"));
            return dto;
        }, limite);
    }

    public List<ReporteLectorVencidoDTO> lectoresVencidos() {
        String sql = "SELECT id_lector, matricula, nombre_completo, correo, prestamos_pendientes, " +
                "max_dias_retraso, total_dias_retraso " +
                "FROM v_reporte_lectores_vencidos ORDER BY max_dias_retraso DESC";
        return jdbc.query(sql, (rs, i) -> {
            ReporteLectorVencidoDTO dto = new ReporteLectorVencidoDTO();
            dto.setIdLector(rs.getInt("id_lector"));
            dto.setMatricula(rs.getString("matricula"));
            dto.setNombreCompleto(rs.getString("nombre_completo"));
            dto.setCorreo(rs.getString("correo"));
            dto.setPrestamosPendientes(rs.getInt("prestamos_pendientes"));
            dto.setMaxDiasRetraso(rs.getInt("max_dias_retraso"));
            dto.setTotalDiasRetraso(rs.getInt("total_dias_retraso"));
            return dto;
        });
    }

    private static java.sql.Timestamp ts(java.time.LocalDate d) {
        return java.sql.Timestamp.valueOf(d.atStartOfDay());
    }

    private int contar(String sql, java.time.LocalDate desde, java.time.LocalDate hastaExcl) {
        Integer n = jdbc.queryForObject(sql, Integer.class, ts(desde), ts(hastaExcl));
        return n == null ? 0 : n;
    }

    /** Préstamos creados en [desde, hastaExcl). */
    public int prestamosEnPeriodo(java.time.LocalDate desde, java.time.LocalDate hastaExcl) {
        return contar("SELECT COUNT(*) FROM prestamos WHERE fecha_prestamo >= ? AND fecha_prestamo < ?", desde, hastaExcl);
    }

    /** Ejemplares devueltos en [desde, hastaExcl). */
    public int devolucionesEnPeriodo(java.time.LocalDate desde, java.time.LocalDate hastaExcl) {
        return contar("SELECT COUNT(*) FROM detalle_prestamo WHERE fecha_devolucion_real >= ? AND fecha_devolucion_real < ?",
                desde, hastaExcl);
    }

    /** Devoluciones puntuales en [desde, hastaExcl). */
    public int puntualesEnPeriodo(java.time.LocalDate desde, java.time.LocalDate hastaExcl) {
        return contar("SELECT COUNT(*) FROM detalle_prestamo WHERE estado_entrega = 'puntual' " +
                "AND fecha_devolucion_real >= ? AND fecha_devolucion_real < ?", desde, hastaExcl);
    }

    /** Préstamos por categoría en el periodo (JOIN de 5 tablas + GROUP BY). */
    public List<java.util.Map<String, Object>> prestamosPorCategoria(java.time.LocalDate desde, java.time.LocalDate hastaExcl) {
        String sql = "SELECT c.nombre_categoria AS categoria, COUNT(dp.id_detalle) AS total " +
                "FROM detalle_prestamo dp " +
                "INNER JOIN prestamos p ON p.id_prestamo = dp.id_prestamo " +
                "INNER JOIN ejemplares e ON e.id_ejemplar = dp.id_ejemplar " +
                "INNER JOIN libros l ON l.id_libro = e.id_libro " +
                "INNER JOIN categorias c ON c.id_categoria = l.id_categoria " +
                "WHERE p.fecha_prestamo >= ? AND p.fecha_prestamo < ? " +
                "GROUP BY c.nombre_categoria ORDER BY total DESC";
        return jdbc.queryForList(sql, ts(desde), ts(hastaExcl));
    }

    /** Los 5 libros con más préstamos en el periodo. */
    public List<java.util.Map<String, Object>> topLibrosEnPeriodo(java.time.LocalDate desde, java.time.LocalDate hastaExcl) {
        String sql = "SELECT l.titulo, l.autor, COUNT(dp.id_detalle) AS total " +
                "FROM detalle_prestamo dp " +
                "INNER JOIN prestamos p ON p.id_prestamo = dp.id_prestamo " +
                "INNER JOIN ejemplares e ON e.id_ejemplar = dp.id_ejemplar " +
                "INNER JOIN libros l ON l.id_libro = e.id_libro " +
                "WHERE p.fecha_prestamo >= ? AND p.fecha_prestamo < ? " +
                "GROUP BY l.id_libro, l.titulo, l.autor ORDER BY total DESC, l.titulo LIMIT 5";
        return jdbc.queryForList(sql, ts(desde), ts(hastaExcl));
    }
}
