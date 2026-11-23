package sissepbook.frontend.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sissepbook.frontend.model.Modelos;

/**
 * Diálogo modal para crear/editar un libro.
 * Devuelve el libro capturado o null si se cancela.
 */
public final class FormularioLibro {

    private FormularioLibro() {
    }

    public static Modelos.Libro mostrar(Modelos.Libro original) {
        Dialog<Modelos.Libro> dialog = new Dialog<>();
        dialog.setTitle(original == null ? "Nuevo libro" : "Editar libro");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfIsbn = new TextField();
        TextField tfTitulo = new TextField();
        TextField tfAutor = new TextField();
        TextField tfEditorial = new TextField();
        TextField tfCategoria = new TextField();
        TextField tfAnio = new TextField();

        if (original != null) {
            tfIsbn.setText(original.isbn);
            tfTitulo.setText(original.titulo);
            tfAutor.setText(original.autor);
            tfEditorial.setText(original.editorial);
            tfCategoria.setText(original.idCategoria != null ? String.valueOf(original.idCategoria) : "");
            tfAnio.setText(original.anioPublicacion != null ? String.valueOf(original.anioPublicacion) : "");
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));
        int r = 0;
        grid.addRow(r++, new Label("ISBN:"), tfIsbn);
        grid.addRow(r++, new Label("Título:"), tfTitulo);
        grid.addRow(r++, new Label("Autor:"), tfAutor);
        grid.addRow(r++, new Label("Editorial:"), tfEditorial);
        grid.addRow(r++, new Label("ID Categoría:"), tfCategoria);
        grid.addRow(r, new Label("Año:"), tfAnio);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) {
                return null;
            }
            Modelos.Libro libro = new Modelos.Libro();
            if (original != null) {
                libro.idLibro = original.idLibro;
                libro.activo = original.activo;
            }
            libro.isbn = tfIsbn.getText().trim();
            libro.titulo = tfTitulo.getText().trim();
            libro.autor = tfAutor.getText().trim();
            libro.editorial = tfEditorial.getText().trim();
            try {
                libro.idCategoria = Integer.parseInt(tfCategoria.getText().trim());
            } catch (NumberFormatException e) {
                libro.idCategoria = null;
            }
            try {
                libro.anioPublicacion = Integer.parseInt(tfAnio.getText().trim());
            } catch (NumberFormatException e) {
                libro.anioPublicacion = null;
            }
            return libro;
        });

        return dialog.showAndWait().orElse(null);
    }
}