package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;
import sissepbook.frontend.util.Tablas;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Reportes: filtros de periodo y tipo, resumen del periodo, préstamos por categoría,
 * libros favoritos y tabla del reporte elegido. "Generar reporte" guarda la tabla mostrada como CSV.
 */
public class ReportesView extends Pagina {

    private static final String T_ACTIVIDAD = "Actividad de la biblioteca";
    private static final String T_LIBROS = "Libros más solicitados";
    private static final String T_VENCIDOS = "Lectores con préstamos vencidos";
    private static final String T_INVENTARIO = "Inventario general";

    private final JSpinner dpDesde = selectorFecha(LocalDate.now().minusDays(29));
    private final JSpinner dpHasta = selectorFecha(LocalDate.now());
    private final JComboBox<String> cmbTipo = new JComboBox<>(
            new String[]{T_ACTIVIDAD, T_LIBROS, T_VENCIDOS, T_INVENTARIO});

    private final JLabel lblPrestamos = new JLabel("0");
    private final JLabel lblPrestamosSub = new JLabel(" ");
    private final JLabel lblDevoluciones = new JLabel("0");
    private final JLabel lblDevolucionesSub = new JLabel(" ");
    private final JLabel lblPuntuales = new JLabel("0%");
    private final JLabel lblPuntualesSub = new JLabel(" ");
    private final JPanel listaCategorias = new JPanel();
    private final JPanel listaTop = new JPanel();
    private final JLabel lblTituloTabla = Comp.seccion(" ");
    private final JPanel contenedorTabla = new JPanel(new BorderLayout(0, 8));
    private Tabla<Map<String, Object>> tabla;

    public ReportesView() {
        super("Reportes", "Convierte la actividad de tu biblioteca en mejores decisiones.");

        Boton aplicar = Comp.boton("Aplicar", true);
        aplicar.addActionListener(e -> aplicar());
        Boton generar = Comp.boton("Generar reporte (CSV)", false);
        generar.addActionListener(e -> exportarCsv());
        arriba(Comp.fila(10, new JLabel("Periodo:"), dpDesde, new JLabel("a"), dpHasta,
                new JLabel("Tipo de reporte:"), cmbTipo, aplicar));

        arriba(Comp.columnas(16,
                Comp.stat("Préstamos del periodo", lblPrestamos, lblPrestamosSub),
                Comp.stat("Devoluciones", lblDevoluciones, lblDevolucionesSub),
                Comp.stat("Devoluciones puntuales", lblPuntuales, lblPuntualesSub)));

        listaCategorias.setLayout(new javax.swing.BoxLayout(listaCategorias, javax.swing.BoxLayout.Y_AXIS));
        listaTop.setLayout(new javax.swing.BoxLayout(listaTop, javax.swing.BoxLayout.Y_AXIS));
        listaCategorias.setOpaque(false);
        listaTop.setOpaque(false);
        arriba(Comp.columnas(16,
                panel("¿Qué están leyendo?", "Préstamos por categoría", listaCategorias),
                panel("Los favoritos del periodo", "Libros con más préstamos", listaTop)));

        contenedorTabla.setOpaque(false);
        contenedorTabla.add(Comp.barra(lblTituloTabla, generar), BorderLayout.NORTH);
        centro(contenedorTabla);
        aplicar();
    }

