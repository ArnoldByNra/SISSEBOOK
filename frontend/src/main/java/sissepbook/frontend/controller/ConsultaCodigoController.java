package sissepbook.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Navegador;
import sissepbook.frontend.util.ScannerField;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller de ConsultaCodigoView.fxml.
 * ScannerField: al leer el código -> GET /api/ejemplares/codigo/{cb} y
 * GET /api/ejemplares/disponibilidad/{cb}; muestra libro, estado, ubicación y disponibles.
 * <b>No</b> contiene SQL.
 */
public class ConsultaCodigoController {

    @FXML private TextField campoCodigo;
    @FXML private GridPane panelDatos;
    @FXML private Label lblTitulo;
    @FXML private Label lblInterno;
    @FXML private Label lblEstado;
    @FXML private Label lblUbicacion;
    @FXML private Label lblDisponibles;
    @FXML private Label lblMensaje;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        ScannerField.vincular(campoCodigo, this::consultar);
    }

    @FXML
    private void volver() {
        Navegador.ir("MainView");
    }

    private void consultar(String codigo) {
        lblMensaje.setText("");
        try {
            String cb = URLEncoder.encode(codigo, StandardCharsets.UTF_8);
            Modelos.Ejemplar ejemplar = api.get("/api/ejemplares/codigo/" + cb, Modelos.Ejemplar.class);
            JsonNode disp = api.get("/api/ejemplares/disponibilidad/" + cb, JsonNode.class);

            lblTitulo.setText(ejemplar.tituloLibro);
            lblInterno.setText(ejemplar.codigoInterno);
            lblEstado.setText(ejemplar.estado);
            lblEstado.getStyleClass().setAll("disponible".equals(ejemplar.estado) ? "estado-ok" : "estado-alerta");
            lblUbicacion.setText(ejemplar.ubicacionEstante);
            lblDisponibles.setText(disp != null && disp.has("disponiblesTitulo")
                    ? disp.get("disponiblesTitulo").asText() : "-");

            panelDatos.setVisible(true);
            panelDatos.setManaged(true);
        } catch (ApiException e) {
            panelDatos.setVisible(false);
            panelDatos.setManaged(false);
            lblMensaje.setText(e.getMessage());
            lblMensaje.getStyleClass().setAll("estado-error");
        } finally {
            ScannerField.enfocar(campoCodigo);
        }
    }
}
