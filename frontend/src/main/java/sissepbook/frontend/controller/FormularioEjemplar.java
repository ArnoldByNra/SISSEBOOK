package sissepbook.frontend.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sissepbook.frontend.model.Modelos;

/**
 * Diálogo modal para crear/editar un ejemplar.
 * Al crear se piden código de barras, código interno, estado y ubicación.
 * Al editar se permiten estado, ubicación y código interno (el código de barras no cambia).
 */
public final class FormularioEjemplar {

    private FormularioEjemplar() {
    }

    public static Modelos.Ejemplar mostrar(Modelos.Ejemplar original, Integer idLibro) {
        Dialog<Modelos.Ejemplar> dialog = new Dialog<>();
        dialog.setTitle(original == null ? "Nuevo ejemplar" : "Editar ejemplar");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfCodigo = new TextField();
        TextField tfInterno = new TextField();
        ComboBox<String> cbEstado = new ComboBox<>();
        // Valores válidos según el ENUM de ejemplares.estado
        cbEstado.getItems().addAll("disponible", "prestado", "en_revision", "baja");
        cbEstado.setValue("disponible");
        TextField tfUbicacion = new TextField("Estante A1");

        if (original != null) {
            tfCodigo.setText(original.codigoBarras);
            tfCodigo.setEditable(false); // el código de barras no se modifica
            tfInterno.setText(original.codigoInterno);
            cbEstado.setValue(original.estado);
            tfUbicacion.setText(original.ubicacionEstante);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));
        int r = 0;
        grid.addRow(r++, new Label("Código de barras:"), tfCodigo);
        grid.addRow(r++, new Label("Código interno:"), tfInterno);
        grid.addRow(r++, new Label("Estado:"), cbEstado);
        grid.addRow(r, new Label("Ubicación:"), tfUbicacion);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) {
                return null;
            }
            Modelos.Ejemplar ejemplar = new Modelos.Ejemplar();
            ejemplar.idLibro = idLibro;
            if (original != null) {
                ejemplar.idEjemplar = original.idEjemplar;
            }
            ejemplar.codigoBarras = tfCodigo.getText().trim();
            ejemplar.codigoInterno = tfInterno.getText().trim();
            ejemplar.estado = cbEstado.getValue();
            ejemplar.ubicacionEstante = tfUbicacion.getText().trim();
            return ejemplar;
        });

        return dialog.showAndWait().orElse(null);
    }
}