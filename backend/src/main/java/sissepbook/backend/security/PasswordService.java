package sissepbook.backend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Hashea contraseñas con BCrypt y verifica coincidencias.
 * NUNCA se guarda ni se imprime la contraseña en texto plano.
 */
public class PasswordService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /** Genera el hash BCrypt de una contraseña en claro. */
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /** Compara una contraseña en claro contra un hash BCrypt almacenado. */
    public boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        return encoder.matches(rawPassword, storedHash);
    }
}
