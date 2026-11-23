package sissepbook.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.session.Permisos;
import sissepbook.frontend.session.Sesion;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;

/**
 * Controller de MainView.fxml.
 * Menú principal: botones a cada módulo, filtrados por Permisos; cerrar sesión.
 * <b>No</b> contiene SQL.
 */
public class MainController {

        @FXML private Label lblUsuario;
    @FXML private Button btnLibros;
    @FXML private Button btnEjemplares;
    @FXML private Button btnLectores;
    @FXML private Button btnPrestamo;
    @FXML private Button btnDevolucion;
    @FXML private Button btnUsuarios;
    @FXML private Button btnBitacora;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        lblUsuario.setText(Sesion.getNombreCompleto() != null
                ? Sesion.getNombreCompleto() + " · " + Sesion.getRol()
                : "Usuario");

        // Módulos de gestión: solo ADMIN y OPERADOR
        boolean gestion = Permisos.puedeGestionar();
        ocultar(btnEjemplares, !gestion);
        ocultar(btnLectores, !gestion);
        ocultar(btnPrestamo, !gestion);
        ocultar(btnDevolucion, !gestion);

        // Usuarios y Bitácora solo para ADMIN
        ocultar(btnUsuarios, !Permisos.puedeAdministrarUsuarios());
        ocultar(btnBitacora, !Permisos.puedeVerBitacora());

        // Libros: CONSULTA puede ver; ADMIN/OPERADOR editan (el backend lo controla)
        btnLibros.setManaged(true);
        btnLibros.setVisible(true);
    }

    private static void ocultar(Button boton, boolean ocultar) {
        boton.setVisible(!ocultar);
        boton.setManaged(!ocultar);
    }

    @FXML private void irLibros() { Navegador.ir("LibrosView"); }
    @FXML private void irEjemplares() { Navegador.ir("EjemplaresView"); }
    @FXML private void irLectores() { Navegador.ir("LectoresView"); }
    @FXML private void irPrestamo() { Navegador.ir("PrestamoView"); }
    @FXML private void irDevolucion() { Navegador.ir("DevolucionView"); }
    @FXML private void irConsulta() { Navegador.ir("ConsultaCodigoView"); }
    @FXML private void irReportes() { Navegador.ir("ReportesView"); }
    @FXML private void irUsuarios() { Navegador.ir("UsuariosView"); }
    @FXML private void irBitacora() { Navegador.ir("BitacoraView"); }

    @FXML
    private void cerrarSesion() {
        if (Alertas.confirmar("¿Deseas cerrar la sesión?")) {
            try {
                api.post("/api/auth/logout", null, Void.class);
            } catch (Exception ignored) {
                // aunque falle el logout remoto, limpiamos la sesión local
            }
            Sesion.cerrar();
            Navegador.ir("LoginView");
        }
    }
}
