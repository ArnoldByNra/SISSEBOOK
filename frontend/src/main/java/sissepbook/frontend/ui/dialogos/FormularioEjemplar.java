package sissepbook.frontend.ui.dialogos;

import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Diálogo modal para crear/editar un ejemplar.
 * Al crear se piden código de barras, código interno, estado y ubicación.
 * Al editar se permiten estado, ubicación y código interno (el código de barras no cambia).
 */
public final class FormularioEjemplar {

    private FormularioEjemplar() {
    }

    public static Modelos.Ejemplar mostrar(Modelos.Ejemplar original, Integer idLibro) {
        JTextField tfCodigo = new JTextField(24);
        JTextField tfInterno = new JTextField();
        // Valores válidos según el ENUM de ejemplares.estado
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"disponible", "prestado", "en_revision", "baja"});
        cbEstado.setSelectedItem("disponible");
        JTextField tfUbicacion = new JTextField("Estante A1");

        if (original != null) {
            tfCodigo.setText(original.codigoBarras);
            tfCodigo.setEditable(false); // el código de barras no se modifica
            tfInterno.setText(original.codigoInterno);
            cbEstado.setSelectedItem(original.estado);
            tfUbicacion.setText(original.ubicacionEstante);
        }

        JPanel grid = Formulario.crear(
                new String[]{"Código de barras:", "Código interno:", "Estado:", "Ubicación:"},
                new JComponent[]{tfCodigo, tfInterno, cbEstado, tfUbicacion});
        if (!Alertas.formulario(original == null ? "Nuevo ejemplar" : "Editar ejemplar", grid)) {
            return null;
        }

        Modelos.Ejemplar ejemplar = new Modelos.Ejemplar();
        ejemplar.idLibro = idLibro;
        if (original != null) {
            ejemplar.idEjemplar = original.idEjemplar;
        }
        ejemplar.codigoBarras = tfCodigo.getText().trim();
        ejemplar.codigoInterno = tfInterno.getText().trim();
        ejemplar.estado = (String) cbEstado.getSelectedItem();
        ejemplar.ubicacionEstante = tfUbicacion.getText().trim();
        return ejemplar;
    }
}
