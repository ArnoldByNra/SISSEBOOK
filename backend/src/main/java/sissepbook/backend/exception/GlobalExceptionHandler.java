package sissepbook.backend.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @RestControllerAdvice: convierte excepciones en JSON {codigo, mensaje}.
 * - NegocioException -> 400
 * - SQLException de SIGNAL (triggers / stored procedures) -> 400 con el mensaje del trigger
 * - Errores de validación (@Valid) -> 400
 * - Cualquier otro error -> 500
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarNegocio(NegocioException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> manejarDatos(DataAccessException ex) {
        String mensaje = extraerMensajeSql(ex);
        return construir(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getDefaultMessage())
                .orElse("Datos inválidos en la petición.");
        return construir(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarGeneral(Exception ex) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor.");
    }

    /** Busca el mensaje original del SIGNAL SQLSTATE '45000' que lanzan triggers/SP. */
    private String extraerMensajeSql(DataAccessException ex) {
        Throwable causa = ex;
        while (causa != null) {
            if (causa instanceof SQLException sqlEx) {
                if (sqlEx.getErrorCode() == 1644 || "45000".equals(sqlEx.getSQLState())) {
                    return sqlEx.getMessage();
                }
                return "Error de base de datos: " + sqlEx.getMessage();
            }
            causa = causa.getCause();
        }
        return "Error de base de datos.";
    }

    private ResponseEntity<Map<String, Object>> construir(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("codigo", status.value());
        cuerpo.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(cuerpo);
    }
}
