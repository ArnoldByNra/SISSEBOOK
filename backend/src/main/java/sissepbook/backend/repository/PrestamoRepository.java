package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Llama a los stored procedures de préstamo/devolución (con parámetros IN y OUT)
 * dentro de una misma conexión. La transacción real vive en el SP
 * (START TRANSACTION / COMMIT / ROLLBACK). Única capa que llama a los SP.
 */
public class PrestamoRepository {

    private final JdbcTemplate jdbc;

    public PrestamoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * CALL sp_registrar_prestamo_codigo(codigo, idLector, idUsuario, dias, OUT idPrestamo)
     * Devuelve el id del préstamo generado.
     */
    public int registrarPrestamo(String codigoBarras, int idLector, int idUsuario, int diasPlazo, String usuarioApp) {
        return jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Integer>) con -> {
            try (var setUsuario = con.prepareStatement("SET @usuario_app = ?")) {
                setUsuario.setString(1, usuarioApp);
                setUsuario.execute();
            }
            try (var cs = con.prepareCall("{CALL sp_registrar_prestamo_codigo(?, ?, ?, ?, ?)}")) {
                cs.setString(1, codigoBarras);
                cs.setInt(2, idLector);
                cs.setInt(3, idUsuario);
                cs.setInt(4, diasPlazo);
                cs.registerOutParameter(5, java.sql.Types.INTEGER);
                cs.execute();
                return cs.getInt(5);
            }
        });
    }

    /** Resultado de registrar un préstamo con varios ejemplares. */
    public record PrestamoMultiple(int idPrestamo, int totalEjemplares, java.time.LocalDateTime vencimiento) {
    }

    /**
     * CALL sp_registrar_prestamo_multiple(codigosSeparadosPorComa, idLector, idUsuario, dias, OUT idPrestamo, OUT total)
     * Un solo préstamo con todos los ejemplares, en una transacción (todo o nada).
     */
    public PrestamoMultiple registrarPrestamoMultiple(java.util.List<String> codigosBarras, int idLector,
                                                      int idUsuario, int diasPlazo, String usuarioApp) {
        String codigos = String.join(",", codigosBarras);
        return jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<PrestamoMultiple>) con -> {
            try (var setUsuario = con.prepareStatement("SET @usuario_app = ?")) {
                setUsuario.setString(1, usuarioApp);
                setUsuario.execute();
            }
            int idPrestamo;
            int total;
            try (var cs = con.prepareCall("{CALL sp_registrar_prestamo_multiple(?, ?, ?, ?, ?, ?)}")) {
                cs.setString(1, codigos);
                cs.setInt(2, idLector);
                cs.setInt(3, idUsuario);
                cs.setInt(4, diasPlazo);
                cs.registerOutParameter(5, java.sql.Types.INTEGER);
                cs.registerOutParameter(6, java.sql.Types.INTEGER);
                cs.execute();
                idPrestamo = cs.getInt(5);
                total = cs.getInt(6);
            }
            java.time.LocalDateTime vencimiento = null;
            try (var ps = con.prepareStatement("SELECT fecha_vencimiento FROM prestamos WHERE id_prestamo = ?")) {
                ps.setInt(1, idPrestamo);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        vencimiento = rs.getTimestamp(1).toLocalDateTime();
                    }
                }
            }
            return new PrestamoMultiple(idPrestamo, total, vencimiento);
        });
    }

    /**
     * CALL sp_registrar_devolucion_codigo(codigo, idUsuario, OUT diasRetraso)
     * Devuelve los días de retraso.
     */
    public int registrarDevolucion(String codigoBarras, int idUsuario, String usuarioApp) {
        return jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Integer>) con -> {
            try (var setUsuario = con.prepareStatement("SET @usuario_app = ?")) {
                setUsuario.setString(1, usuarioApp);
                setUsuario.execute();
            }
            try (var cs = con.prepareCall("{CALL sp_registrar_devolucion_codigo(?, ?, ?)}")) {
                cs.setString(1, codigoBarras);
                cs.setInt(2, idUsuario);
                cs.registerOutParameter(3, java.sql.Types.INTEGER);
                cs.execute();
                return cs.getInt(3);
            }
        });
    }

    /** Fecha de vencimiento del préstamo recién creado. */
    public java.time.LocalDateTime fechaVencimiento(int idPrestamo) {
        return jdbc.queryForObject("SELECT fecha_vencimiento FROM prestamos WHERE id_prestamo = ?",
                (rs, i) -> rs.getTimestamp("fecha_vencimiento").toLocalDateTime(), idPrestamo);
    }

    /** Préstamos con al menos un ejemplar sin devolver y ya vencidos (usa fn_calcular_dias_retraso). */
    public List<Map<String, Object>> prestamosVencidos() {
        String sql = "SELECT p.id_prestamo, l.matricula, l.nombre_completo, e.codigo_barras, " +
                "p.fecha_vencimiento, fn_calcular_dias_retraso(p.fecha_vencimiento, NULL) AS dias_retraso " +
                "FROM prestamos p " +
                "INNER JOIN lectores l ON l.id_lector = p.id_lector " +
                "INNER JOIN detalle_prestamo dp ON dp.id_prestamo = p.id_prestamo " +
                "INNER JOIN ejemplares e ON e.id_ejemplar = dp.id_ejemplar " +
                "WHERE dp.fecha_devolucion_real IS NULL AND p.fecha_vencimiento < NOW() " +
                "ORDER BY dias_retraso DESC";
        return jdbc.queryForList(sql);
    }
}
