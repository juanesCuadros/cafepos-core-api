package com.cafepos.core.personal.infrastructure.persistence;

import com.cafepos.core.personal.domain.Turno;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/** Prefijo "Personal": el nombre simple "TurnoJpaRepository" colisiona con operacion.infrastructure.persistence.TurnoJpaRepository (mismo bean id por defecto en Spring) — confirmado real. */
interface PersonalTurnoJpaRepository extends TenantAwareRepository<Turno, Integer> {

    @Query(value = "SELECT t.id AS id, e.nombre AS empleado_nombre, t.fecha AS fecha, "
            + "t.hora_inicio AS hora_inicio, t.hora_fin AS hora_fin, t.horas_trabajadas AS horas_trabajadas "
            + "FROM turno t JOIN empleado e ON e.id = t.empleado_id "
            + "WHERE (CAST(:fechaInicio AS date) IS NULL OR t.fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR t.fecha <= CAST(:fechaFin AS date)) "
            + "AND (CAST(:empleadoId AS int) IS NULL OR t.empleado_id = CAST(:empleadoId AS int)) "
            + "ORDER BY t.fecha DESC, t.hora_inicio DESC", nativeQuery = true)
    List<TurnoResumenRow> listar(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin,
                                  @Param("empleadoId") Integer empleadoId);
}
