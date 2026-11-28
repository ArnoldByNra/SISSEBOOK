package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import sissepbook.backend.dto.UsuarioDTO;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de usuarios_sistema + roles.
 * Única capa que escribe SQL. Establece @usuario_app antes de escribir para la auditoría.
 */
public class UsuarioRepository {

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Fila usada para el login (incluye el hash de la contraseña). */
    public record UsuarioLogin(int idUsuario, String nombreUsuario, String nombreCompleto,
                               String passwordHash, String rol, boolean activo) {
    }

    public Optional<UsuarioLogin> buscarParaLogin(String nombreUsuario) {
        String sql = "SELECT u.id_usuario, u.nombre_usuario, u.nombre_completo, u.password_hash, " +
                "r.nombre_rol, u.activo " +
                "FROM usuarios_sistema u INNER JOIN roles r ON r.id_rol = u.id_rol " +
                "WHERE u.nombre_usuario = ?";
        List<UsuarioLogin> filas = jdbc.query(sql, (rs, i) -> new UsuarioLogin(
                rs.getInt("id_usuario"),
                rs.getString("nombre_usuario"),
                rs.getString("nombre_completo"),
                rs.getString("password_hash"),
                rs.getString("nombre_rol"),
                rs.getBoolean("activo")
        ), nombreUsuario);
        return filas.stream().findFirst();
    }

    public List<UsuarioDTO> listar() {
        String sql = "SELECT u.id_usuario, u.nombre_completo, u.nombre_usuario, u.id_rol, r.nombre_rol, u.activo " +
                "FROM usuarios_sistema u INNER JOIN roles r ON r.id_rol = u.id_rol " +
                "ORDER BY u.id_usuario";
        return jdbc.query(sql, (rs, i) -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setIdUsuario(rs.getInt("id_usuario"));
            dto.setNombreCompleto(rs.getString("nombre_completo"));
            dto.setNombreUsuario(rs.getString("nombre_usuario"));
            dto.setIdRol(rs.getInt("id_rol"));
            dto.setNombreRol(rs.getString("nombre_rol"));
            dto.setActivo(rs.getBoolean("activo"));
            return dto;
        });
    }

    public List<String[]> listarRoles() {
        return jdbc.query("SELECT id_rol, nombre_rol FROM roles ORDER BY id_rol",
                (rs, i) -> new String[]{rs.getString("id_rol"), rs.getString("nombre_rol")});
    }

        /** Inserta un usuario con hash ya calculado. Registra @usuario_app para la auditoría. */
    public int insertar(UsuarioDTO dto, String passwordHash, String usuarioApp) {
        String sql = "INSERT INTO usuarios_sistema (nombre_completo, nombre_usuario, password_hash, id_rol, activo) " +
                "VALUES (?, ?, ?, ?, ?)";
        return AuditoriaSql.insertarConLlave(jdbc, usuarioApp, sql,
                dto.getNombreCompleto(), dto.getNombreUsuario(), passwordHash,
                dto.getIdRol(), dto.isActivo());
    }

    public void actualizar(UsuarioDTO dto, String passwordHash, String usuarioApp) {
        if (passwordHash != null) {
            AuditoriaSql.ejecutar(jdbc, usuarioApp,
                    "UPDATE usuarios_sistema SET nombre_completo=?, nombre_usuario=?, password_hash=?, " +
                            "id_rol=?, activo=? WHERE id_usuario=?",
                    dto.getNombreCompleto(), dto.getNombreUsuario(), passwordHash,
                    dto.getIdRol(), dto.isActivo(), dto.getIdUsuario());
        } else {
            AuditoriaSql.ejecutar(jdbc, usuarioApp,
                    "UPDATE usuarios_sistema SET nombre_completo=?, nombre_usuario=?, id_rol=?, activo=? " +
                            "WHERE id_usuario=?",
                    dto.getNombreCompleto(), dto.getNombreUsuario(),
                    dto.getIdRol(), dto.isActivo(), dto.getIdUsuario());
        }
    }

    public boolean existeNombreUsuario(String nombreUsuario, Integer idExcluir) {
        String sql = "SELECT COUNT(*) FROM usuarios_sistema WHERE nombre_usuario = ?";
        Integer total;
        if (idExcluir == null) {
            total = jdbc.queryForObject(sql, Integer.class, nombreUsuario);
        } else {
            total = jdbc.queryForObject(sql + " AND id_usuario <> ?", Integer.class, nombreUsuario, idExcluir);
        }
        return total != null && total > 0;
    }
}
