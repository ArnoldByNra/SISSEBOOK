package sissepbook.backend.config;

import sissepbook.backend.exception.NegocioException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Ejecuta los scripts de src/main/resources/db/*.sql.
 * Los scripts usan "DELIMITER //" (comando del cliente mysql que JDBC no entiende),
 * así que aquí se separan las sentencias respetando el delimitador vigente.
 */
final class ScriptSql {

    /** Orden en el que se ejecutan los scripts. */
    static final String[] SCRIPTS = {
            "01_schema.sql", "02_datos_base.sql", "03_functions.sql", "04_procedures.sql",
            "05_triggers_validacion.sql", "06_triggers_auditoria.sql", "07_datos_prueba.sql", "08_reportes.sql"
    };

    private ScriptSql() {
    }

    /** Ejecuta todos los scripts en orden sobre la conexión dada. */
    static void ejecutarTodos(Connection con, String nombreBd) {
        for (String nombre : SCRIPTS) {
            ejecutarRecurso(con, "/db/" + nombre, nombreBd);
        }
    }

    private static void ejecutarRecurso(Connection con, String recurso, String nombreBd) {
        String texto = leer(recurso);
        if (!"sissebook".equals(nombreBd)) {
            texto = texto.replace("sissebook", nombreBd);
        }
        for (String sql : dividir(texto)) {
            try (Statement st = con.createStatement()) {
                st.setEscapeProcessing(false);
                st.execute(sql);
            } catch (SQLException e) {
                String primera = sql.lines().findFirst().orElse(sql);
                throw new NegocioException("Falló " + recurso.substring(4) + " en «" + primera.trim() + "»: "
                        + e.getMessage());
            }
        }
    }

    private static String leer(String recurso) {
        try (InputStream in = ScriptSql.class.getResourceAsStream(recurso)) {
            if (in == null) {
                throw new NegocioException("No se encontró el script " + recurso + " dentro del programa.");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new NegocioException("No se pudo leer el script " + recurso + ": " + e.getMessage());
        }
    }

    /** Separa un script en sentencias, entendiendo las líneas "DELIMITER x" y los comentarios "--". */
    static List<String> dividir(String script) {
        List<String> sentencias = new ArrayList<>();
        String delimitador = ";";
        StringBuilder actual = new StringBuilder();
        for (String linea : script.split("\\R")) {
            String t = linea.trim();
            if (actual.length() == 0) {
                if (t.isEmpty() || t.startsWith("--")) {
                    continue;
                }
                if (t.regionMatches(true, 0, "DELIMITER ", 0, 10)) {
                    delimitador = t.substring(10).trim();
                    continue;
                }
            }
            actual.append(linea).append('\n');
            if (t.endsWith(delimitador)) {
                String sql = actual.toString().trim();
                sql = sql.substring(0, sql.length() - delimitador.length()).trim();
                if (!sql.isEmpty()) {
                    sentencias.add(sql);
                }
                actual.setLength(0);
            }
        }
        String resto = actual.toString().trim();
        if (!resto.isEmpty()) {
            sentencias.add(resto);
        }
        return sentencias;
    }
}