    private static JSpinner selectorFecha(LocalDate inicial) {
        SpinnerDateModel modelo = new SpinnerDateModel(
                Date.from(inicial.atStartOfDay(ZoneId.systemDefault()).toInstant()), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner s = new JSpinner(modelo);
        s.setEditor(new JSpinner.DateEditor(s, "yyyy-MM-dd"));
        s.setPreferredSize(new Dimension(120, 32));
        return s;
    }

    private static LocalDate valor(JSpinner s) {
        return ((Date) s.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static Tarjeta panel(String titulo, String sub, JPanel lista) {
        Tarjeta p = Comp.tarjeta(18);
        JLabel t = Comp.seccion(titulo);
        JLabel s = Comp.suave(sub);
        for (Component c : new Component[]{t, s, lista}) {
            ((javax.swing.JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        p.add(t);
        p.add(s);
        p.add(Box.createVerticalStrut(8));
        p.add(lista);
        return p;
    }

    private void aplicar() {
        LocalDate desde = valor(dpDesde);
        LocalDate hasta = valor(dpHasta);
        if (desde.isAfter(hasta)) {
            Alertas.error("La fecha inicial no puede ser posterior a la final.");
            return;
        }
        cargarResumen(desde, hasta);
        cargarTabla(desde, hasta);
    }

    @SuppressWarnings("unchecked")
    private void cargarResumen(LocalDate desde, LocalDate hasta) {
        try {
            Map<String, Object> r = Datos.reportePeriodo(desde.toString(), hasta.toString());
            int pres = Tablas.entero(r, "prestamos");
            int ant = Tablas.entero(r, "prestamosAnterior");
            int dev = Tablas.entero(r, "devoluciones");
            int punt = Tablas.entero(r, "puntuales");
            lblPrestamos.setText(String.valueOf(pres));
            lblPrestamosSub.setText(ant == 0 ? "Sin datos del periodo anterior"
                    : String.format("%+.1f%% vs. periodo anterior", (pres - ant) * 100.0 / ant));
            lblDevoluciones.setText(String.valueOf(dev));
            lblDevolucionesSub.setText(pres == 0 ? " " : String.format("%.1f%% sobre préstamos", dev * 100.0 / pres));
            lblPuntuales.setText(dev == 0 ? "0%" : String.format("%.1f%%", punt * 100.0 / dev));
            lblPuntualesSub.setText(punt + " entregas a tiempo");

            listaCategorias.removeAll();
            List<Map<String, Object>> cats = (List<Map<String, Object>>) r.get("porCategoria");
            int max = cats.stream().mapToInt(c -> Tablas.entero(c, "total")).max().orElse(1);
            for (Map<String, Object> c : cats) {
                JLabel nombre = new JLabel(Tablas.texto(c.get("categoria")));
                nombre.setPreferredSize(new Dimension(150, 22));
                JProgressBar barra = new JProgressBar(0, Math.max(max, 1));
                barra.setValue(Tablas.entero(c, "total"));
                barra.setForeground(Tema.VERDE);
                barra.setBorderPainted(false);
                JPanel fila = new JPanel(new BorderLayout(10, 0));
                fila.setOpaque(false);
                fila.setAlignmentX(Component.LEFT_ALIGNMENT);
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
                fila.add(nombre, BorderLayout.WEST);
                fila.add(barra, BorderLayout.CENTER);
                fila.add(new JLabel(Tablas.texto(c.get("total"))), BorderLayout.EAST);
                listaCategorias.add(fila);
                listaCategorias.add(Box.createVerticalStrut(6));
            }
            if (cats.isEmpty()) {
                listaCategorias.add(new JLabel("Sin préstamos en el periodo."));
            }

            listaTop.removeAll();
            List<Map<String, Object>> top = (List<Map<String, Object>>) r.get("topLibros");
            int n = 1;
            for (Map<String, Object> t : top) {
                JLabel pos = Comp.suave(String.format("%02d", n++));
                JPanel fila = new JPanel(new BorderLayout(10, 0));
                fila.setOpaque(false);
                fila.setAlignmentX(Component.LEFT_ALIGNMENT);
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
                fila.add(pos, BorderLayout.WEST);
                fila.add(new JLabel(Tablas.texto(t.get("titulo"))), BorderLayout.CENTER);
                fila.add(new JLabel(Tablas.texto(t.get("total")) + " préstamos"), BorderLayout.EAST);
                listaTop.add(fila);
                listaTop.add(Box.createVerticalStrut(6));
            }
            if (top.isEmpty()) {
                listaTop.add(new JLabel("Sin préstamos en el periodo."));
            }
            revalidate();
            repaint();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarTabla(LocalDate desde, LocalDate hasta) {
        String tipo = (String) cmbTipo.getSelectedItem();
        Tabla<Map<String, Object>> nueva;
        Supplier<List<Map<String, Object>>> fuente;
        switch (tipo) {
            case T_LIBROS -> {
                fuente = () -> Datos.reporteLibrosMasSolicitados(50);
                nueva = new Tabla<>(Tablas.col("Título", "titulo"), Tablas.col("Autor", "autor"),
                        Tablas.col("Categoría", "categoria"), Tablas.col("Préstamos", "totalPrestamos"),
                        Tablas.col("Lectores distintos", "lectoresDistintos"),
                        Tablas.col("Disponibles", "ejemplaresDisponibles"));
            }
            case T_VENCIDOS -> {
                fuente = Datos::reporteLectoresVencidos;
                nueva = new Tabla<>(Tablas.col("Matrícula", "matricula"), Tablas.col("Lector", "nombreCompleto"),
                        Tablas.col("Correo", "correo"), Tablas.col("Pendientes", "prestamosPendientes"),
                        Tablas.col("Máx. días de retraso", "maxDiasRetraso"),
                        Tablas.col("Total días de retraso", "totalDiasRetraso"));
            }
            case T_INVENTARIO -> {
                fuente = () -> Datos.disponibilidad("");
                nueva = new Tabla<>(Tablas.col("Título", "titulo"), Tablas.col("Autor", "autor"),
                        Tablas.col("Categoría", "categoria"), Tablas.col("Ejemplares", "total"),
                        Tablas.col("Disponibles", "disponibles"), Tablas.col("Prestados", "prestados"));
            }
            default -> {
                fuente = () -> Datos.historial(1000);
                nueva = new Tabla<>(Tablas.colFecha("Fecha", "fecha_hora"), Tablas.col("Tipo", "tipo_movimiento"),
                        Tablas.col("Código", "codigo_barras"), Tablas.col("Libro", "titulo"),
                        Tablas.col("Lector", "lector"), Tablas.col("Registró", "usuario"));
            }
        }
        try {
            List<Map<String, Object>> filas = fuente.get();
            if (T_ACTIVIDAD.equals(tipo)) {
                String d = desde.toString();
                String h = hasta.toString();
                filas = filas.stream().filter(f -> {
                    String dia = Tablas.fecha(f.get("fecha_hora"));
                    dia = dia.length() >= 10 ? dia.substring(0, 10) : dia;
                    return dia.compareTo(d) >= 0 && dia.compareTo(h) <= 0;
                }).toList();
            }
            nueva.setItems(filas);
            lblTituloTabla.setText(tipo + " · " + filas.size() + " registros");
            if (tabla != null) {
                contenedorTabla.remove(tabla.componente());
            }
            contenedorTabla.add(nueva.componente(), BorderLayout.CENTER);
            tabla = nueva;
            contenedorTabla.revalidate();
            contenedorTabla.repaint();
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }

    /** Guarda en un CSV (UTF-8 con BOM, abre bien en Excel) la tabla que se está mostrando. */
    private void exportarCsv() {
        if (tabla == null || tabla.items().isEmpty()) {
            Alertas.error("No hay datos que exportar con los filtros actuales.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar reporte");
        fc.setSelectedFile(new File("reporte_" + ((String) cmbTipo.getSelectedItem()).toLowerCase().replace(' ', '_')
                .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                + "_" + valor(dpDesde) + "_" + valor(dpHasta) + ".csv"));
        fc.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
        if (fc.showSaveDialog(Navegador.frame()) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File destino = fc.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".csv")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".csv");
        }
        StringBuilder sb = new StringBuilder("\uFEFF");
        List<String> titulos = tabla.titulos();
        for (int i = 0; i < titulos.size(); i++) {
            sb.append(i == 0 ? "" : ",").append(csv(titulos.get(i)));
        }
        sb.append("\r\n");
        for (int r = 0; r < tabla.items().size(); r++) {
            for (int c = 0; c < titulos.size(); c++) {
                sb.append(c == 0 ? "" : ",").append(csv(tabla.texto(r, c)));
            }
            sb.append("\r\n");
        }
        try {
            Files.writeString(destino.toPath(), sb.toString(), StandardCharsets.UTF_8);
            Alertas.info("Reporte guardado en:\n" + destino.getAbsolutePath());
        } catch (IOException e) {
            Alertas.error("No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    private static String csv(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
