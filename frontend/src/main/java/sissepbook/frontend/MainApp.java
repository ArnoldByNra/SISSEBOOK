package sissepbook.frontend;

import sissepbook.backend.config.BaseDatos;
import sissepbook.backend.config.ConfiguracionConexion;
import sissepbook.frontend.ui.Tema;
import sissepbook.frontend.util.Navegador;

import javax.swing.SwingUtilities;
import java.nio.file.Files;

/**
 * Arranca la aplicación de escritorio (Swing, incluido en el JDK). Si ya hay una conexión guardada se
 * conecta a la base de datos y muestra el login; si no (o si falla) muestra la pantalla de conexión.
 */
public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tema.instalar();
            Navegador.init();
            boolean conectado = false;
            if (Files.exists(ConfiguracionConexion.archivo())) {
                try {
                    BaseDatos.conectar(ConfiguracionConexion.cargar());
                    conectado = true;
                } catch (RuntimeException e) {
                    // sin conexión: se abre la pantalla para configurarla
                }
            }
            Navegador.ir(conectado ? "LoginView" : "ConexionView");
        });
    }
}
