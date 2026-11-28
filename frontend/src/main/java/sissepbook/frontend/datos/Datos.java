package sissepbook.frontend.datos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import sissepbook.backend.config.BaseDatos;
import sissepbook.backend.config.Servicios;
import sissepbook.backend.dto.DevolucionRequest;
import sissepbook.backend.dto.EjemplarDTO;
import sissepbook.backend.dto.LectorDTO;
import sissepbook.backend.dto.LibroDTO;
import sissepbook.backend.dto.LoginRequest;
import sissepbook.backend.dto.PrestamoRequest;
import sissepbook.backend.dto.UsuarioDTO;
import sissepbook.backend.exception.Errores;
import sissepbook.backend.security.SesionActual;
import sissepbook.frontend.model.Modelos;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Único punto por el que las pantallas piden y guardan datos. Llama directo a los servicios del
 * backend (que hablan con la base de datos local o de Azure); no hay API ni HTTP de por medio.
 *
 * Los servicios devuelven sus DTO; aquí se copian a los modelos de las pantallas (Modelos) y,
 * si algo falla, se lanza una DatosException con un mensaje listo para mostrar.
 */
public final class Datos {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setTimeZone(TimeZone.getDefault());

    private static final TypeReference<List<Map<String, Object>>> FILAS = new TypeReference<>() { };
    private static final TypeReference<Map<String, Object>> MAPA = new TypeReference<>() { };

    private Datos() {
    }

    // ------------------------------------------------------------------ infraestructura

    @FunctionalInterface
    private interface Accion<T> {
        T ejecutar(Servicios s) throws Exception;
    }

    private static <T> T llamar(Accion<T> accion) {
        try {
            return accion.ejecutar(BaseDatos.servicios());
        } catch (DatosException e) {
            throw e;
        } catch (Exception e) {
            throw new DatosException(Errores.mensaje(e));
        }
    }

