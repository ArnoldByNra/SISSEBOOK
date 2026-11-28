package sissepbook.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import sissepbook.backend.dto.BitacoraDTO;

import java.util.ArrayList;
import java.util.List;

/** SELECT de bitacora_auditoria con filtros. Única capa que escribe SQL de bitácora. */
public class BitacoraRepository {

    private final JdbcTemplate jdbc;

    public BitacoraRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<BitacoraDTO> consultar(String tabla, String usuario, String desde, String hasta, int limite) {
        StringBuilder sql = new StringBuilder(
                "SELECT id_auditoria, tabla_afectada, tipo_operacion, id_registro, fecha_hora, " +
                "usuario_responsable, CAST(informacion_anterior AS CHAR) AS info_ant, " +
                "CAST(informacion_nueva AS CHAR) AS info_nueva " +
                "FROM bitacora_auditoria WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (tabla != null && !tabla.isBlank()) {
            sql.append("AND tabla_afectada = ? ");
            params.add(tabla);
        }
        if (usuario != null && !usuario.isBlank()) {
            sql.append("AND usuario_responsable LIKE ? ");
            params.add("%" + usuario + "%");
        }
        if (desde != null && !desde.isBlank()) {
            sql.append("AND fecha_hora >= ? ");
            params.add(desde);
        }
        if (hasta != null && !hasta.isBlank()) {
            sql.append("AND fecha_hora <= ? ");
            params.add(hasta);
        }
        int lim = (limite > 0 && limite <= 1000) ? limite : 200;
        sql.append("ORDER BY fecha_hora DESC LIMIT ?");
        params.add(lim);

        return jdbc.query(sql.toString(), (rs, i) -> {
            BitacoraDTO dto = new BitacoraDTO();
            dto.setIdAuditoria(rs.getLong("id_auditoria"));
            dto.setTablaAfectada(rs.getString("tabla_afectada"));
            dto.setTipoOperacion(rs.getString("tipo_operacion"));
            dto.setIdRegistro(rs.getString("id_registro"));
            dto.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
            dto.setUsuarioResponsable(rs.getString("usuario_responsable"));
            dto.setInformacionAnterior(rs.getString("info_ant"));
            dto.setInformacionNueva(rs.getString("info_nueva"));
            return dto;
        }, params.toArray());
    }
}
