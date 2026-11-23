package sissepbook.frontend.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sissepbook.frontend.api.ApiClient;
import sissepbook.frontend.api.ApiException;
import sissepbook.frontend.model.Modelos;
import sissepbook.frontend.util.Alertas;
import sissepbook.frontend.util.Navegador;

import java.util.List;

/**
 * Controller de ReportesView.fxml.
 * Dos pestañas: libros más solicitados y lectores con préstamos vencidos.
 * <b>No</b> contiene SQL.
 */
public class ReportesController {

    @FXML private TableView<Modelos.ReporteLibroSolicitado> tablaLibros;
    @FXML private TableColumn<Modelos.ReporteLibroSolicitado, String> colLTitulo;
    @FXML private TableColumn<Modelos.ReporteLibroSolicitado, String> colLAutor;
    @FXML private TableColumn<Modelos.ReporteLibroSolicitado, String> colLCategoria;
    @FXML private TableColumn<Modelos.ReporteLibroSolicitado, Integer> colLTotal;
    @FXML private TableColumn<Modelos.ReporteLibroSolicitado, Integer> colLLectores;
    @FXML private TableColumn<Modelos.ReporteLibroSolicitado, Integer> colLDisp;

    @FXML private TableView<Modelos.ReporteLectorVencido> tablaLectores;
    @FXML private TableColumn<Modelos.ReporteLectorVencido, String> colLMatricula;
    @FXML private TableColumn<Modelos.ReporteLectorVencido, String> colLNombre;
    @FXML private TableColumn<Modelos.ReporteLectorVencido, Integer> colLPendientes;
    @FXML private TableColumn<Modelos.ReporteLectorVencido, Integer> colLMaxDias;
    @FXML private TableColumn<Modelos.ReporteLectorVencido, Integer> colLTotalDias;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        colLTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colLAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colLCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colLTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrestamos"));
        colLLectores.setCellValueFactory(new PropertyValueFactory<>("lectoresDistintos"));
        colLDisp.setCellValueFactory(new PropertyValueFactory<>("ejemplaresDisponibles"));

        colLMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colLNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colLPendientes.setCellValueFactory(new PropertyValueFactory<>("prestamosPendientes"));
        colLMaxDias.setCellValueFactory(new PropertyValueFactory<>("maxDiasRetraso"));
        colLTotalDias.setCellValueFactory(new PropertyValueFactory<>("totalDiasRetraso"));

        actualizar();
    }

    @FXML
    private void volver() {
        Navegador.ir("MainView");
    }

    @FXML
    private void actualizar() {
        try {
            List<Modelos.ReporteLibroSolicitado> libros = api.getList(
                    "/api/reportes/libros-mas-solicitados?limite=50",
                    new com.fasterxml.jackson.core.type.TypeReference<List<Modelos.ReporteLibroSolicitado>>() { });
            tablaLibros.setItems(FXCollections.observableArrayList(libros));

            List<Modelos.ReporteLectorVencido> lectores = api.getList(
                    "/api/reportes/lectores-vencidos",
                    new com.fasterxml.jackson.core.type.TypeReference<List<Modelos.ReporteLectorVencido>>() { });
            tablaLectores.setItems(FXCollections.observableArrayList(lectores));
        } catch (ApiException e) {
            Alertas.error(e.getMessage());
        }
    }
}
