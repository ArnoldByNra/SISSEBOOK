package sissepbook.frontend.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import sissepbook.frontend.session.Permisos;
import sissepbook.frontend.session.Sesion;
import sissepbook.frontend.util.Navegador;

/** Pantalla 2. Menú lateral + área de contenido. Cada botón trae en userData el nombre de su vista FXML ("" = inicio). */
public class MainController {
    @FXML private StackPane contentArea;
    @FXML private GridPane inicioPane;
    @FXML private Label lblTitulo;
    @FXML private Label lblUsuario;
    @FXML private Button btnUsuarios;
    @FXML private Button btnBitacora;
    @FXML private Button cardUsuarios;
    @FXML private Button cardBitacora;

    @FXML
    private void initialize() {
        lblUsuario.setText(Sesion.nombre + " · " + Sesion.rol);
        // Ocultar módulos de administrador (solo UX).
        for (Button b : new Button[]{btnUsuarios, btnBitacora, cardUsuarios, cardBitacora}) {
            b.setVisible(Permisos.esAdmin());
            b.setManaged(Permisos.esAdmin());
        }
        // TODO: ocultar Ejemplares, Lectores, Préstamo, Devolución y Reportes si el rol es CONSULTA.
    }

    @FXML
    private void irModulo(ActionEvent e) {
        Button origen = (Button) e.getSource();
        String vista = (String) origen.getUserData();
        if (vista == null || vista.isEmpty()) {
            contentArea.getChildren().setAll(inicioPane);
            lblTitulo.setText("Menú principal");
            return;
        }
        Node nodo = Navegador.nodo(vista);
        contentArea.getChildren().setAll(nodo);
        lblTitulo.setText(origen.getText().split("\n")[0]);
    }

    @FXML
    private void onCerrarSesion() {
        Sesion.cerrar();
        Navegador.ir("LoginView");
    }
}
