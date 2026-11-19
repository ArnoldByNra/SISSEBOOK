package sissepbook.frontend.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/** Pantalla 3. Préstamo con lector de código de barras (el lector escribe el código y manda ENTER). */
public class PrestamoController {
    public record Fila(String codigo, String titulo, String estado) {}

    @FXML private TextField txtMatricula;
    @FXML private Label lblLector;
    @FXML private TextField txtCodigo;
    @FXML private TableView<Fila> tblEjemplares;
    @FXML private TableColumn<Fila, String> colCodigo;
    @FXML private TableColumn<Fila, String> colTitulo;
    @FXML private TableColumn<Fila, String> colEstado;
    @FXML private Spinner<Integer> spnDias;
    @FXML private Label lblVence;

    private final ObservableList<Fila> filas = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().codigo()));
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().titulo()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().estado()));
        tblEjemplares.setItems(filas);
        spnDias.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 7));
        txtMatricula.requestFocus();
    }

    @FXML
    private void onBuscarLector() {
        // TODO: GET /api/lectores/matricula/{m} -> mostrar nombre/estado en lblLector; pasar el foco a txtCodigo.
        txtCodigo.requestFocus();
    }

    @FXML
    private void onEscanear() {
        String codigo = txtCodigo.getText().trim();
        txtCodigo.clear();
        if (codigo.isEmpty()) return;
        // TODO: GET /api/ejemplares/codigo/{codigo}; si está DISPONIBLE agregar a la lista, si no, Alertas.error(...).
        filas.add(new Fila(codigo, "(título)", "(estado)"));
        txtCodigo.requestFocus();
    }

    @FXML
    private void onQuitar() {
        Fila f = tblEjemplares.getSelectionModel().getSelectedItem();
        if (f != null) filas.remove(f);
    }

    @FXML
    private void onCancelar() {
        filas.clear();
        txtMatricula.clear();
        lblLector.setText("");
        txtMatricula.requestFocus();
    }

    @FXML
    private void onConfirmar() {
        // TODO: POST /api/prestamos {idLector, codigosBarras, dias}. El backend ejecuta sp_registrar_prestamo (transacción).
        // Éxito: mostrar fecha de vencimiento en lblVence y limpiar. Error: Alertas.error(mensaje del SP/trigger).
    }
}
