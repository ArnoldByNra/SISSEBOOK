package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sissepbook.backend.dto.LibroDTO;

import java.util.List;

/** SQL de libros con JdbcTemplate. Única capa que escribe SQL de libros. */
@Repository
public class LibroRepository {

    private final JdbcTemplate jdbc;

    public LibroRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT_BASE =
            "SELECT l.id_libro, l.isbn, l.titulo, l.autor, l.editorial, l.id_categoria, " +
            "c.nombre_categoria, l.anio_publicacion, l.activo, " +
            "fn_contar_ejemplares_disponibles(l.id_libro) AS disponibles " +
            "FROM libros l INNER JOIN categorias c ON c.id_categoria = l.id_categoria ";

    public List<LibroDTO> buscar(String filtro) {
        String sql = SELECT_BASE;
        if (filtro != null && !filtro.isBlank()) {
            sql += "WHERE l.titulo LIKE ? OR l.autor LIKE ? OR l.isbn LIKE ? ";
            String like = "%" + filtro.trim() + "%";
            sql += "ORDER BY l.titulo";
            return jdbc.query(sql, this::map, like, like, like);
        }
        sql += "ORDER BY l.titulo";
        return jdbc.query(sql, this::map);
    }

    public LibroDTO buscarPorId(int idLibro) {
        List<LibroDTO> filas = jdbc.query(SELECT_BASE + "WHERE l.id_libro = ?", this::map, idLibro);
        return filas.stream().findFirst().orElse(null);
    }

        public int insertar(LibroDTO dto, String usuarioApp) {
        String sql = "INSERT INTO libros (isbn, titulo, autor, editorial, id_categoria, anio_publicacion, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return AuditoriaSql.insertarConLlave(jdbc, usuarioApp, sql,
                dto.getIsbn(), dto.getTitulo(), dto.getAutor(), dto.getEditorial(),
                dto.getIdCategoria(), dto.getAnioPublicacion(), dto.isActivo());
    }

    public void actualizar(LibroDTO dto, String usuarioApp) {
        AuditoriaSql.ejecutar(jdbc, usuarioApp,
                "UPDATE libros SET isbn=?, titulo=?, autor=?, editorial=?, id_categoria=?, " +
                        "anio_publicacion=?, activo=? WHERE id_libro=?",
                dto.getIsbn(), dto.getTitulo(), dto.getAutor(), dto.getEditorial(),
                dto.getIdCategoria(), dto.getAnioPublicacion(), dto.isActivo(), dto.getIdLibro());
    }

    public void eliminar(int idLibro, String usuarioApp) {
        AuditoriaSql.ejecutar(jdbc, usuarioApp, "DELETE FROM libros WHERE id_libro = ?", idLibro);
    }

    public boolean existeIsbn(String isbn, Integer idExcluir) {
        String sql = "SELECT COUNT(*) FROM libros WHERE isbn = ?";
        Integer total = (idExcluir == null)
                ? jdbc.queryForObject(sql, Integer.class, isbn)
                : jdbc.queryForObject(sql + " AND id_libro <> ?", Integer.class, isbn, idExcluir);
        return total != null && total > 0;
    }

    private LibroDTO map(java.sql.ResultSet rs, int i) throws java.sql.SQLException {
        LibroDTO dto = new LibroDTO();
        dto.setIdLibro(rs.getInt("id_libro"));
        dto.setIsbn(rs.getString("isbn"));
        dto.setTitulo(rs.getString("titulo"));
        dto.setAutor(rs.getString("autor"));
        dto.setEditorial(rs.getString("editorial"));
        dto.setIdCategoria(rs.getInt("id_categoria"));
        dto.setNombreCategoria(rs.getString("nombre_categoria"));
        dto.setAnioPublicacion(rs.getInt("anio_publicacion"));
        dto.setActivo(rs.getBoolean("activo"));
        dto.setEjemplaresDisponibles(rs.getInt("disponibles"));
        return dto;
    }
}
