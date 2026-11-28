package sissepbook.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import sissepbook.backend.exception.Errores;
import sissepbook.backend.exception.NegocioException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Punto único de conexión a la base de datos (local o Azure). No hay servidor ni API:
 * el frontend llama directo a los servicios que se crean aquí cuando hay conexión.
 *
 * Uso: BaseDatos.conectar(config) -> BaseDatos.servicios().libros().listar(...)
 */
public final class BaseDatos {

    /** Resultado de probar una conexión. */
    public record Estado(boolean baseExiste, boolean inicializada) {
    }

    private static HikariDataSource origen;
    private static Servicios servicios;

    private BaseDatos() {
    }

    public static synchronized boolean estaConectado() {
        return servicios != null && origen != null && !origen.isClosed();
    }

    /** Servicios listos para usar. Falla con un mensaje claro si aún no hay conexión. */
    public static synchronized Servicios servicios() {
        if (!estaConectado()) {
            throw new NegocioException("No hay conexión con la base de datos. Configúrala en la pantalla de conexión.");
        }
        return servicios;
    }

    /** Abre (o reabre) el pool de conexiones con la configuración dada. */
    public static synchronized void conectar(ConfiguracionConexion cfg) {
        cfg.validar();
        cerrar();
        HikariConfig hc = new HikariConfig();
        hc.setPoolName("sissebook");
        hc.setJdbcUrl(cfg.jdbcUrl(true));
        hc.setUsername(cfg.getUsuario().trim());
        hc.setPassword(cfg.getContrasena() == null ? "" : cfg.getContrasena());
        hc.setMaximumPoolSize(4);
        hc.setMinimumIdle(1);
        hc.setConnectionTimeout(8000);
        try {
            origen = new HikariDataSource(hc);
            servicios = new Servicios(new JdbcTemplate(origen));
        } catch (RuntimeException e) {
            origen = null;
            servicios = null;
            throw new NegocioException("No se pudo conectar: " + Errores.mensaje(e));
        }
    }

    public static synchronized void cerrar() {
        if (origen != null) {
            origen.close();
        }
        origen = null;
        servicios = null;
    }

    /**
     * Prueba el servidor (usuario/contraseña/red) sin tocar nada y dice si la base existe y
     * si ya tiene las tablas del sistema.
     */
    public static Estado probar(ConfiguracionConexion cfg) {
        cfg.validar();
        try (Connection con = abrirSinBase(cfg)) {
            boolean existe = contar(con,
                    "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = ?",
                    cfg.getBaseDatos().trim()) > 0;
            boolean inicializada = existe && contar(con,
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = 'usuarios_sistema'",
                    cfg.getBaseDatos().trim()) > 0;
            return new Estado(existe, inicializada);
        } catch (SQLException e) {
            throw new NegocioException("No se pudo conectar al servidor: " + Errores.mensaje(e));
        }
    }

    /**
     * Crea la base y carga tablas, procedimientos, triggers y datos de prueba.
     * ATENCIÓN: 01_schema.sql hace DROP DATABASE IF EXISTS, así que borra la base anterior.
     */
    public static void inicializar(ConfiguracionConexion cfg) {
        cfg.validar();
        cerrar();
        try (Connection con = abrirSinBase(cfg)) {
            ScriptSql.ejecutarTodos(con, cfg.getBaseDatos().trim());
        } catch (SQLException e) {
            throw new NegocioException("No se pudo inicializar la base de datos: " + Errores.mensaje(e));
        }
        conectar(cfg);
    }

    private static Connection abrirSinBase(ConfiguracionConexion cfg) throws SQLException {
        Properties p = new Properties();
        p.setProperty("user", cfg.getUsuario().trim());
        p.setProperty("password", cfg.getContrasena() == null ? "" : cfg.getContrasena());
        DriverManager.setLoginTimeout(10);
        return DriverManager.getConnection(cfg.jdbcUrl(false), p);
    }

    private static int contar(Connection con, String sql, String parametro) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
