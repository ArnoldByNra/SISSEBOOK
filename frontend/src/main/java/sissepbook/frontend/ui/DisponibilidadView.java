package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Tablas;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

/** Disponibilidad: catálogo con ejemplares totales, disponibles y prestados (JOIN + GROUP BY). */
public class DisponibilidadView extends Pagina {

    private final JTextField campoBusqueda = new JTextField();
    private final JLabel lblTotal = new JLabel("0");
    private final JLabel lblDisponibles = new JLabel("0");
    private final JLabel lblPrestados = new JLabel("0");
    private final JLabel lblTitulos = Comp.suave(" ");
    private final Tabla<Map<String, Object>> tabla;

    public DisponibilidadView() {
        super("Disponibilidad", "Encuentra el libro que buscas y consulta sus ejemplares.");

        arriba(Comp.columnas(16,
                Comp.stat("Total de ejemplares", lblTotal, null),
                Comp.stat("Disponibles", lblDisponibles, null),
                Comp.stat("En préstamo", lblPrestados, null)));

        campoBusqueda.setToolTipText("Buscar por título, autor o ISBN");
        campoBusqueda.addActionListener(e -> buscar());
        Boton btn = Comp.boton("Buscar", true);
        btn.addActionListener(e -> buscar());
        javax.swing.JPanel barra = new javax.swing.JPanel(new BorderLayout(10, 0));
        barra.setOpaque(false);
        barra.add(campoBusqueda, BorderLayout.CENTER);
        barra.add(Comp.fila(10, btn, lblTitulos), BorderLayout.EAST);
        arriba(barra);

        tabla = new Tabla<>(Tablas.col("Título", "titulo"), Tablas.col("Autor", "autor"),
                Tablas.col("Categoría", "categoria"), Tablas.col("Ejemplares", "total"),
                Tablas.col("Disponibles", "disponibles"), Tablas.col("Prestados", "prestados"),
                Tablas.<Map<String, Object>>colDe("Estado",
                        f -> Tablas.entero(f, "disponibles") > 0 ? "Disponible" : "Agotado"));
        centro(tabla.componente());
        buscar();
    }

    private void buscar() {
        try {
            String q = campoBusqueda.getText() == null ? "" : campoBusqueda.getText().trim();
            List<Map<String, Object>> filas = Datos.disponibilidad(q);
            tabla.setItems(filas);
            int total = 0, disp = 0, prest = 0;
            for (Map<String, Object> f : filas) {
                total += Tablas.entero(f, "total");
                disp += Tablas.entero(f, "disponibles");
                prest += Tablas.entero(f, "prestados");
            }
            lblTitulos.setText(filas.size() + " títulos");
            lblTotal.setText(String.valueOf(total));
            lblDisponibles.setText(String.valueOf(disp));
            lblPrestados.setText(String.valueOf(prest));
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }
}
