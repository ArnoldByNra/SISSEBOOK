package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import sissepbook.backend.dto.LectorDTO;

import java.sql.PreparedStatement;
import java.util.List;

/** SQL de lectores. Única capa que escribe SQL de lectores. */
@Repository
public class LectorRepository {

    private final JdbcTemplate jdbc;

    public LectorRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT_BASE =
            "SELECT id_lector, matricula, nombre_completo, carrera_o_area, correo, estado_lector, max_prestamos " +
            "FROM lectores ";

    public List<LectorDTO> buscar(String filtro) {
        if (filtro != null && !filtro.isBlank()) {
            String like = "%" + filtro.trim() + "%";
            return jdbc.query(SELECT_BASE + "WHERE matricula LIKE ? OR nombre_completo LIKE ? ORDER BY nombre_completo",
                    this::map, like, like);
        }
        return jdbc.query(SELECT_BASE + "ORDER BY nombre_completo", this::map);
    }

    public LectorDTO buscarPorId(int idLector) {
        List<LectorDTO> filas = jdbc.query(SELECT_BASE + "WHERE id_lector = ?", this::map, idLector);
        return filas.stream().findFirst().orElse(null);
    }

    public LectorDTO buscarPorMatricula(String matricula) {
        List<LectorDTO> filas = jdbc.query(SELECT_BASE + "WHERE matricula = ?", this::map, matricula);
        return filas.stream().findFirst().orElse(null);
    }

    public int insertar(LectorDTO dto) {
        String sql = "INSERT INTO lectores (matricula, nombre_completo, carrera_o_area, correo, estado_lector, max_prestamos) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getMatricula());
            ps.setString(2, dto.getNombreCompleto());
            ps.setString(3, dto.getCarreraOArea());
            ps.setString(4, dto.getCorreo());
            ps.setString(5, dto.getEstadoLector());
            ps.setInt(6, dto.getMaxPrestamos());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : 0;
    }

    public void actualizar(LectorDTO dto) {
        jdbc.update("UPDATE lectores SET matricula=?, nombre_completo=?, carrera_o_area=?, correo=?, " +
                        "estado_lector=?, max_prestamos=? WHERE id_lector=?",
                dto.getMatricula(), dto.getNombreCompleto(), dto.getCarreraOArea(), dto.getCorreo(),
                dto.getEstadoLector(), dto.getMaxPrestamos(), dto.getIdLector());
    }

    public boolean existeMatricula(String matricula, Integer idExcluir) {
        String sql = "SELECT COUNT(*) FROM lectores WHERE matricula = ?";
        Integer total = (idExcluir == null)
                ? jdbc.queryForObject(sql, Integer.class, matricula)
                : jdbc.queryForObject(sql + " AND id_lector <> ?", Integer.class, matricula, idExcluir);
        return total != null && total > 0;
    }

    private LectorDTO map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        LectorDTO dto = new LectorDTO();
        dto.setIdLector(rs.getInt("id_lector"));
        dto.setMatricula(rs.getString("matricula"));
        dto.setNombreCompleto(rs.getString("nombre_completo"));
        dto.setCarreraOArea(rs.getString("carrera_o_area"));
        dto.setCorreo(rs.getString("correo"));
        dto.setEstadoLector(rs.getString("estado_lector"));
        dto.setMaxPrestamos(rs.getInt("max_prestamos"));
        return dto;
    }
}
