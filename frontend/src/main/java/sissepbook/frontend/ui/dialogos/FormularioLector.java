package sissepbook.frontend.ui.dialogos;

import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Diálogo modal para crear/editar un lector. Devuelve el lector capturado o null si se cancela. */
public final class FormularioLector {

    private FormularioLector() {
    }

    public static Modelos.Lector mostrar(Modelos.Lector original) {
        JTextField tfMatricula = new JTextField(24);
        JTextField tfNombre = new JTextField();
        JTextField tfCarrera = new JTextField();
        JTextField tfCorreo = new JTextField();
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"activo", "suspendido", "inactivo"});
        JTextField tfMax = new JTextField("3");

        if (original != null) {
            tfMatricula.setText(original.matricula);
            tfNombre.setText(original.nombreCompleto);
            tfCarrera.setText(original.carreraOArea);
            tfCorreo.setText(original.correo);
            cbEstado.setSelectedItem(original.estadoLector);
            tfMax.setText(String.valueOf(original.maxPrestamos));
        }

        JPanel grid = Formulario.crear(
                new String[]{"Matrícula:", "Nombre completo:", "Carrera/Área:", "Correo:", "Estado:", "Máx. préstamos:"},
                new JComponent[]{tfMatricula, tfNombre, tfCarrera, tfCorreo, cbEstado, tfMax});
        if (!Alertas.formulario(original == null ? "Nuevo lector" : "Editar lector", grid)) {
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
        lector.estadoLector = (String) cbEstado.getSelectedItem();
        Integer max = Formulario.entero(tfMax.getText());
        lector.maxPrestamos = max != null ? max : 3;
        return lector;
    }
}
