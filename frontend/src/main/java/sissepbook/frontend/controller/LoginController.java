
package sissepbook.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sissepbook.frontend.util.Navegador;

public class LoginController {
    @FXML private TextField txtUsuario;

    @FXML private PasswordField txtPassword;

    @FXML private Label lblError;

    @FXML private void onLogin() {

        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario == null || usuario.isBlank()
                || password == null || password.isBlank()) {

            lblError.setText("Ingresa tu usuario y contraseña.");
            lblError.setVisible(true);
            lblError.setManaged(true);

            return;
        }

        lblError.setText("Datos ingresados correctamente.");
        lblError.setVisible(true);
        lblError.setManaged(true);
        Navegador.ir("MainView");
    }
}
