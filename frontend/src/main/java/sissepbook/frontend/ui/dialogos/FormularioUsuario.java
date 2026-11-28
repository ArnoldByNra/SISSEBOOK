package sissepbook.frontend.ui.dialogos;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Component;

/** Diálogo modal para crear/editar un usuario del sistema. Los roles se cargan desde la tabla roles. */
public final class FormularioUsuario {

    private FormularioUsuario() {
    }

    public static Modelos.Usuario mostrar(Modelos.Usuario original) {
        JTextField tfNombre = new JTextField(24);
        JTextField tfUsuario = new JTextField();
        JPasswordField tfPass = new JPasswordField();
        JComboBox<Modelos.Rol> cbRol = new JComboBox<>();
        JCheckBox chkActivo = new JCheckBox("Activo", true);

        try {
            for (Modelos.Rol r : Datos.roles()) {
                cbRol.addItem(r);
            }
        } catch (DatosException e) {
            // si falla la carga de roles el combo queda vacío
        }
        cbRol.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean sel, boolean foco) {
                super.getListCellRendererComponent(list, value, index, sel, foco);
                if (value instanceof Modelos.Rol r) {
                    setText(r.nombreRol);
                }
                return this;
            }
        });
        cbRol.setSelectedIndex(cbRol.getItemCount() > 0 ? 0 : -1);

        if (original != null) {
            tfNombre.setText(original.nombreCompleto);
            tfUsuario.setText(original.nombreUsuario);
            chkActivo.setSelected(original.activo);
            tfPass.setToolTipText("(dejar vacío para no cambiar)");
            for (int i = 0; i < cbRol.getItemCount(); i++) {
                Modelos.Rol r = cbRol.getItemAt(i);
                if (r.idRol != null && original.idRol != null && r.idRol.equals(String.valueOf(original.idRol))) {
                    cbRol.setSelectedIndex(i);
                }
            }
        }

        JPanel grid = Formulario.crear(
                new String[]{"Nombre completo:", "Nombre de usuario:",
                        original == null ? "Contraseña:" : "Nueva contraseña (vacío = no cambiar):",
                        "Rol:", "Estado:"},
                new JComponent[]{tfNombre, tfUsuario, tfPass, cbRol, chkActivo});
        if (!Alertas.formulario(original == null ? "Nuevo usuario" : "Editar usuario", grid)) {
            return null;
        }

        Modelos.Usuario usuario = new Modelos.Usuario();
        if (original != null) {
            usuario.idUsuario = original.idUsuario;
        }
        usuario.nombreCompleto = tfNombre.getText().trim();
        usuario.nombreUsuario = tfUsuario.getText().trim();
        String pass = new String(tfPass.getPassword());
        usuario.contrasena = pass.isBlank() ? null : pass;
        Modelos.Rol rol = (Modelos.Rol) cbRol.getSelectedItem();
        usuario.idRol = rol != null ? Integer.parseInt(rol.idRol) : null;
        usuario.activo = chkActivo.isSelected();
        return usuario;
    }
}
