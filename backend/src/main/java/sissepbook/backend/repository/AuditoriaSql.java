package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Ejecuta una escritura con la variable de sesión @usuario_app establecida en la MISMA
 * conexión, garantizando que los triggers de auditoría registren al usuario correcto.
 *
 * JdbcTemplate.update("SET @usuario_app = ?") y el UPDATE/INSERT posterior podrían ir en
 * conexiones distintas del pool; por eso ambas sentencias se ejecutan dentro de un único
 * ConnectionCallback.
 */
final class AuditoriaSql {

    private AuditoriaSql() {
    }

    /** Sentencia de escritura simple (UPDATE/DELETE/INSERT sin llave). */
    static int ejecutar(JdbcTemplate jdbc, String usuarioApp, String sql, Object... params) {
        return jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Integer>) con -> {
            setUsuario(con, usuarioApp);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
                return ps.executeUpdate();
            }
        });
    }

    /** INSERT que devuelve la llave autogenerada. */
    static int insertarConLlave(JdbcTemplate jdbc, String usuarioApp, String sql, Object... params) {
        return jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Integer>) con -> {
            setUsuario(con, usuarioApp);
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    return keys.next() ? keys.getInt(1) : 0;
                }
            }
        });
    }

    private static void setUsuario(Connection con, String usuarioApp) throws java.sql.SQLException {
        try (PreparedStatement ps = con.prepareStatement("SET @usuario_app = ?")) {
            ps.setString(1, usuarioApp);
            ps.execute();
        }
    }
}