package sissepbook.frontend.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.session.Permisos;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller de LibrosView.fxml.
 * Tabla de libros, búsqueda, alta/edición/baja. <b>No</b> contiene SQL.
 */
public class LibrosController {

    @FXML private TextField campoBusqueda;
    @FXML private TableView<Modelos.Libro> tabla;
    @FXML private TableColumn<Modelos.Libro, Integer> colId;
    @FXML private TableColumn<Modelos.Libro, String> colTitulo;
    @FXML private TableColumn<Modelos.Libro, String> colAutor;
    @FXML private TableColumn<Modelos.Libro, String> colEditorial;
    @FXML private TableColumn<Modelos.Libro, String> colIsbn;
    @FXML private TableColumn<Modelos.Libro, String> colCategoria;
    @FXML private TableColumn<Modelos.Libro, Integer> colAnio;
    @FXML private TableColumn<Modelos.Libro, Integer> colDisp;
        @FXML private TableColumn<Modelos.Libro, String> colActivo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnBaja;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        // El rol CONSULTA solo puede ver; se ocultan las acciones de escritura
        boolean gestion = Permisos.puedeGestionar();
        btnNuevo.setVisible(gestion);
        btnNuevo.setManaged(gestion);
        btnEditar.setVisible(gestion);
        btnEditar.setManaged(gestion);
        btnBaja.setVisible(gestion);
        btnBaja.setManaged(gestion);

        colId.setCellValueFactory(new PropertyValueFactory<>("idLibro"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colDisp.setCellValueFactory(new PropertyValueFactory<>("ejemplaresDisponibles"));
        colActivo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().activo ? "Sí" : "No"));
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
            String path = "/api/libros";
            if (q != null && !q.isBlank()) {
                path += "?q=" + URLEncoder.encode(q.trim(), StandardCharsets.UTF_8);
            }
            var list = api.getList(path, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<Modelos.Libro>>() { });
            tabla.setItems(FXCollections.observableArrayList(list));
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void nuevo() {
        Modelos.Libro libro = FormularioLibro.mostrar(null);
        if (libro == null) {
            return;
        }
        try {
            api.post("/api/libros", libro, Modelos.Libro.class);
            cargar();
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void editar() {
        Modelos.Libro sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alertas.advertencia("Selecciona un libro para editar.");
            return;
        }
        Modelos.Libro editado = FormularioLibro.mostrar(sel);
        if (editado == null) {
            return;
        }
        try {
            api.put("/api/libros/" + sel.idLibro, editado, Modelos.Libro.class);
            cargar();
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void desactivar() {
        Modelos.Libro sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alertas.advertencia("Selecciona un libro para dar de baja.");
            return;
        }
        if (!Alertas.confirmar("¿Dar de baja el libro \"" + sel.titulo + "\"?")) {
            return;
        }
        try {
            api.delete("/api/libros/" + sel.idLibro);
            cargar();
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }
}
