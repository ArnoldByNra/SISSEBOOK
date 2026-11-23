package sissepbook.frontend.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
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
 * Controller de EjemplaresView.fxml.
 * Elige un libro, muestra sus ejemplares y permite alta / edición de estado. <b>No</b> contiene SQL.
 */
public class EjemplaresController {

    @FXML private ComboBox<Modelos.Libro> cbLibro;
    @FXML private TableView<Modelos.Ejemplar> tabla;
    @FXML private TableColumn<Modelos.Ejemplar, Integer> colId;
    @FXML private TableColumn<Modelos.Ejemplar, String> colCodigo;
    @FXML private TableColumn<Modelos.Ejemplar, String> colInterno;
    @FXML private TableColumn<Modelos.Ejemplar, String> colEstado;
    @FXML private TableColumn<Modelos.Ejemplar, String> colUbicacion;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEjemplar"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colInterno.setCellValueFactory(new PropertyValueFactory<>("codigoInterno"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacionEstante"));

        cbLibro.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Modelos.Libro item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.titulo + " (" + item.isbn + ")");
            }
        });
        cbLibro.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Modelos.Libro item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.titulo + " (" + item.isbn + ")");
            }
        });

        cargarLibros();
    }

    @FXML
    private void volver() {
        Navegador.ir("MainView");
    }

    private void cargarLibros() {
        try {
            List<Modelos.Libro> libros = api.getList("/api/libros",
                    new com.fasterxml.jackson.core.type.TypeReference<List<Modelos.Libro>>() { });
            cbLibro.setItems(FXCollections.observableArrayList(libros));
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void cargarEjemplares() {
        Modelos.Libro libro = cbLibro.getValue();
        if (libro == null) {
            return;
        }
        try {
            List<Modelos.Ejemplar> ejemplares = api.getList("/api/ejemplares/libro/" + libro.idLibro,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Modelos.Ejemplar>>() { });
            tabla.setItems(FXCollections.observableArrayList(ejemplares));
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void nuevo() {
        Modelos.Libro libro = cbLibro.getValue();
        if (libro == null) {
            Alertas.advertencia("Selecciona primero un libro.");
            return;
        }
        Modelos.Ejemplar ejemplar = FormularioEjemplar.mostrar(null, libro.idLibro);
        if (ejemplar == null) {
            return;
        }
        try {
            api.post("/api/ejemplares", ejemplar, Modelos.Ejemplar.class);
            cargarEjemplares();
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void editar() {
        Modelos.Ejemplar sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alertas.advertencia("Selecciona un ejemplar para editar.");
            return;
        }
        Modelos.Ejemplar editado = FormularioEjemplar.mostrar(sel, sel.idLibro);
        if (editado == null) {
            return;
        }
        try {
            api.put("/api/ejemplares/" + sel.idEjemplar, editado, Modelos.Ejemplar.class);
            cargarEjemplares();
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }
}
