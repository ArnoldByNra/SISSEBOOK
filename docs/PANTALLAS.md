# Pantallas (ajustar con el diseño de Figma)


| # | Pantalla | FXML | Controller | Roles | Endpoint(s) |
|---|---|---|---|---|---|
| 1 | Login | `LoginView.fxml` | `LoginController` | todos | POST /api/auth/login |
| 2 | Menú principal | `MainView.fxml` | `MainController` | todos (botones según rol) | - |
| 3 | Libros | `LibrosView.fxml` | `LibrosController` | ADMIN, OPERADOR(ver/crear) , CONSULTA(ver) | /api/libros |
| 4 | Ejemplares | `EjemplaresView.fxml` | `EjemplaresController` | ADMIN, OPERADOR | /api/ejemplares |
| 5 | Lectores | `LectoresView.fxml` | `LectoresController` | ADMIN, OPERADOR | /api/lectores |
| 6 | Préstamo (escáner) | `PrestamoView.fxml` | `PrestamoController` | ADMIN, OPERADOR | POST /api/prestamos |
| 7 | Devolución (escáner) | `DevolucionView.fxml` | `DevolucionController` | ADMIN, OPERADOR | POST /api/devoluciones |
| 8 | Consulta por código | `ConsultaCodigoView.fxml` | `ConsultaCodigoController` | todos | GET /api/ejemplares/codigo/{cb} |
| 9 | Reportes | `ReportesView.fxml` | `ReportesController` | ADMIN, OPERADOR | /api/reportes/* |
| 10 | Usuarios | `UsuariosView.fxml` | `UsuariosController` | ADMIN | /api/usuarios |
| 11 | Bitácora | `BitacoraView.fxml` | `BitacoraController` | ADMIN | GET /api/bitacora |
