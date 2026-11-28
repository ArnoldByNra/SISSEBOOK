package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.ScannerField;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Consulta de un ejemplar por código de barras. */
public class ConsultaCodigoView extends Pagina {

    private final JTextField campoCodigo = new JTextField();
    private final Tarjeta panelDatos = new Tarjeta(new GridBagLayout());
    private final JLabel lblTitulo = new JLabel();
    private final JLabel lblInterno = new JLabel();
    private final JLabel lblEstado = new JLabel();
    private final JLabel lblUbicacion = new JLabel();
    private final JLabel lblDisponibles = new JLabel();
    private final Parrafo lblMensaje = new Parrafo(620);

    public ConsultaCodigoView() {
        super("Consultar código", "Escanea o escribe el código de barras para ver el ejemplar.");
        campoCodigo.setToolTipText("Escanea o escribe el código y presiona ENTER");
        campoCodigo.setPreferredSize(new Dimension(420, 34));
        arriba(Comp.fila(0, campoCodigo));

        panelDatos.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 22, 20));
        String[] etiquetas = {"Título:", "Código interno:", "Estado:", "Ubicación:", "Disponibles del título:"};
        JLabel[] valores = {lblTitulo, lblInterno, lblEstado, lblUbicacion, lblDisponibles};
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 0, 5, 14);
        c.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < etiquetas.length; i++) {
            c.gridy = i;
            c.gridx = 0;
            panelDatos.add(Comp.etiqueta(etiquetas[i], Font.PLAIN, 13, Tema.TEXTO_SUAVE), c);
            c.gridx = 1;
            valores[i].setFont(Tema.fuente(Font.PLAIN, 13));
            panelDatos.add(valores[i], c);
        }
        panelDatos.setVisible(false);
        lblMensaje.setTexto("");

        JPanel contenido = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contenido.setOpaque(false);
        JPanel col = Comp.columna(12, panelDatos, lblMensaje);
        contenido.add(col);
        centro(contenido);
        ScannerField.vincular(campoCodigo, this::consultar);
    }

    private void consultar(String codigo) {
        lblMensaje.setTexto("");
        try {
            Modelos.Ejemplar ejemplar = Datos.ejemplarPorCodigo(codigo);
            int disponibles = Datos.disponiblesDelTitulo(codigo);
            lblTitulo.setText(ejemplar.tituloLibro);
            lblInterno.setText(ejemplar.codigoInterno);
            lblEstado.setText(ejemplar.estado);
            Comp.estiloEstado(lblEstado, "disponible".equals(ejemplar.estado) ? Tema.OK : Tema.ALERTA, true);
            lblUbicacion.setText(ejemplar.ubicacionEstante);
            lblDisponibles.setText(String.valueOf(disponibles));
            panelDatos.setVisible(true);
        } catch (DatosException e) {
            panelDatos.setVisible(false);
            lblMensaje.setForeground(Tema.ERROR);
            lblMensaje.setTexto(e.getMessage());
        } finally {
            ScannerField.enfocar(campoCodigo);
            revalidate();
            repaint();
        }
    }
}
