package sissepbook.backend.exception;

import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLRecoverableException;
import java.sql.SQLTransientConnectionException;

/**
 * Convierte cualquier excepción en un mensaje corto para mostrar al usuario.
 * - NegocioException: se muestra tal cual.
 * - SIGNAL de triggers / procedimientos (SQLSTATE 45000, código 1644): se muestra el texto que escribió la BD.
 * - Problemas de red o credenciales: mensaje de conexión.
 * - Cualquier otra cosa: mensaje genérico (el detalle técnico no se muestra al usuario).
 */
public final class Errores {

    private Errores() {
    }

    public static String mensaje(Throwable ex) {
        Throwable causa = ex;
        while (causa != null) {
            if (causa instanceof NegocioException) {
                return causa.getMessage();
            }
            if (causa instanceof SQLException sql) {
                if (sql.getErrorCode() == 1644 || "45000".equals(sql.getSQLState())) {
                    return sql.getMessage();
                }
                if (sql.getErrorCode() == 1045) {
                    return "Usuario o contraseña de la base de datos incorrectos.";
                }
                if (sql.getErrorCode() == 1044 || sql.getErrorCode() == 1142) {
                    return "El usuario de la base de datos no tiene permisos para esta operación (" + sql.getMessage() + ").";
                }
                if (sql.getErrorCode() == 1049) {
                    return "La base de datos no existe. Usa «Crear base de datos» en la pantalla de conexión.";
                }
                if (esDeConexion(sql)) {
                    return "No se pudo comunicar con el servidor de base de datos. Revisa el servidor, el puerto, "
                            + "tu red o el firewall de Azure.";
                }
                if (sql.getErrorCode() == 1062) {
                    return "Ya existe un registro con ese valor único (duplicado).";
                }
                if (sql.getErrorCode() == 1451) {
                    return "No se puede eliminar: el registro está relacionado con otros datos.";
                }
                return "Error de base de datos: " + sql.getMessage();
            }
            causa = causa.getCause();
        }
        return ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error inesperado.";
    }

    private static boolean esDeConexion(SQLException e) {
        String estado = e.getSQLState();
        return e instanceof SQLNonTransientConnectionException
                || e instanceof SQLTransientConnectionException
                || e instanceof SQLRecoverableException
                || (estado != null && estado.startsWith("08"));
    }
}
