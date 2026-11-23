package sissepbook.frontend.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;

import java.util.List;

/**
 * Diálogo modal para crear/editar un usuario del sistema.
 * Los roles se cargan desde /api/usuarios/roles.
 */
public final class FormularioUsuario {

    private FormularioUsuario() {
    }

    public static Modelos.Usuario mostrar(Modelos.Usuario original, ApiClient api) {
        Dialog<Modelos.Usuario> dialog = new Dialog<>();
        dialog.setTitle(original == null ? "Nuevo usuario" : "Editar usuario");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfNombre = new TextField();
        TextField tfUsuario = new TextField();
        PasswordField tfPass = new PasswordField();
        ComboBox<Modelos.Rol> cbRol = new ComboBox<>();
        CheckBox chkActivo = new CheckBox("Activo");
        chkActivo.setSelected(true);

        try {
            List<Modelos.Rol> roles = api.getList("/api/usuarios/roles",
                    new com.fasterxml.jackson.core.type.TypeReference<List<Modelos.Rol>>() { });
            cbRol.getItems().setAll(roles);
        } catch (ApiException e) {
            // si falla la carga de roles el combo queda vacío
        }
        cbRol.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Modelos.Rol item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.nombreRol);
            }
        });
        cbRol.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Modelos.Rol item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.nombreRol);
            }
        });
        if (cbRol.getItems().size() == 1) {
            cbRol.getSelectionModel().selectFirst();
        }

        if (original != null) {
            tfNombre.setText(original.nombreCompleto);
            tfUsuario.setText(original.nombreUsuario);
            chkActivo.setSelected(original.activo);
            tfPass.setPromptText("(dejar vacío para no cambiar)");
            for (Modelos.Rol r : cbRol.getItems()) {
                if (r.idRol != null && original.idRol != null
                        && r.idRol.equals(String.valueOf(original.idRol))) {
                    cbRol.setValue(r);
                }
            }
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));
        int r = 0;
        grid.addRow(r++, new Label("Nombre completo:"), tfNombre);
        grid.addRow(r++, new Label("Nombre de usuario:"), tfUsuario);
        grid.addRow(r++, new Label(original == null ? "Contraseña:" : "Nueva contraseña:"), tfPass);
        grid.addRow(r++, new Label("Rol:"), cbRol);
        grid.addRow(r, new Label("Estado:"), chkActivo);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) {
                return null;
            }
            Modelos.Usuario usuario = new Modelos.Usuario();
            if (original != null) {
                usuario.idUsuario = original.idUsuario;
            }
            usuario.nombreCompleto = tfNombre.getText().trim();
            usuario.nombreUsuario = tfUsuario.getText().trim();
            String pass = tfPass.getText();
            usuario.contrasena = (pass == null || pass.isBlank()) ? null : pass;
            Modelos.Rol rol = cbRol.getValue();
            usuario.idRol = rol != null ? Integer.parseInt(rol.idRol) : null;
            usuario.activo = chkActivo.isSelected();
            return usuario;
        });

        return dialog.showAndWait().orElse(null);
    }
}