    private static <T> T conv(Object origen, Class<T> tipo) {
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(origen), tipo);
        } catch (Exception e) {
            throw new DatosException("No se pudo convertir la información: " + e.getMessage());
        }
    }

    private static <T> T conv(Object origen, TypeReference<T> tipo) {
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(origen), tipo);
        } catch (Exception e) {
            throw new DatosException("No se pudo convertir la información: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ sesión

    public static Modelos.LoginResponse login(String usuario, String contrasena) {
        LoginRequest r = new LoginRequest();
        r.setUsuario(usuario);
        r.setContrasena(contrasena);
        return llamar(s -> conv(s.auth().login(r), Modelos.LoginResponse.class));
    }

    public static void logout() {
        SesionActual.cerrar();
    }

    // ------------------------------------------------------------------ libros

    public static List<Modelos.Libro> libros(String filtro) {
        return llamar(s -> conv(s.libros().listar(filtro), new TypeReference<List<Modelos.Libro>>() { }));
    }

    public static Modelos.Libro crearLibro(Modelos.Libro libro) {
        return llamar(s -> conv(s.libros().crear(conv(libro, LibroDTO.class)), Modelos.Libro.class));
    }

    public static Modelos.Libro actualizarLibro(int idLibro, Modelos.Libro libro) {
        return llamar(s -> conv(s.libros().actualizar(idLibro, conv(libro, LibroDTO.class)), Modelos.Libro.class));
    }

    public static void eliminarLibro(int idLibro) {
        llamar(s -> {
            s.libros().eliminar(idLibro);
            return null;
        });
    }

    // ------------------------------------------------------------------ ejemplares

    public static List<Modelos.Ejemplar> ejemplaresPorLibro(int idLibro) {
        return llamar(s -> conv(s.ejemplares().listarPorLibro(idLibro), new TypeReference<List<Modelos.Ejemplar>>() { }));
    }

    public static Modelos.Ejemplar ejemplarPorCodigo(String codigoBarras) {
        return llamar(s -> conv(s.ejemplares().obtenerPorCodigo(codigoBarras), Modelos.Ejemplar.class));
    }

    /** Cuántos ejemplares disponibles tiene el título del código escaneado (usa sp_consultar_disponibilidad_codigo). */
    public static int disponiblesDelTitulo(String codigoBarras) {
        return llamar(s -> Integer.parseInt(s.ejemplares().consultarDisponibilidad(codigoBarras)[1]));
    }

    public static Modelos.Ejemplar crearEjemplar(Modelos.Ejemplar ejemplar) {
        return llamar(s -> conv(s.ejemplares().crear(conv(ejemplar, EjemplarDTO.class)), Modelos.Ejemplar.class));
    }

    public static Modelos.Ejemplar actualizarEjemplar(int idEjemplar, Modelos.Ejemplar ejemplar) {
        return llamar(s -> conv(s.ejemplares().actualizar(idEjemplar, conv(ejemplar, EjemplarDTO.class)),
                Modelos.Ejemplar.class));
    }

    // ------------------------------------------------------------------ lectores

    public static List<Modelos.Lector> lectores(String filtro) {
        return llamar(s -> conv(s.lectores().listar(filtro), new TypeReference<List<Modelos.Lector>>() { }));
    }

    public static Modelos.Lector lectorPorMatricula(String matricula) {
        return llamar(s -> conv(s.lectores().obtenerPorMatricula(matricula), Modelos.Lector.class));
    }

    public static Modelos.Lector crearLector(Modelos.Lector lector) {
        return llamar(s -> conv(s.lectores().crear(conv(lector, LectorDTO.class)), Modelos.Lector.class));
    }

    public static Modelos.Lector actualizarLector(int idLector, Modelos.Lector lector) {
        return llamar(s -> conv(s.lectores().actualizar(idLector, conv(lector, LectorDTO.class)), Modelos.Lector.class));
    }

    // ------------------------------------------------------------------ usuarios

    public static List<Modelos.Usuario> usuarios() {
        return llamar(s -> conv(s.usuarios().listar(), new TypeReference<List<Modelos.Usuario>>() { }));
    }

    public static List<Modelos.Rol> roles() {
        return llamar(s -> s.usuarios().listarRoles().stream().map(r -> {
            Modelos.Rol rol = new Modelos.Rol();
            rol.idRol = r[0];
            rol.nombreRol = r[1];
            return rol;
        }).toList());
    }

    public static Modelos.Usuario crearUsuario(Modelos.Usuario usuario) {
        return llamar(s -> conv(s.usuarios().crear(conv(usuario, UsuarioDTO.class)), Modelos.Usuario.class));
    }

    public static Modelos.Usuario actualizarUsuario(int idUsuario, Modelos.Usuario usuario) {
        return llamar(s -> conv(s.usuarios().actualizar(idUsuario, conv(usuario, UsuarioDTO.class)),
                Modelos.Usuario.class));
    }

    // ------------------------------------------------------------------ préstamos y devoluciones

    public static Modelos.PrestamoResponse prestar(Modelos.PrestamoRequest solicitud) {
        PrestamoRequest r = conv(solicitud, PrestamoRequest.class);
        return llamar(s -> conv(s.prestamos().registrar(r), Modelos.PrestamoResponse.class));
    }

    public static Modelos.DevolucionResponse devolver(String codigoBarras) {
        DevolucionRequest r = new DevolucionRequest();
        r.setCodigoBarras(codigoBarras);
        return llamar(s -> conv(s.devoluciones().registrar(r), Modelos.DevolucionResponse.class));
    }

    public static List<Map<String, Object>> prestamosVencidos() {
        return llamar(s -> conv(s.prestamos().vencidos(), FILAS));
    }

    // ------------------------------------------------------------------ panel, disponibilidad, historial

    public static Map<String, Object> dashboard() {
        return llamar(s -> conv(s.panel().resumen(), MAPA));
    }

    public static List<Map<String, Object>> disponibilidad(String filtro) {
        return llamar(s -> conv(s.panel().disponibilidad(filtro), FILAS));
    }

    public static List<Map<String, Object>> historial(int limite) {
        return llamar(s -> conv(s.panel().historial(limite), FILAS));
    }

    // ------------------------------------------------------------------ reportes y bitácora

    public static List<Map<String, Object>> reporteLibrosMasSolicitados(int limite) {
        return llamar(s -> conv(s.reportes().librosMasSolicitados(limite), FILAS));
    }

    public static List<Map<String, Object>> reporteLectoresVencidos() {
        return llamar(s -> conv(s.reportes().lectoresVencidos(), FILAS));
    }

    public static Map<String, Object> reportePeriodo(String desde, String hasta) {
        return llamar(s -> conv(s.reportes().resumenPeriodo(desde, hasta), MAPA));
    }

    public static List<Modelos.Bitacora> bitacora(String tabla, String usuario, int limite) {
        return llamar(s -> conv(s.bitacora().consultar(tabla, usuario, null, null, limite),
                new TypeReference<List<Modelos.Bitacora>>() { }));
    }
}
