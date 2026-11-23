package sissepbook.frontend.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller de BitacoraView.fxml. Solo ADMIN.
 * Tabla de auditoría con filtros y detalle info anterior/nueva. <b>No</b> contiene SQL.
 */
public class BitacoraController {

    @FXML private ComboBox<String> cbTabla;
    @FXML private TextField campoUsuario;
    @FXML private TableView<Modelos.Bitacora> tabla;
    @FXML private TableColumn<Modelos.Bitacora, Long> colId;
    @FXML private TableColumn<Modelos.Bitacora, String> colFecha;
    @FXML private TableColumn<Modelos.Bitacora, String> colTabla;
    @FXML private TableColumn<Modelos.Bitacora, String> colOperacion;
    @FXML private TableColumn<Modelos.Bitacora, String> colRegistro;
    @FXML private TableColumn<Modelos.Bitacora, String> colUsuario;
    @FXML private TextArea txtAnterior;
    @FXML private TextArea txtNuevo;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        cbTabla.getItems().addAll("", "libros", "ejemplares", "prestamos", "usuarios_sistema");
        cbTabla.getSelectionModel().selectFirst();

        colId.setCellValueFactory(new PropertyValueFactory<>("idAuditoria"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colTabla.setCellValueFactory(new PropertyValueFactory<>("tablaAfectada"));
        colOperacion.setCellValueFactory(new PropertyValueFactory<>("tipoOperacion"));
        colRegistro.setCellValueFactory(new PropertyValueFactory<>("idRegistro"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuarioResponsable"));

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            txtAnterior.setText(sel != null && sel.informacionAnterior != null ? sel.informacionAnterior : "");
            txtNuevo.setText(sel != null && sel.informacionNueva != null ? sel.informacionNueva : "");
        });

        filtrar();
    }

    @FXML
    private void volver() {
        Navegador.ir("MainView");
    }

    @FXML
    private void filtrar() {
        try {
            StringBuilder path = new StringBuilder("/api/bitacora?limite=300");
            String tablaSel = cbTabla.getValue();
            if (tablaSel != null && !tablaSel.isBlank()) {
                path.append("&tabla=").append(URLEncoder.encode(tablaSel, StandardCharsets.UTF_8));
            }
            String usuario = campoUsuario.getText();
            if (usuario != null && !usuario.isBlank()) {
                path.append("&usuario=").append(URLEncoder.encode(usuario.trim(), StandardCharsets.UTF_8));
            }
            List<Modelos.Bitacora> filas = api.getList(path.toString(),
                    new com.fasterxml.jackson.core.type.TypeReference<List<Modelos.Bitacora>>() { });
            tabla.setItems(FXCollections.observableArrayList(filas));
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }
}
