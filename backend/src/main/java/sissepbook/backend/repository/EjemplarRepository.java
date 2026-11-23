package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sissepbook.backend.dto.EjemplarDTO;

import java.util.List;

/** SQL de ejemplares. Única capa que escribe SQL de ejemplares. */
@Repository
public class EjemplarRepository {

    private final JdbcTemplate jdbc;

    public EjemplarRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT_BASE =
            "SELECT e.id_ejemplar, e.id_libro, l.titulo, e.codigo_barras, e.codigo_interno, " +
            "e.estado, e.ubicacion_estante " +
            "FROM ejemplares e INNER JOIN libros l ON l.id_libro = e.id_libro ";

    public List<EjemplarDTO> listarPorLibro(int idLibro) {
        return jdbc.query(SELECT_BASE + "WHERE e.id_libro = ? ORDER BY e.codigo_interno", this::map, idLibro);
    }

    public EjemplarDTO buscarPorId(int idEjemplar) {
        List<EjemplarDTO> filas = jdbc.query(SELECT_BASE + "WHERE e.id_ejemplar = ?", this::map, idEjemplar);
        return filas.stream().findFirst().orElse(null);
    }

    public EjemplarDTO buscarPorCodigoBarras(String codigoBarras) {
        List<EjemplarDTO> filas = jdbc.query(SELECT_BASE + "WHERE e.codigo_barras = ?", this::map, codigoBarras);
        return filas.stream().findFirst().orElse(null);
    }

        public int insertar(EjemplarDTO dto, String usuarioApp) {
        String sql = "INSERT INTO ejemplares (id_libro, codigo_barras, codigo_interno, estado, ubicacion_estante) " +
                "VALUES (?, ?, ?, ?, ?)";
        return AuditoriaSql.insertarConLlave(jdbc, usuarioApp, sql,
                dto.getIdLibro(), dto.getCodigoBarras(), dto.getCodigoInterno(),
                dto.getEstado() != null ? dto.getEstado() : "disponible",
                dto.getUbicacionEstante() != null ? dto.getUbicacionEstante() : "General");
    }

    public void actualizar(EjemplarDTO dto, String usuarioApp) {
        AuditoriaSql.ejecutar(jdbc, usuarioApp,
                "UPDATE ejemplares SET estado=?, ubicacion_estante=?, codigo_interno=? WHERE id_ejemplar=?",
                dto.getEstado(), dto.getUbicacionEstante(), dto.getCodigoInterno(), dto.getIdEjemplar());
    }

    public boolean existeCodigoBarras(String codigoBarras) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM ejemplares WHERE codigo_barras = ?", Integer.class, codigoBarras);
        return total != null && total > 0;
    }

    public boolean existeCodigoInterno(String codigoInterno) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM ejemplares WHERE codigo_interno = ?", Integer.class, codigoInterno);
        return total != null && total > 0;
    }

        /** Llama al SP sp_consultar_disponibilidad_codigo y devuelve [estado, disponibles]. */
    public String[] consultarDisponibilidad(String codigoBarras) {
        return jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<String[]>) con -> {
            try (var cs = con.prepareCall("{CALL sp_consultar_disponibilidad_codigo(?, ?, ?)}")) {
                cs.setString(1, codigoBarras);
                cs.registerOutParameter(2, java.sql.Types.VARCHAR);
                cs.registerOutParameter(3, java.sql.Types.INTEGER);
                cs.execute();
                return new String[]{cs.getString(2), String.valueOf(cs.getInt(3))};
            }
        });
    }

    private EjemplarDTO map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        EjemplarDTO dto = new EjemplarDTO();
        dto.setIdEjemplar(rs.getInt("id_ejemplar"));
        dto.setIdLibro(rs.getInt("id_libro"));
        dto.setTituloLibro(rs.getString("titulo"));
        dto.setCodigoBarras(rs.getString("codigo_barras"));
        dto.setCodigoInterno(rs.getString("codigo_interno"));
        dto.setEstado(rs.getString("estado"));
        dto.setUbicacionEstante(rs.getString("ubicacion_estante"));
        return dto;
    }
}
