package sissepbook.frontend.ui;

import sissepbook.frontend.util.Tablas;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Tabla de solo lectura con filas de tipo T (un modelo o un Map). Reemplaza al TableView de JavaFX. */
final class Tabla<T> {

    private final List<Tablas.Col<T>> columnas;
    private final List<T> filas = new ArrayList<>();
    private final JTable tabla;
    private final JScrollPane scroll;

    @SafeVarargs
    Tabla(Tablas.Col<T>... columnas) {
        this.columnas = List.of(columnas);
        tabla = new JTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return filas.size();
            }

            @Override
            public int getColumnCount() {
                return Tabla.this.columnas.size();
            }

            @Override
            public String getColumnName(int c) {
                return Tabla.this.columnas.get(c).titulo();
            }

            @Override
            public Object getValueAt(int r, int c) {
                return Tablas.texto(Tabla.this.columnas.get(c).valor().apply(filas.get(r)));
            }
        });
        tabla.setRowHeight(28);
        tabla.setFont(Tema.fuente(Font.PLAIN, 13));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(0xeef1f5));
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(new Color(0xd6e4f0));
        tabla.setSelectionForeground(Tema.TEXTO);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        DefaultTableCellRenderer celda = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, false, r, c);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return comp;
            }
        };
        tabla.setDefaultRenderer(Object.class, celda);

        JTableHeader cab = tabla.getTableHeader();
        cab.setReorderingAllowed(false);
        cab.setPreferredSize(new java.awt.Dimension(10, 32));
        cab.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int r, int c) {
                super.getTableCellRendererComponent(t, v, false, false, r, c);
                setBackground(Tema.ENCABEZADO_TABLA);
                setForeground(Tema.AZUL_BARRA);
                setFont(Tema.fuente(Font.BOLD, 13));
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        scroll = new JScrollPane(tabla);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        scroll.setBorder(javax.swing.BorderFactory.createLineBorder(Tema.BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
    }

    JScrollPane componente() {
        return scroll;
    }

    JTable tabla() {
        return tabla;
    }

    void setItems(List<T> nuevas) {
        filas.clear();
        filas.addAll(nuevas);
        ((AbstractTableModel) tabla.getModel()).fireTableDataChanged();
        ajustarColumnas();
    }

    /** Reparte el ancho según lo que mide el título y los primeros datos de cada columna. */
    private void ajustarColumnas() {
        for (int c = 0; c < columnas.size(); c++) {
            int largo = columnas.get(c).titulo().length();
            for (int r = 0; r < Math.min(filas.size(), 100); r++) {
                largo = Math.max(largo, Tablas.texto(columnas.get(c).valor().apply(filas.get(r))).length());
            }
            tabla.getColumnModel().getColumn(c).setPreferredWidth(Math.max(50, Math.min(380, largo * 8 + 24)));
        }
    }

    List<T> items() {
        return filas;
    }

    /** Fila seleccionada o null. */
    T seleccionado() {
        int r = tabla.getSelectedRow();
        return r < 0 ? null : filas.get(r);
    }

    void alSeleccionar(Consumer<T> accion) {
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                accion.accept(seleccionado());
            }
        });
    }

    void quitar(T fila) {
        filas.remove(fila);
        ((AbstractTableModel) tabla.getModel()).fireTableDataChanged();
    }

    void agregar(T fila) {
        filas.add(fila);
        ((AbstractTableModel) tabla.getModel()).fireTableDataChanged();
    }

    void limpiar() {
        setItems(List.of());
    }

    List<String> titulos() {
        return columnas.stream().map(Tablas.Col::titulo).toList();
    }

    String texto(int fila, int columna) {
        return Tablas.texto(columnas.get(columna).valor().apply(filas.get(fila)));
    }
}
