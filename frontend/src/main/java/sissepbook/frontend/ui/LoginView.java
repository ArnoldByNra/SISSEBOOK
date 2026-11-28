package sissepbook.frontend.ui;

import sissepbook.frontend.datos.Datos;
import sissepbook.frontend.datos.DatosException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.session.Sesion;
import sissepbook.frontend.util.Navegador;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Font;

/** Inicio de sesión: valida usuario y contraseña contra la base de datos -> guarda Sesion -> abre el marco principal. */
public class LoginView extends Fondo {

    private final JTextField campoUsuario = new JTextField();
    private final JPasswordField campoContrasena = new JPasswordField();
    private final Parrafo lblError = new Parrafo(300);
    private final JLabel lblCargando = Comp.suave(" ");
    private final Boton btnIngresar = Comp.boton("Ingresar", true);

    public LoginView() {
        JLabel titulo = Comp.etiqueta("SISSE · Biblioteca", Font.BOLD, 22, Tema.AZUL_BARRA);
        JLabel sub = Comp.etiqueta("Inicia sesión para continuar", Font.PLAIN, 13, Tema.TEXTO_SUAVE);
        campoUsuario.setToolTipText("Usuario");
        campoContrasena.setToolTipText("Contraseña");
        campoContrasena.addActionListener(e -> ingresar());
        campoUsuario.addActionListener(e -> campoContrasena.requestFocusInWindow());
        lblError.setForeground(Tema.ERROR);
        lblError.setFont(Tema.fuente(Font.PLAIN, 12));
        lblError.setTexto("");
        btnIngresar.addActionListener(e -> ingresar());
        Boton btnConexion = Comp.boton("Configurar conexión a la base de datos", false);
        btnConexion.addActionListener(e -> Navegador.ir("ConexionView"));

        Tarjeta card = Comp.tarjeta(32);
        for (var c : new javax.swing.JComponent[]{titulo, sub}) {
            c.setAlignmentX(0.5f);
        }
        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(campo("Usuario", campoUsuario));
        card.add(Box.createVerticalStrut(10));
        card.add(campo("Contraseña", campoContrasena));
        card.add(Box.createVerticalStrut(8));
        lblError.setAlignmentX(0.5f);
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));
        anchoCompleto(btnIngresar);
        card.add(btnIngresar);
        lblCargando.setAlignmentX(0.5f);
        card.add(Box.createVerticalStrut(6));
        card.add(lblCargando);
        card.add(Box.createVerticalStrut(6));
        anchoCompleto(btnConexion);
        card.add(btnConexion);
        add(card);
    }

    private static javax.swing.JComponent campo(String etiqueta, javax.swing.JComponent campo) {
        JLabel l = Comp.etiqueta(etiqueta, Font.PLAIN, 12, Tema.TEXTO_SUAVE);
        javax.swing.JPanel p = Comp.columna(3, l, campo);
        campo.setPreferredSize(new Dimension(300, 30));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        p.setAlignmentX(0.5f);
        p.setBorder(BorderFactory.createEmptyBorder());
        return p;
    }

    private static void anchoCompleto(Boton b) {
        b.setAlignmentX(0.5f);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, b.getPreferredSize().height));
    }

    private void ingresar() {
        lblError.setTexto("");
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoContrasena.getPassword());
        if (usuario == null || usuario.isBlank() || contrasena.isBlank()) {
            lblError.setTexto("Ingresa usuario y contraseña.");
            return;
        }
        btnIngresar.setEnabled(false);
        lblCargando.setText("Validando...");
        paintImmediately(0, 0, getWidth(), getHeight());
        try {
            Modelos.LoginResponse resp = Datos.login(usuario.trim(), contrasena);
            Sesion.iniciar(resp.idUsuario, resp.nombreUsuario, resp.nombreCompleto, resp.rol);
            Navegador.ir("MainView");
        } catch (DatosException e) {
            lblError.setTexto(e.getMessage());
        } catch (Exception e) {
            lblError.setTexto("No se pudo iniciar sesión: " + e.getMessage());
        } finally {
            btnIngresar.setEnabled(true);
            lblCargando.setText(" ");
        }
    }
}
