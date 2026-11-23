package sissepbook.frontend.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;
import sissepbook.frontend.util.ScannerField;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller de PrestamoView.fxml.
 * Selecciona lector; ScannerField: cada código escaneado agrega el ejemplar a la lista;
 * Confirmar -> POST /api/prestamos (transacción en BD). <b>No</b> contiene SQL.
 */
public class PrestamoController {

    @FXML private TextField campoMatricula;
    @FXML private TextField campoCodigo;
    @FXML private TextField campoDias;
    @FXML private Label lblLector;
    @FXML private Label lblMensaje;
    @FXML private TableView<Modelos.Ejemplar> tablaEscaneados;
    @FXML private TableColumn<Modelos.Ejemplar, String> colCodigo;
    @FXML private TableColumn<Modelos.Ejemplar, String> colTitulo;
    @FXML private TableColumn<Modelos.Ejemplar, String> colEstado;

    private final ApiClient api = new ApiClient();
    private final ObservableList<Modelos.Ejemplar> escaneados = FXCollections.observableArrayList();
    private Modelos.Lector lectorSeleccionado;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("tituloLibro"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaEscaneados.setItems(escaneados);

        // Lector USB: escribe código + ENTER
        ScannerField.vincular(campoCodigo, this::agregarEjemplar);
    }

    @FXML
    private void volver() {
        Navegador.ir("MainView");
    }

    @FXML
    private void buscarLector() {
        String matricula = campoMatricula.getText();
        if (matricula == null || matricula.isBlank()) {
            Alertas.advertencia("Ingresa la matrícula del lector.");
            return;
        }
        try {
            Modelos.Lector lector = api.get("/api/lectores/matricula/"
                    + java.net.URLEncoder.encode(matricula.trim(), java.nio.charset.StandardCharsets.UTF_8),
                    Modelos.Lector.class);
            lectorSeleccionado = lector;
            lblLector.setText(lector.nombreCompleto + " (" + lector.estadoLector + ", máx " + lector.maxPrestamos + ")");
            lblLector.getStyleClass().setAll("activo".equals(lector.estadoLector) ? "estado-ok" : "estado-error");
        } catch (ApiException e) {
            lectorSeleccionado = null;
            lblLector.setText("");
            Alertas.error(e.getMessage());
        }
    }

    private void agregarEjemplar(String codigo) {
        try {
            Modelos.Ejemplar ejemplar = api.get("/api/ejemplares/codigo/"
                    + java.net.URLEncoder.encode(codigo, java.nio.charset.StandardCharsets.UTF_8),
                    Modelos.Ejemplar.class);

            boolean yaEsta = escaneados.stream()
                    .anyMatch(e -> codigo.equals(e.codigoBarras));
            if (yaEsta) {
                lblMensaje.setText("El ejemplar " + codigo + " ya está en la lista.");
                return;
            }
            if (!"disponible".equals(ejemplar.estado)) {
                lblMensaje.setText("El ejemplar " + codigo + " no está disponible (" + ejemplar.estado + ").");
                return;
            }
            escaneados.add(ejemplar);
            lblMensaje.setText("Agregado: " + ejemplar.tituloLibro);
            ScannerField.enfocar(campoCodigo);
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
            ScannerField.enfocar(campoCodigo);
        }
    }

    @FXML
    private void quitar() {
        Modelos.Ejemplar sel = tablaEscaneados.getSelectionModel().getSelectedItem();
        if (sel != null) {
            escaneados.remove(sel);
        }
    }

    @FXML
    private void confirmar() {
        if (lectorSeleccionado == null) {
            Alertas.advertencia("Primero busca y selecciona un lector.");
            return;
        }
        if (escaneados.isEmpty()) {
            Alertas.advertencia("Escanea al menos un ejemplar.");
            return;
        }

        int dias;
        try {
            dias = Integer.parseInt(campoDias.getText().trim());
        } catch (NumberFormatException e) {
            dias = 14;
        }

        List<String> codigos = new ArrayList<>();
        for (Modelos.Ejemplar e : escaneados) {
            codigos.add(e.codigoBarras);
        }

        Modelos.PrestamoRequest req = new Modelos.PrestamoRequest();
        req.idLector = lectorSeleccionado.idLector;
        req.codigosBarras = codigos;
        req.diasPrestamo = dias;

        try {
            Modelos.PrestamoResponse resp = api.post("/api/prestamos", req, Modelos.PrestamoResponse.class);
            Alertas.info(resp.mensaje);
            escaneados.clear();
            campoMatricula.clear();
            lblLector.setText("");
            lectorSeleccionado = null;
            lblMensaje.setText("");
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }
}
