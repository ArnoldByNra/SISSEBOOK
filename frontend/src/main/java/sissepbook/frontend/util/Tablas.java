package sissepbook.frontend.util;

import java.util.Map;
import java.util.function.Function;

/** Utilidades para armar columnas de tablas (filas Map o modelos) y dar formato a valores. */
public final class Tablas {

    /** Columna: título + cómo sacar el texto de una fila. */
    public record Col<T>(String titulo, Function<T, Object> valor) {
    }

    private Tablas() {
    }

    public static String texto(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    public static int entero(Map<String, Object> fila, String clave) {
        Object v = fila.get(clave);
        if (v instanceof Number n) {
            return n.intValue();
        }
        try {
            return v == null ? 0 : Integer.parseInt(String.valueOf(v));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** "2026-11-20T10:30:00.000+00:00" -> "2026-11-20 10:30". */
    public static String fecha(Object v) {
        String s = texto(v).replace('T', ' ');
        return s.length() > 16 ? s.substring(0, 16) : s;
    }

    /** Columna para filas Map. */
    public static Col<Map<String, Object>> col(String titulo, String clave) {
        return new Col<>(titulo, fila -> fila.get(clave));
    }

    public static Col<Map<String, Object>> colFecha(String titulo, String clave) {
        return new Col<>(titulo, fila -> fecha(fila.get(clave)));
    }

    /** Columna para una tabla de modelos. */
    public static <T> Col<T> colDe(String titulo, Function<T, Object> f) {
        return new Col<>(titulo, f);
    }
}
