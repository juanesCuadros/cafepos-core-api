package com.cafepos.core.personal.infrastructure.persistence;

import com.cafepos.core.personal.domain.Empleado;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** Nativa con CAST explicito en cada ocurrencia de cada parametro opcional (ver CLAUDE.md). */
interface EmpleadoJpaRepository extends TenantAwareRepository<Empleado, Integer> {

    @Query(value = "SELECT id AS id, codigo AS codigo, nombre AS nombre, cedula AS cedula, cargo AS cargo, "
            + "telefono AS telefono, estado AS estado FROM empleado "
            + "WHERE (CAST(:cargo AS varchar) IS NULL OR cargo = CAST(:cargo AS varchar)) "
            + "AND (CAST(:estado AS varchar) IS NULL OR estado = CAST(:estado AS varchar)) "
            + "AND (CAST(:q AS varchar) IS NULL OR nombre ILIKE '%' || CAST(:q AS varchar) || '%') "
            + "ORDER BY nombre", nativeQuery = true)
    List<EmpleadoResumenRow> listar(@Param("cargo") String cargo, @Param("estado") String estado,
                                     @Param("q") String q);

    /** El indice unico idx_usuario_empleado_id_unico (V27) garantiza a lo sumo una fila. */
    @Query(value = "SELECT u.id AS id, u.correo AS correo, r.nombre AS rol FROM usuario u "
            + "JOIN rol r ON r.id = u.rol_id WHERE u.empleado_id = :empleadoId", nativeQuery = true)
    Optional<UsuarioAsociadoRow> buscarUsuarioAsociado(@Param("empleadoId") Integer empleadoId);

    @Query(value = "SELECT COUNT(*) AS total_turnos, COALESCE(SUM(horas_trabajadas), 0) AS horas_trabajadas "
            + "FROM turno WHERE empleado_id = :empleadoId "
            + "AND fecha >= date_trunc('month', CURRENT_DATE)::date "
            + "AND fecha < (date_trunc('month', CURRENT_DATE) + interval '1 month')::date", nativeQuery = true)
    ResumenTurnosMesRow resumenTurnosMesActual(@Param("empleadoId") Integer empleadoId);
}
