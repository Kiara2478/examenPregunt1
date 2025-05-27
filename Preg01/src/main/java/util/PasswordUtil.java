package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author ANDREA
 */
public class PasswordUtil {
    
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Método de ejemplo para actualizar contraseñas en la base de datos
     */
    public static void main(String[] args) {
        // Ejemplo de uso
        String password = "1234";
        String hashed = hashPassword(password);
        
        System.out.println("Contraseña original: " + password);
        System.out.println("Hash generado: " + hashed);
        System.out.println("Verificación: " + verifyPassword(password, hashed));
    }
}
