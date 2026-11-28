package sissepbook.frontend.ui.dialogos;

import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JPanel;

/** Diálogo modal para crear/editar un libro. Devuelve el libro capturado o null si se cancela. */
public final class FormularioLibro {

    private FormularioLibro() {
    }

    public static Modelos.Libro mostrar(Modelos.Libro original) {
        JTextField tfIsbn = new JTextField(24);
        JTextField tfTitulo = new JTextField();
        JTextField tfAutor = new JTextField();
        JTextField tfEditorial = new JTextField();
        JTextField tfCategoria = new JTextField();
        JTextField tfAnio = new JTextField();

        if (original != null) {
            tfIsbn.setText(original.isbn);
            tfTitulo.setText(original.titulo);
            tfAutor.setText(original.autor);
            tfEditorial.setText(original.editorial);
            tfCategoria.setText(original.idCategoria != null ? String.valueOf(original.idCategoria) : "");
            tfAnio.setText(original.anioPublicacion != null ? String.valueOf(original.anioPublicacion) : "");
        }

        JPanel grid = Formulario.crear(
                new String[]{"ISBN:", "Título:", "Autor:", "Editorial:", "ID Categoría:", "Año:"},
                new JComponent[]{tfIsbn, tfTitulo, tfAutor, tfEditorial, tfCategoria, tfAnio});
        if (!Alertas.formulario(original == null ? "Nuevo libro" : "Editar libro", grid)) {
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
        libro.idCategoria = Formulario.entero(tfCategoria.getText());
        libro.anioPublicacion = Formulario.entero(tfAnio.getText());
        return libro;
    }
}
