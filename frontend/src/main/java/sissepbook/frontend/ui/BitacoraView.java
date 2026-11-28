package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Tablas;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.util.List;
import java.util.function.Function;

/** Bitácora de auditoría con filtros y detalle anterior/nuevo (solo administrador). */
public class BitacoraView extends Pagina {

    private final JComboBox<String> cbTabla = new JComboBox<>(
            new String[]{"", "libros", "ejemplares", "prestamos", "usuarios_sistema"});
    private final JTextField campoUsuario = new JTextField();
    private final JTextArea txtAnterior = new JTextArea(3, 40);
    private final JTextArea txtNuevo = new JTextArea(3, 40);
    private final Tabla<Modelos.Bitacora> tabla;

    public BitacoraView() {
        super("Bitácora de auditoría", "Cambios registrados automáticamente por los triggers de la base de datos.");
        campoUsuario.setToolTipText("usuario_responsable");
        campoUsuario.setPreferredSize(new Dimension(200, 32));
        campoUsuario.addActionListener(e -> filtrar());
        Boton filtrar = Comp.boton("Filtrar", true);
        filtrar.addActionListener(e -> filtrar());
        arriba(Comp.fila(10, new JLabel("Tabla:"), cbTabla, new JLabel("Usuario:"), campoUsuario, filtrar));

        tabla = new Tabla<>(
                c("ID", b -> b.idAuditoria), c("Fecha", b -> Tablas.fecha(b.fechaHora)), c("Tabla", b -> b.tablaAfectada),
                c("Operación", b -> b.tipoOperacion), c("Registro", b -> b.idRegistro),
                c("Usuario", b -> b.usuarioResponsable));
        tabla.alSeleccionar(sel -> {
            txtAnterior.setText(sel != null && sel.informacionAnterior != null ? sel.informacionAnterior : "");
            txtNuevo.setText(sel != null && sel.informacionNueva != null ? sel.informacionNueva : "");
        });

        for (JTextArea t : new JTextArea[]{txtAnterior, txtNuevo}) {
            t.setEditable(false);
            t.setLineWrap(true);
            t.setWrapStyleWord(true);
        }
        Tarjeta detalle = Comp.tarjeta(12);
        JLabel l1 = new JLabel("Información anterior:");
        JLabel l2 = new JLabel("Información nueva:");
        JScrollPane s1 = new JScrollPane(txtAnterior);
        JScrollPane s2 = new JScrollPane(txtNuevo);
        for (javax.swing.JComponent c : new javax.swing.JComponent[]{l1, s1, l2, s2}) {
            c.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        }
        detalle.add(l1);
        detalle.add(s1);
        detalle.add(javax.swing.Box.createVerticalStrut(6));
        detalle.add(l2);
        detalle.add(s2);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabla.componente(), detalle);
        split.setResizeWeight(0.62);
        split.setBorder(null);
        split.setOpaque(false);
        centro(split);
        filtrar();
    }

    private static Tablas.Col<Modelos.Bitacora> c(String t, Function<Modelos.Bitacora, Object> f) {
        return Tablas.colDe(t, f);
    }

    private void filtrar() {
        try {
            String tablaSel = (String) cbTabla.getSelectedItem();
            String usuario = campoUsuario.getText();
            List<Modelos.Bitacora> filas = Datos.bitacora(
                    tablaSel != null && !tablaSel.isBlank() ? tablaSel : null,
                    usuario != null && !usuario.isBlank() ? usuario.trim() : null, 300);
            tabla.setItems(filas);
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }
}
