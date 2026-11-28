package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Tablas;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;

/** Libros atrasados: préstamos vencidos sin devolver. */
public class AtrasadosView extends Pagina {

    private final JLabel lblResumen = Comp.etiqueta(" ", Font.BOLD, 13, Tema.BANNER_TEXTO);
    private final Tabla<Map<String, Object>> tabla;

    public AtrasadosView() {
        super("Libros atrasados", "Da seguimiento a los préstamos que necesitan tu atención.");

        Boton actualizar = Comp.boton("Actualizar", false);
        actualizar.addActionListener(e -> cargar());
        JPanel banner = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBackground(Tema.BANNER);
        banner.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        banner.add(lblResumen, BorderLayout.WEST);
        banner.add(actualizar, BorderLayout.EAST);
        arriba(banner);

        tabla = new Tabla<>(Tablas.col("Préstamo", "id_prestamo"), Tablas.col("Matrícula", "matricula"),
                Tablas.col("Lector", "nombre_completo"), Tablas.col("Código de barras", "codigo_barras"),
                Tablas.colFecha("Vencimiento", "fecha_vencimiento"), Tablas.col("Días de retraso", "dias_retraso"));
        centro(tabla.componente());
        cargar();
    }

    private void cargar() {
        try {
            List<Map<String, Object>> filas = Datos.prestamosVencidos();
            tabla.setItems(filas);
            lblResumen.setText(filas.size() + " préstamos necesitan seguimiento");
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }
}
