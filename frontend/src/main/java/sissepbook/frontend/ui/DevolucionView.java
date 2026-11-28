package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.ScannerField;

import javax.swing.Box;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JPanel;

/** Devolución por código de barras. La transacción vive en sp_registrar_devolucion_codigo. */
public class DevolucionView extends Pagina {

    private final JTextField campoCodigo = new JTextField();
    private final Parrafo lblResultado = new Parrafo(560);
    private final Parrafo lblDetalle = new Parrafo(560);

    public DevolucionView() {
        super("Devoluciones", "Registra el regreso de cada libro a su estante.");
        javax.swing.JLabel ayuda = Comp.seccion("Escanea el código de barras del ejemplar a devolver");
        campoCodigo.setToolTipText("Escanea y presiona ENTER");
        campoCodigo.setPreferredSize(new Dimension(380, 34));
        campoCodigo.setMaximumSize(new Dimension(380, 34));
        lblResultado.setFont(Tema.fuente(Font.BOLD, 16));
        lblResultado.setTexto("");
        lblDetalle.setFont(Tema.fuente(Font.PLAIN, 13));
        lblDetalle.setTexto("");

        Tarjeta caja = Comp.tarjeta(24);
        for (Component c : new Component[]{ayuda, campoCodigo, lblResultado, lblDetalle}) {
            ((javax.swing.JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        caja.add(ayuda);
        caja.add(Box.createVerticalStrut(14));
        caja.add(campoCodigo);
        caja.add(Box.createVerticalStrut(14));
        caja.add(lblResultado);
        caja.add(Box.createVerticalStrut(8));
        caja.add(lblDetalle);
        caja.setPreferredSize(new Dimension(620, 240));
        JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        envoltura.setOpaque(false);
        envoltura.add(caja);
        centro(envoltura);
        ScannerField.vincular(campoCodigo, this::devolver);
    }

    private void devolver(String codigo) {
        lblResultado.setTexto("");
        lblDetalle.setTexto("");
        try {
            Modelos.DevolucionResponse resp = Datos.devolver(codigo);
            lblResultado.setTexto(resp.mensaje);
            lblResultado.setForeground(resp.diasRetraso > 0 ? Tema.ALERTA : Tema.OK);
            lblDetalle.setTexto("Código: " + resp.codigoBarras + " · Días de retraso: " + resp.diasRetraso);
        } catch (DatosException e) {
            lblResultado.setTexto("Error");
            lblResultado.setForeground(Tema.ERROR);
            lblDetalle.setTexto(e.getMessage());
        } finally {
            ScannerField.enfocar(campoCodigo);
        }
    }
}
