package sissepbook.backend.exception;

/**
 * QUÉ SE VA A HACER AQUÍ:
 * - @RestControllerAdvice: convierte excepciones en JSON {codigo, mensaje}.
 * - NegocioException -> 400, no autenticado -> 401, sin permiso -> 403, SQLException de SIGNAL (triggers) -> 400 con el mensaje del trigger.
 */
public class GlobalExceptionHandler {
}
