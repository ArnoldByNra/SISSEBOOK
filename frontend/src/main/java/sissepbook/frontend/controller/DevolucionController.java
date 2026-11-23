package sissepbook.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Navegador;
import sissepbook.frontend.util.ScannerField;

/**
 * Controller de DevolucionView.fxml.
 * ScannerField: al leer el código -> POST /api/devoluciones; muestra días de retraso.
 * <b>No</b> contiene SQL.
 */
public class DevolucionController {

    @FXML private TextField campoCodigo;
    @FXML private Label lblResultado;
    @FXML private Label lblDetalle;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        ScannerField.vincular(campoCodigo, this::devolver);
    }

    @FXML
    private void volver() {
        Navegador.ir("MainView");
    }

    private void devolver(String codigo) {
        lblResultado.setText("");
        lblDetalle.setText("");
        try {
            Modelos.DevolucionResponse resp = api.post("/api/devoluciones",
                    new Modelos.DevolucionRequest(codigo), Modelos.DevolucionResponse.class);

            lblResultado.setText(resp.mensaje);
            lblResultado.getStyleClass().setAll(resp.diasRetraso > 0 ? "estado-alerta" : "estado-ok");
            lblDetalle.setText("Código: " + resp.codigoBarras + " · Días de retraso: " + resp.diasRetraso);
        } catch (ApiException e) {
            lblResultado.setText("Error");
            lblResultado.getStyleClass().setAll("estado-error");
            lblDetalle.setText(e.getMessage());
        } finally {
            ScannerField.enfocar(campoCodigo);
        }
    }
}
