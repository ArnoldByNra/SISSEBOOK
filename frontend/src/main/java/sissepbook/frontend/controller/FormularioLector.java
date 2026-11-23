package sissepbook.frontend.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sissepbook.frontend.model.Modelos;

/**
 * Diálogo modal para crear/editar un lector.
 * Devuelve el lector capturado o null si se cancela.
 */
public final class FormularioLector {

    private FormularioLector() {
    }

    public static Modelos.Lector mostrar(Modelos.Lector original) {
        Dialog<Modelos.Lector> dialog = new Dialog<>();
        dialog.setTitle(original == null ? "Nuevo lector" : "Editar lector");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfMatricula = new TextField();
        TextField tfNombre = new TextField();
        TextField tfCarrera = new TextField();
        TextField tfCorreo = new TextField();
        ComboBox<String> cbEstado = new ComboBox<>();
        cbEstado.getItems().addAll("activo", "suspendido", "inactivo");
        cbEstado.setValue("activo");
        TextField tfMax = new TextField("3");

        if (original != null) {
            tfMatricula.setText(original.matricula);
            tfNombre.setText(original.nombreCompleto);
            tfCarrera.setText(original.carreraOArea);
            tfCorreo.setText(original.correo);
            cbEstado.setValue(original.estadoLector);
            tfMax.setText(String.valueOf(original.maxPrestamos));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));
        int r = 0;
        grid.addRow(r++, new Label("Matrícula:"), tfMatricula);
        grid.addRow(r++, new Label("Nombre completo:"), tfNombre);
        grid.addRow(r++, new Label("Carrera/Área:"), tfCarrera);
        grid.addRow(r++, new Label("Correo:"), tfCorreo);
        grid.addRow(r++, new Label("Estado:"), cbEstado);
        grid.addRow(r, new Label("Máx. préstamos:"), tfMax);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) {
                return null;
            }
            Modelos.Lector lector = new Modelos.Lector();
            if (original != null) {
                lector.idLector = original.idLector;
            }
            lector.matricula = tfMatricula.getText().trim();
            lector.nombreCompleto = tfNombre.getText().trim();
            lector.carreraOArea = tfCarrera.getText().trim();
            lector.correo = tfCorreo.getText().trim();
            lector.estadoLector = cbEstado.getValue();
            try {
                lector.maxPrestamos = Integer.parseInt(tfMax.getText().trim());
            } catch (NumberFormatException e) {
                lector.maxPrestamos = 3;
            }
            return lector;
        });

        return dialog.showAndWait().orElse(null);
    }
}