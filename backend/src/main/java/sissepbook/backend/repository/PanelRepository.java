package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/** SQL del dashboard, disponibilidad e historial de movimientos. */
public class PanelRepository {

    private final JdbcTemplate jdbc;

    public PanelRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> resumen() {
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM libros WHERE activo = 1) AS libros, " +
                "(SELECT COUNT(*) FROM ejemplares WHERE estado <> 'baja') AS ejemplares, " +
                "(SELECT COUNT(*) FROM ejemplares WHERE estado = 'disponible') AS disponibles, " +
                "(SELECT COUNT(*) FROM ejemplares WHERE estado = 'prestado') AS prestados, " +
                "(SELECT COUNT(*) FROM detalle_prestamo dp INNER JOIN prestamos p ON p.id_prestamo = dp.id_prestamo " +
                "  WHERE dp.fecha_devolucion_real IS NULL AND p.fecha_vencimiento < NOW()) AS atrasados, " +
                "(SELECT COUNT(*) FROM lectores WHERE estado_lector = 'activo') AS lectores";
        return jdbc.queryForMap(sql);
    }

    public List<Map<String, Object>> disponibilidad(String q) {
        String like = "%" + q + "%";
        String sql = "SELECT l.id_libro, l.titulo, l.autor, c.nombre_categoria AS categoria, " +
                "COUNT(e.id_ejemplar) AS total, " +
                "COALESCE(SUM(e.estado = 'disponible'), 0) AS disponibles, " +
                "COALESCE(SUM(e.estado = 'prestado'), 0) AS prestados " +
                "FROM libros l " +
                "INNER JOIN categorias c ON c.id_categoria = l.id_categoria " +
                "LEFT JOIN ejemplares e ON e.id_libro = l.id_libro AND e.estado <> 'baja' " +
                "WHERE l.activo = 1 AND (? = '' OR l.titulo LIKE ? OR l.autor LIKE ? OR l.isbn LIKE ?) " +
                "GROUP BY l.id_libro, l.titulo, l.autor, c.nombre_categoria " +
                "ORDER BY l.titulo";
        return jdbc.queryForList(sql, q, like, like, like);
    }

    public List<Map<String, Object>> historial(int limite) {
        String sql = "SELECT m.fecha_hora, m.tipo_movimiento, e.codigo_barras, li.titulo, " +
                "COALESCE(le.nombre_completo, '') AS lector, u.nombre_usuario AS usuario, m.descripcion " +
                "FROM movimientos_biblioteca m " +
                "INNER JOIN ejemplares e ON e.id_ejemplar = m.id_ejemplar " +
                "INNER JOIN libros li ON li.id_libro = e.id_libro " +
                "LEFT JOIN lectores le ON le.id_lector = m.id_lector " +
                "INNER JOIN usuarios_sistema u ON u.id_usuario = m.id_usuario " +
                "ORDER BY m.fecha_hora DESC, m.id_movimiento DESC LIMIT ?";
        return jdbc.queryForList(sql, limite);
    }
}
