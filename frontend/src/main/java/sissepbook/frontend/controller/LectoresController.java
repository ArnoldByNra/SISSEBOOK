package sissepbook.frontend.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller de LectoresView.fxml.
 * Tabla de lectores, búsqueda, alta/edición y cambio de estado. <b>No</b> contiene SQL.
 */
public class LectoresController {

    @FXML private TextField campoBusqueda;
    @FXML private TableView<Modelos.Lector> tabla;
    @FXML private TableColumn<Modelos.Lector, Integer> colId;
    @FXML private TableColumn<Modelos.Lector, String> colMatricula;
    @FXML private TableColumn<Modelos.Lector, String> colNombre;
    @FXML private TableColumn<Modelos.Lector, String> colCarrera;
    @FXML private TableColumn<Modelos.Lector, String> colCorreo;
    @FXML private TableColumn<Modelos.Lector, String> colEstado;
    @FXML private TableColumn<Modelos.Lector, Integer> colMax;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idLector"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("carreraOArea"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoLector"));
        colMax.setCellValueFactory(new PropertyValueFactory<>("maxPrestamos"));
        cargar();
    }

    @FXML
    private void volver() {
        Navegador.ir("MainView");
    }

    @FXML
    private void buscar() {
        cargar();
    }

    private void cargar() {
        try {
            String q = campoBusqueda.getText();
            String path = "/api/lectores";
            if (q != null && !q.isBlank()) {
                path += "?q=" + URLEncoder.encode(q.trim(), StandardCharsets.UTF_8);
            }
            var list = api.getList(path, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<Modelos.Lector>>() { });
            tabla.setItems(FXCollections.observableArrayList(list));
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void nuevo() {
        Modelos.Lector lector = FormularioLector.mostrar(null);
        if (lector == null) {
            return;
        }
        try {
            api.post("/api/lectores", lector, Modelos.Lector.class);
            cargar();
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void editar() {
        Modelos.Lector sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alertas.advertencia("Selecciona un lector para editar.");
            return;
        }
        Modelos.Lector editado = FormularioLector.mostrar(sel);
        if (editado == null) {
            return;
        }
        try {
            api.put("/api/lectores/" + sel.idLector, editado, Modelos.Lector.class);
            cargar();
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }
}
