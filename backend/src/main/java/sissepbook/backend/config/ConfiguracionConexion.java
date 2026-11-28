package sissepbook.backend.config;

import sissepbook.backend.exception.NegocioException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Datos para conectarse a la base de datos MySQL: una instalación local o un servicio de Azure
 * (Azure Database for MySQL). Se guardan en ~/.sissebook/conexion.properties.
 * La contraseña solo se guarda en disco si "recordarContrasena" está activo.
 */
public class ConfiguracionConexion {

    public enum Tipo { LOCAL, AZURE }

    private Tipo tipo = Tipo.LOCAL;
    private String host = "localhost";
    private int puerto = 3306;
    private String baseDatos = "sissebook";
    private String usuario = "root";
    private String contrasena = "";
    private boolean ssl = false;
    private boolean recordarContrasena = false;

    // ---------------------------------------------------------------- persistencia

    public static Path archivo() {
        return Paths.get(System.getProperty("user.home"), ".sissebook", "conexion.properties");
    }

    /** Lee la configuración guardada; si no existe devuelve los valores por defecto (MySQL local). */
    public static ConfiguracionConexion cargar() {
        ConfiguracionConexion c = new ConfiguracionConexion();
        Path f = archivo();
        if (!Files.exists(f)) {
            return c;
        }
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(f)) {
            p.load(in);
            c.tipo = "AZURE".equalsIgnoreCase(p.getProperty("tipo")) ? Tipo.AZURE : Tipo.LOCAL;
            c.host = p.getProperty("host", c.host);
            c.puerto = Integer.parseInt(p.getProperty("puerto", String.valueOf(c.puerto)).trim());
            c.baseDatos = p.getProperty("baseDatos", c.baseDatos);
            c.usuario = p.getProperty("usuario", c.usuario);
            c.contrasena = p.getProperty("contrasena", "");
            c.ssl = Boolean.parseBoolean(p.getProperty("ssl", "false"));
            c.recordarContrasena = Boolean.parseBoolean(p.getProperty("recordarContrasena", "false"));
        } catch (IOException | NumberFormatException e) {
            return new ConfiguracionConexion();
        }
        return c;
    }

    /** Guarda la configuración (sin la contraseña si no se pidió recordarla). */
    public void guardar() {
        Properties p = new Properties();
        p.setProperty("tipo", tipo.name());
        p.setProperty("host", host);
        p.setProperty("puerto", String.valueOf(puerto));
        p.setProperty("baseDatos", baseDatos);
        p.setProperty("usuario", usuario);
        p.setProperty("ssl", String.valueOf(ssl));
        p.setProperty("recordarContrasena", String.valueOf(recordarContrasena));
        if (recordarContrasena) {
            p.setProperty("contrasena", contrasena == null ? "" : contrasena);
        }
        try {
            Path f = archivo();
            Files.createDirectories(f.getParent());
            try (OutputStream out = Files.newOutputStream(f)) {
                p.store(out, "Conexion a la base de datos de SISSE Biblioteca");
            }
        } catch (IOException e) {
            throw new NegocioException("No se pudo guardar la configuración de conexión: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- validación y URL

    /** Revisa que los datos mínimos estén completos y sean seguros de usar en la URL. */
    public void validar() {
        if (host == null || host.isBlank()) {
            throw new NegocioException("Indica el servidor (host) de la base de datos.");
        }
        if (!host.trim().matches("[A-Za-z0-9._-]+")) {
            throw new NegocioException("El servidor solo puede tener letras, números, punto, guion y guion bajo.");
        }
        if (puerto < 1 || puerto > 65535) {
            throw new NegocioException("El puerto debe estar entre 1 y 65535.");
        }
        if (baseDatos == null || !baseDatos.trim().matches("[A-Za-z0-9_]{1,60}")) {
            throw new NegocioException("El nombre de la base de datos solo puede tener letras, números y guion bajo.");
        }
        if (usuario == null || usuario.isBlank()) {
            throw new NegocioException("Indica el usuario de la base de datos.");
        }
    }

    /** URL JDBC. Con conBaseDatos=false se conecta solo al servidor (para crear la base). */
    public String jdbcUrl(boolean conBaseDatos) {
        StringBuilder u = new StringBuilder("jdbc:mysql://")
                .append(host.trim()).append(':').append(puerto).append('/');
        if (conBaseDatos) {
            u.append(baseDatos.trim());
        }
        u.append("?serverTimezone=America/Mexico_City")
                .append("&characterEncoding=UTF-8")
                .append("&connectTimeout=8000")
                .append("&socketTimeout=30000");
        if (ssl) {
            u.append("&sslMode=REQUIRED");
        } else {
            u.append("&sslMode=DISABLED&allowPublicKeyRetrieval=true");
        }
        return u.toString();
    }

    // ---------------------------------------------------------------- getters / setters

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPuerto() { return puerto; }
    public void setPuerto(int puerto) { this.puerto = puerto; }

    public String getBaseDatos() { return baseDatos; }
    public void setBaseDatos(String baseDatos) { this.baseDatos = baseDatos; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public boolean isSsl() { return ssl; }
    public void setSsl(boolean ssl) { this.ssl = ssl; }

    public boolean isRecordarContrasena() { return recordarContrasena; }
    public void setRecordarContrasena(boolean recordarContrasena) { this.recordarContrasena = recordarContrasena; }
}
