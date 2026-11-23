package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sissepbook.backend.dto.ReporteLectorVencidoDTO;
import sissepbook.backend.dto.ReporteLibroSolicitadoDTO;

import java.util.List;

/** Ejecuta las consultas de database/08_reportes.sql (vistas). Única capa que escribe SQL de reportes. */
@Repository
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
}
