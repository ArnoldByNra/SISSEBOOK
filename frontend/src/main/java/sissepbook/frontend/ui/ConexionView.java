package sissepbook.frontend.ui;

import sissepbook.backend.config.BaseDatos;
import sissepbook.backend.config.ConfiguracionConexion;
import sissepbook.backend.exception.Errores;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Pantalla de conexión a la base de datos: MySQL local o Azure Database for MySQL.
 * La app se conecta directo con estos datos. Se guardan en ~/.sissebook/conexion.properties.
 */
public class ConexionView extends Fondo {

    private static final String LOCAL = "MySQL local (en esta computadora)";
    private static final String AZURE = "Azure Database for MySQL (en la nube)";
    private static final int ANCHO = 500;

    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{LOCAL, AZURE});
    private final JTextField tfHost = new JTextField(24);
    private final JTextField tfPuerto = new JTextField(6);
    private final JTextField tfBase = new JTextField();
    private final JTextField tfUsuario = new JTextField();
    private final JPasswordField tfContrasena = new JPasswordField();
    private final JCheckBox chkSsl = new JCheckBox("Usar conexión segura SSL (obligatorio en Azure)");
    private final JCheckBox chkRecordar = new JCheckBox("Recordar la contraseña en este equipo");
    private final Parrafo lblEstado = new Parrafo(ANCHO);
    private final Parrafo lblAyuda = new Parrafo(ANCHO);
    private final Boton btnProbar = Comp.boton("Probar conexión", false);
    private final Boton btnConectar = Comp.boton("Guardar y conectar", true);
    private final Boton btnCrear = Comp.boton("Crear base de datos…", false);
    private final Boton btnVolver = Comp.boton("Volver al inicio de sesión", false);
    private boolean cargando;

    public ConexionView() {
        JLabel titulo = Comp.etiqueta("Conexión a la base de datos", Font.BOLD, 22, Tema.AZUL_BARRA);
        Parrafo sub = new Parrafo(ANCHO);
        sub.setForeground(Tema.TEXTO_SUAVE);
        sub.setTexto("Elige dónde está la base de datos y captura los datos de acceso.");

        lblAyuda.setForeground(Tema.TEXTO_SUAVE);
        lblAyuda.setFont(Tema.fuente(Font.PLAIN, 12));
        lblEstado.setFont(Tema.fuente(Font.PLAIN, 13));
        lblEstado.setTexto("");

        JPanel form = Comp.formulario(
                new String[]{"Tipo:", "Servidor:", "Puerto:", "Base de datos:", "Usuario:", "Contraseña:", null, null},
                new javax.swing.JComponent[]{cbTipo, tfHost, tfPuerto, tfBase, tfUsuario, tfContrasena, chkSsl, chkRecordar});
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        form.setPreferredSize(new Dimension(ANCHO, form.getPreferredSize().height));
        chkSsl.setOpaque(false);
        chkRecordar.setOpaque(false);
        cbTipo.addActionListener(e -> aplicarTipo());

        btnProbar.addActionListener(e -> probar());
        btnConectar.addActionListener(e -> guardarYConectar());
        btnCrear.addActionListener(e -> crearBase());
        btnVolver.addActionListener(e -> Navegador.ir("LoginView"));
        btnVolver.setVisible(BaseDatos.estaConectado());

        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.add(btnProbar);
        botones.add(btnCrear);
        botones.add(btnConectar);

        Tarjeta card = Comp.tarjeta(28);
        for (Component c : new Component[]{titulo, sub, form, lblAyuda, lblEstado, botones, btnVolver}) {
            ((javax.swing.JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(14));
        card.add(form);
        card.add(Box.createVerticalStrut(10));
        card.add(lblAyuda);
        card.add(Box.createVerticalStrut(8));
        card.add(lblEstado);
        card.add(Box.createVerticalStrut(10));
        card.add(botones);
        card.add(Box.createVerticalStrut(6));
        card.add(btnVolver);
        add(card);

        ConfiguracionConexion cfg = ConfiguracionConexion.cargar();
        cargando = true;
        cbTipo.setSelectedItem(cfg.getTipo() == ConfiguracionConexion.Tipo.AZURE ? AZURE : LOCAL);
        cargando = false;
        tfHost.setText(cfg.getHost());
        tfPuerto.setText(String.valueOf(cfg.getPuerto()));
        tfBase.setText(cfg.getBaseDatos());
        tfUsuario.setText(cfg.getUsuario());
        tfContrasena.setText(cfg.getContrasena());
        chkSsl.setSelected(cfg.isSsl());
        chkRecordar.setSelected(cfg.isRecordarContrasena());
        actualizarAyuda();
    }

    // ------------------------------------------------------------------ tipo de servidor

    private void aplicarTipo() {
        if (cargando) {
            return;
        }
        boolean azure = AZURE.equals(cbTipo.getSelectedItem());
        if (azure) {
            if ("localhost".equals(tfHost.getText().trim())) {
                tfHost.setText("");
            }
            tfHost.setToolTipText("miservidor.mysql.database.azure.com");
            tfPuerto.setText("3306");
            chkSsl.setSelected(true);
            if ("root".equals(tfUsuario.getText().trim())) {
                tfUsuario.setText("");
            }
        } else {
            if (tfHost.getText().isBlank() || tfHost.getText().endsWith("azure.com")) {
                tfHost.setText("localhost");
            }
            tfPuerto.setText("3306");
            chkSsl.setSelected(false);
            if (tfUsuario.getText().isBlank()) {
                tfUsuario.setText("root");
            }
        }
        actualizarAyuda();
    }

    private void actualizarAyuda() {
        if (AZURE.equals(cbTipo.getSelectedItem())) {
            lblAyuda.setTexto("Azure: usa el nombre del servidor que aparece en el portal (termina en "
                    + ".mysql.database.azure.com) y agrega tu IP en las reglas de firewall del servidor. "
                    + "Para crear triggers y funciones activa el parámetro log_bin_trust_function_creators = ON.");
        } else {
            lblAyuda.setTexto("Local: necesitas MySQL 8 instalado y en ejecución. Usuario y contraseña de tu MySQL.");
        }
    }

    // ------------------------------------------------------------------ acciones

    /** Lee el formulario; devuelve null (y muestra el motivo) si algo no es válido. */
    private ConfiguracionConexion leer() {
        ConfiguracionConexion cfg = new ConfiguracionConexion();
        cfg.setTipo(AZURE.equals(cbTipo.getSelectedItem()) ? ConfiguracionConexion.Tipo.AZURE : ConfiguracionConexion.Tipo.LOCAL);
        cfg.setHost(tfHost.getText().trim());
        try {
            cfg.setPuerto(Integer.parseInt(tfPuerto.getText().trim()));
        } catch (NumberFormatException e) {
            mostrarError("El puerto debe ser un número (normalmente 3306).");
            return null;
        }
        cfg.setBaseDatos(tfBase.getText().trim());
        cfg.setUsuario(tfUsuario.getText().trim());
        cfg.setContrasena(new String(tfContrasena.getPassword()));
        cfg.setSsl(chkSsl.isSelected());
        cfg.setRecordarContrasena(chkRecordar.isSelected());
        try {
            cfg.validar();
        } catch (RuntimeException e) {
            mostrarError(Errores.mensaje(e));
            return null;
        }
        return cfg;
    }

    private void probar() {
        ConfiguracionConexion cfg = leer();
        if (cfg == null) {
            return;
        }
        ejecutar("Probando conexión…", () -> BaseDatos.probar(cfg), estado -> {
            if (!estado.baseExiste()) {
                mostrarOk("Conexión correcta. La base «" + cfg.getBaseDatos()
                        + "» aún no existe: usa «Crear base de datos».");
            } else if (!estado.inicializada()) {
                mostrarOk("Conexión correcta. La base existe pero no tiene las tablas del sistema: "
                        + "usa «Crear base de datos».");
            } else {
                mostrarOk("Conexión correcta. La base «" + cfg.getBaseDatos() + "» está lista.");
            }
        });
    }

    private void guardarYConectar() {
        ConfiguracionConexion cfg = leer();
        if (cfg == null) {
            return;
        }
        ejecutar("Conectando…", () -> BaseDatos.probar(cfg), estado -> {
            if (!estado.inicializada()) {
                mostrarError("La base «" + cfg.getBaseDatos() + "» no tiene las tablas del sistema. "
                        + "Usa «Crear base de datos» para crearla con datos de prueba.");
                return;
            }
            ejecutar("Conectando…", () -> {
                BaseDatos.conectar(cfg);
                cfg.guardar();
                return Boolean.TRUE;
            }, ok -> Navegador.ir("LoginView"));
        });
    }

    private void crearBase() {
        ConfiguracionConexion cfg = leer();
        if (cfg == null) {
            return;
        }
        boolean confirmado = Alertas.confirmar("Se creará la base «" + cfg.getBaseDatos() + "» con sus tablas, "
                + "procedimientos, triggers y datos de prueba.\n\n"
                + "ATENCIÓN: si ya existe una base con ese nombre, se BORRARÁ junto con todos sus datos.\n\n"
                + "¿Continuar?");
        if (!confirmado) {
            return;
        }
        ejecutar("Creando base de datos (puede tardar unos segundos)…", () -> {
            BaseDatos.inicializar(cfg);
            cfg.guardar();
            return Boolean.TRUE;
        }, ok -> {
            Alertas.info("Base de datos creada. Usuarios de prueba:\n"
                    + "amartinez / Admin1234* (Administrador)\n"
                    + "cherrera / Oper1234* (Operador)\n"
                    + "lmendoza / Cons1234* (Consulta)");
            Navegador.ir("LoginView");
        });
    }

    // ------------------------------------------------------------------ utilidades

    /** Corre el trabajo en un hilo aparte para no congelar la ventana. */
    private <T> void ejecutar(String texto, Supplier<T> trabajo, Consumer<T> alTerminar) {
        mostrarInfo(texto);
        bloquear(true);
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() {
                return trabajo.get();
            }

            @Override
            protected void done() {
                bloquear(false);
                try {
                    T valor = get();
                    lblEstado.setTexto("");
                    alTerminar.accept(valor);
                } catch (ExecutionException e) {
                    mostrarError(Errores.mensaje(e.getCause()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void bloquear(boolean si) {
        btnProbar.setEnabled(!si);
        btnConectar.setEnabled(!si);
        btnCrear.setEnabled(!si);
        btnVolver.setEnabled(!si);
    }

    private void mostrarInfo(String texto) {
        lblEstado.setForeground(Tema.TEXTO_SUAVE);
        lblEstado.setTexto(texto);
    }

    private void mostrarOk(String texto) {
        lblEstado.setForeground(Tema.OK);
        lblEstado.setTexto(texto);
    }

    private void mostrarError(String texto) {
        lblEstado.setForeground(Tema.ERROR);
        lblEstado.setTexto(texto);
    }
}
