package sissepbook.frontend.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;

import java.util.List;

/**
 * Controller de UsuariosView.fxml. Solo ADMIN.
 * Tabla de usuarios, alta y edición (rol, activo). <b>No</b> contiene SQL.
 */
public class UsuariosController {

    @FXML private TableView<Modelos.Usuario> tabla;
    @FXML private TableColumn<Modelos.Usuario, Integer> colId;
    @FXML private TableColumn<Modelos.Usuario, String> colNombre;
    @FXML private TableColumn<Modelos.Usuario, String> colUsuario;
    @FXML private TableColumn<Modelos.Usuario, String> colRol;
    @FXML private TableColumn<Modelos.Usuario, String> colActivo;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("nombreRol"));
        colActivo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().activo ? "Sí" : "No"));
        cargar();
    }

    @FXML
    private void volver() {
        Navegador.ir("MainView");
    }

    private void cargar() {
        try {
            List<Modelos.Usuario> usuarios = api.getList("/api/usuarios",
                    new com.fasterxml.jackson.core.type.TypeReference<List<Modelos.Usuario>>() { });
            tabla.setItems(FXCollections.observableArrayList(usuarios));
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void nuevo() {
        Modelos.Usuario usuario = FormularioUsuario.mostrar(null, api);
        if (usuario == null) {
            return;
        }
        try {
            api.post("/api/usuarios", usuario, Modelos.Usuario.class);
            cargar();
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void editar() {
        Modelos.Usuario sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alertas.advertencia("Selecciona un usuario para editar.");
            return;
        }
        sel.contrasena = null;
        Modelos.Usuario editado = FormularioUsuario.mostrar(sel, api);
        if (editado == null) {
            return;
        }
        try {
            api.put("/api/usuarios/" + sel.idUsuario, editado, Modelos.Usuario.class);
            cargar();
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }
}
