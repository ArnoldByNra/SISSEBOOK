package sissepbook.frontend.ui;

import javax.swing.JComponent;

/** Fábrica de pantallas por nombre. Todas están hechas en código. */
public final class Vistas {

    private Vistas() {
    }

    public static JComponent crear(String nombre) {
        return switch (nombre) {
            case "ConexionView" -> new ConexionView();
            case "LoginView" -> new LoginView();
            case "DashboardView" -> new DashboardView();
            case "LibrosView" -> new LibrosView();
            case "PrestamoView" -> new PrestamoView();
            case "DevolucionView" -> new DevolucionView();
            case "DisponibilidadView" -> new DisponibilidadView();
            case "AtrasadosView" -> new AtrasadosView();
            case "ReportesView" -> new ReportesView();
            case "ConsultaCodigoView" -> new ConsultaCodigoView();
            case "EjemplaresView" -> new EjemplaresView();
            case "LectoresView" -> new LectoresView();
            case "HistorialView" -> new HistorialView();
            case "UsuariosView" -> new UsuariosView();
            case "BitacoraView" -> new BitacoraView();
            default -> throw new IllegalArgumentException("Pantalla desconocida: " + nombre);
        };
    }
}
