package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.ScannerField;
import sissepbook.frontend.util.Tablas;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Nuevo préstamo con lector de código de barras. La transacción vive en sp_registrar_prestamo_multiple. */
public class PrestamoView extends Pagina {

    private final JTextField campoMatricula = new JTextField();
    private final JTextField campoCodigo = new JTextField();
    private final JTextField campoDias = new JTextField("14");
    private final JLabel lblLector = new JLabel(" ");
    private final JLabel lblMensaje = new JLabel(" ");
    private final Tabla<Modelos.Ejemplar> tablaEscaneados;
    private Modelos.Lector lectorSeleccionado;

    public PrestamoView() {
        super("Préstamos", "Escanea los ejemplares y registra el préstamo en una sola operación.");
        campoMatricula.setToolTipText("Ej. MAT-2026-001");
        campoMatricula.setPreferredSize(new Dimension(200, 32));
        campoMatricula.addActionListener(e -> buscarLector());
        Boton buscar = Comp.boton("Buscar lector", false);
        buscar.addActionListener(e -> buscarLector());
        arriba(Comp.fila(10, new JLabel("Matrícula del lector:"), campoMatricula, buscar, lblLector));

        campoCodigo.setToolTipText("Escanea y presiona ENTER");
        campoCodigo.setPreferredSize(new Dimension(280, 32));
        campoDias.setPreferredSize(new Dimension(60, 32));
        arriba(Comp.fila(10, new JLabel("Escanear código de barras:"), campoCodigo,
                new JLabel("Plazo (días):"), campoDias));

        tablaEscaneados = new Tabla<>(
                c("Código de barras", x -> x.codigoBarras), c("Título", x -> x.tituloLibro), c("Estado", x -> x.estado));
        centro(tablaEscaneados.componente());

        Boton quitar = Comp.boton("Quitar seleccionado", false);
        quitar.addActionListener(e -> quitar());
        Boton confirmar = Comp.boton("Confirmar préstamo", true);
        confirmar.addActionListener(e -> confirmar());
        javax.swing.JPanel sur = new javax.swing.JPanel(new java.awt.BorderLayout(0, 8));
        sur.setOpaque(false);
        sur.add(Comp.barra(quitar, confirmar), java.awt.BorderLayout.NORTH);
        sur.add(lblMensaje, java.awt.BorderLayout.CENTER);
        add(sur, java.awt.BorderLayout.SOUTH);

        ScannerField.vincular(campoCodigo, this::agregarEjemplar);
    }

    private static Tablas.Col<Modelos.Ejemplar> c(String t, Function<Modelos.Ejemplar, Object> f) {
        return Tablas.colDe(t, f);
    }

    private void buscarLector() {
        String matricula = campoMatricula.getText();
        if (matricula == null || matricula.isBlank()) {
            Alertas.advertencia("Ingresa la matrícula del lector.");
            return;
        }
        try {
            Modelos.Lector lector = Datos.lectorPorMatricula(matricula.trim());
            lectorSeleccionado = lector;
            lblLector.setText(lector.nombreCompleto + " (" + lector.estadoLector + ", máx " + lector.maxPrestamos + ")");
            Comp.estiloEstado(lblLector, "activo".equals(lector.estadoLector) ? Tema.OK : Tema.ERROR, true);
            ScannerField.enfocar(campoCodigo);
        } catch (DatosException e) {
            lectorSeleccionado = null;
            lblLector.setText(" ");
            Alertas.error(e.getMessage());
        }
    }

    private void agregarEjemplar(String codigo) {
        try {
            Modelos.Ejemplar ejemplar = Datos.ejemplarPorCodigo(codigo);
            if (tablaEscaneados.items().stream().anyMatch(e -> codigo.equals(e.codigoBarras))) {
                lblMensaje.setText("El ejemplar " + codigo + " ya está en la lista.");
                return;
            }
            if (!"disponible".equals(ejemplar.estado)) {
                lblMensaje.setText("El ejemplar " + codigo + " no está disponible (" + ejemplar.estado + ").");
                return;
            }
            tablaEscaneados.agregar(ejemplar);
            lblMensaje.setText("Agregado: " + ejemplar.tituloLibro);
            ScannerField.enfocar(campoCodigo);
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
            ScannerField.enfocar(campoCodigo);
        }
    }

    private void quitar() {
        Modelos.Ejemplar sel = tablaEscaneados.seleccionado();
        if (sel != null) {
            tablaEscaneados.quitar(sel);
        }
    }

    private void confirmar() {
        if (lectorSeleccionado == null) {
            Alertas.advertencia("Primero busca y selecciona un lector.");
            return;
        }
        if (tablaEscaneados.items().isEmpty()) {
            Alertas.advertencia("Escanea al menos un ejemplar.");
            return;
        }
        int dias;
        try {
            dias = Integer.parseInt(campoDias.getText().trim());
        } catch (NumberFormatException e) {
            dias = 14;
        }
        List<String> codigos = new ArrayList<>();
        for (Modelos.Ejemplar e : tablaEscaneados.items()) {
            codigos.add(e.codigoBarras);
        }
        Modelos.PrestamoRequest req = new Modelos.PrestamoRequest();
        req.idLector = lectorSeleccionado.idLector;
        req.codigosBarras = codigos;
        req.diasPrestamo = dias;
        try {
            Modelos.PrestamoResponse resp = Datos.prestar(req);
            Alertas.info(resp.mensaje);
            tablaEscaneados.limpiar();
            campoMatricula.setText("");
            lblLector.setText(" ");
            lectorSeleccionado = null;
            lblMensaje.setText(" ");
        } catch (DatosException e) {
            Alertas.error(e.getMessage());
        }
    }
}
