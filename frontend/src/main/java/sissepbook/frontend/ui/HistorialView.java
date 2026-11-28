package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Tablas;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.util.List;
import java.util.Map;

/** Historial de movimientos (movimientos_biblioteca). Filtro por tipo en el cliente. */
public class HistorialView extends Pagina {

    private final JComboBox<String> cmbTipo = new JComboBox<>(
            new String[]{"Todos", "prestamo", "devolucion", "alta_ejemplar", "baja_ejemplar", "ajuste"});
    private final JLabel lblConteo = Comp.suave(" ");
    private final Tabla<Map<String, Object>> tabla;
    private List<Map<String, Object>> todas = List.of();

    public HistorialView() {
        super("Historial de movimientos", "Cada movimiento de tu biblioteca, en un solo lugar.");

        cmbTipo.addActionListener(e -> filtrar());
        Boton actualizar = Comp.boton("Actualizar", false);
        actualizar.addActionListener(e -> cargar());
        arriba(Comp.barra(Comp.fila(10, new JLabel("Tipo:"), cmbTipo, actualizar), lblConteo));

        tabla = new Tabla<>(Tablas.colFecha("Fecha", "fecha_hora"), Tablas.col("Tipo", "tipo_movimiento"),
                Tablas.col("Código", "codigo_barras"), Tablas.col("Libro", "titulo"),
                Tablas.col("Lector", "lector"), Tablas.col("Registró", "usuario"),
                Tablas.col("Descripción", "descripcion"));
        centro(tabla.componente());
        cargar();
    }

    private void cargar() {
        try {
            todas = Datos.historial(300);
            filtrar();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void filtrar() {
        String tipo = (String) cmbTipo.getSelectedItem();
        List<Map<String, Object>> filas = todas.stream()
                .filter(f -> tipo == null || "Todos".equals(tipo)
                        || tipo.equals(Tablas.texto(f.get("tipo_movimiento"))))
                .toList();
        tabla.setItems(filas);
        lblConteo.setText(filas.size() + " movimientos");
    }
}
