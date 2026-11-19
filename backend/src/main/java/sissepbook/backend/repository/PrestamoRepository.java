package sissepbook.backend.repository;

/**
 * QUÉ SE VA A HACER AQUÍ:
 * - CALL sp_registrar_prestamo(?,?,?,?,?,?) y CALL sp_registrar_devolucion(?,?,?,?) con parámetros IN y OUT (CallableStatement.registerOutParameter).
 * - Antes de cada CALL: SET @usuario_app = ? para la auditoría.
 * - Todas las llamadas en la MISMA conexión.
 * - Única capa que escribe SQL / llama a SP.
 */
public class PrestamoRepository {
}
