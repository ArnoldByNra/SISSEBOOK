package sissepbook.backend.config;

import org.springframework.jdbc.core.JdbcTemplate;
import sissepbook.backend.repository.BitacoraRepository;
import sissepbook.backend.repository.EjemplarRepository;
import sissepbook.backend.repository.LectorRepository;
import sissepbook.backend.repository.LibroRepository;
import sissepbook.backend.repository.PanelRepository;
import sissepbook.backend.repository.PrestamoRepository;
import sissepbook.backend.repository.ReporteRepository;
import sissepbook.backend.repository.UsuarioRepository;
import sissepbook.backend.security.PasswordService;
import sissepbook.backend.service.AuthService;
import sissepbook.backend.service.BitacoraService;
import sissepbook.backend.service.DevolucionService;
import sissepbook.backend.service.EjemplarService;
import sissepbook.backend.service.LectorService;
import sissepbook.backend.service.LibroService;
import sissepbook.backend.service.PanelService;
import sissepbook.backend.service.PrestamoService;
import sissepbook.backend.service.ReporteService;
import sissepbook.backend.service.UsuarioService;

/** Crea y une repositorios y servicios (sin Spring). Se arma una vez por conexión. */
public final class Servicios {

    private final AuthService auth;
    private final UsuarioService usuarios;
    private final LibroService libros;
    private final EjemplarService ejemplares;
    private final LectorService lectores;
    private final PrestamoService prestamos;
    private final DevolucionService devoluciones;
    private final ReporteService reportes;
    private final BitacoraService bitacora;
    private final PanelService panel;

    Servicios(JdbcTemplate jdbc) {
        PasswordService passwords = new PasswordService();
        UsuarioRepository usuarioRepo = new UsuarioRepository(jdbc);
        PrestamoRepository prestamoRepo = new PrestamoRepository(jdbc);

        this.auth = new AuthService(usuarioRepo, passwords);
        this.usuarios = new UsuarioService(usuarioRepo, passwords);
        this.libros = new LibroService(new LibroRepository(jdbc));
        this.ejemplares = new EjemplarService(new EjemplarRepository(jdbc));
        this.lectores = new LectorService(new LectorRepository(jdbc));
        this.prestamos = new PrestamoService(prestamoRepo);
        this.devoluciones = new DevolucionService(prestamoRepo);
        this.reportes = new ReporteService(new ReporteRepository(jdbc));
        this.bitacora = new BitacoraService(new BitacoraRepository(jdbc));
        this.panel = new PanelService(new PanelRepository(jdbc));
    }

    public AuthService auth() { return auth; }
    public UsuarioService usuarios() { return usuarios; }
    public LibroService libros() { return libros; }
    public EjemplarService ejemplares() { return ejemplares; }
    public LectorService lectores() { return lectores; }
    public PrestamoService prestamos() { return prestamos; }
    public DevolucionService devoluciones() { return devoluciones; }
    public ReporteService reportes() { return reportes; }
    public BitacoraService bitacora() { return bitacora; }
    public PanelService panel() { return panel; }
}
