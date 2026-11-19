package sissepbook.frontend.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/** Pantalla 4. CRUD de libros: tabla + formulario lateral. */
public class LibrosController {
    public record Fila(String isbn, String titulo, String autor, String disponibles) {}

    @FXML private TextField txtBuscar;
    @FXML private TableView<Fila> tblLibros;
    @FXML private TableColumn<Fila, String> colIsbn;
    @FXML private TableColumn<Fila, String> colTitulo;
    @FXML private TableColumn<Fila, String> colAutor;
    @FXML private TableColumn<Fila, String> colDisp;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtEditorial;
    @FXML private ComboBox<String> cmbCategoria;

    private final ObservableList<Fila> filas = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isbn()));
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().titulo()));
        colAutor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().autor()));
        colDisp.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().disponibles()));
        tblLibros.setItems(filas);
        tblLibros.getSelectionModel().selectedItemProperty().addListener((o, a, f) -> {
            if (f != null) { txtIsbn.setText(f.isbn()); txtTitulo.setText(f.titulo()); txtAutor.setText(f.autor()); }
        });
        onBuscar();
    }

    @FXML private void onBuscar() { /* TODO: GET /api/libros?q=txtBuscar -> llenar filas. */ }

    @FXML private void onNuevo() {
        tblLibros.getSelectionModel().clearSelection();
        txtIsbn.clear(); txtTitulo.clear(); txtAutor.clear(); txtEditorial.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        txtIsbn.requestFocus();
    }

    @FXML private void onGuardar() { /* TODO: POST (nuevo) o PUT (existente) /api/libros; recargar tabla. Solo ADMIN/OPERADOR. */ }

    @FXML private void onEliminar() { /* TODO: confirmar con Alertas.confirmar y DELETE /api/libros/{id}. Solo ADMIN. */ }
}
