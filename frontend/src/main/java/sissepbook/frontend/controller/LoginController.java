package sissepbook.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.session.Sesion;
import sissepbook.frontend.util.Navegador;

/**
 * Controller de LoginView.fxml.
 * Lee usuario/contraseña, POST /api/auth/login, guarda Sesion y va al menú.
 * <b>No</b> contiene SQL.
 */
public class LoginController {

    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private Label lblError;
    @FXML private Label lblCargando;
    @FXML private javafx.scene.control.Button btnIngresar;

    private final ApiClient api = new ApiClient();

    @FXML
    private void ingresar() {
        lblError.setText("");
        String usuario = campoUsuario.getText();
        String contrasena = campoContrasena.getText();

        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            lblError.setText("Ingresa usuario y contraseña.");
            return;
        }

        btnIngresar.setDisable(true);
        lblCargando.setText("Validando...");
        try {
            Modelos.LoginResponse resp = api.post("/api/auth/login",
                    new Modelos.LoginRequest(usuario.trim(), contrasena), Modelos.LoginResponse.class);

            Sesion.iniciar(resp.token, resp.idUsuario, resp.nombreUsuario, resp.nombreCompleto, resp.rol);
            Navegador.ir("MainView");
        } catch (ApiException e) {
            lblError.setText(e.getMessage());
        } catch (Exception e) {
            lblError.setText("No se pudo iniciar sesión: " + e.getMessage());
        } finally {
            btnIngresar.setDisable(false);
            lblCargando.setText("");
        }
    }
}
