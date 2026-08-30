package com.cafepos.core.personal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Mapea turno (ver V1__schema_v4.sql, Modulo 8.2) — mapeo PROPIO de este
 * modulo para la gestion Admin/Jefe, misma tabla fisica que
 * operacion.domain.Turno (autoregistro del empleado, pantalla distinta,
 * ver api_02_operacion.md). usuario_id aca es SIEMPRE quien registra el
 * turno (el admin/jefe autenticado), nunca el empleado — mismo campo,
 * mismo significado que ya usa el autoregistro, solo que ahi coincide con
 * el propio empleado.
 */
/**
 * name = "PersonalTurno" a proposito: el nombre de entidad JPA por
 * defecto (nombre simple de la clase) coincidiria con
 * operacion.domain.Turno — Hibernate exige nombres de entidad distintos
 * dentro del mismo persistence unit aunque mapeen la misma tabla fisica
 * (confirmado real: DuplicateMappingException al arrancar). No afecta el
 * mapeo real (sigue siendo la tabla turno) ni ninguna query, este modulo
 * solo usa SQL nativo.
 */
@Entity(name = "PersonalTurno")
@Table(name = "turno")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Turno {

    private static final BigDecimal MINUTOS_POR_HORA = new BigDecimal("60");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "empleado_id", nullable = false)
    private Integer empleadoId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private OffsetDateTime horaInicio;

    @Column(name = "hora_fin")
    private OffsetDateTime horaFin;

    @Column(name = "horas_trabajadas")
    private BigDecimal horasTrabajadas;

    @Column
    private String observaciones;

    public Turno(Integer tenantId, Integer empleadoId, Integer usuarioId, LocalDate fecha, OffsetDateTime horaInicio,
                 OffsetDateTime horaFin, String observaciones) {
        this.tenantId = tenantId;
        this.empleadoId = empleadoId;
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.horasTrabajadas = calcularHoras(horaInicio, horaFin);
        this.observaciones = observaciones;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no
     * tocar". horas_trabajadas se RECALCULA si cambia hora_inicio u
     * hora_fin (nunca se acepta del cliente, ver DECISIONES YA TOMADAS).
     */
    public void actualizar(Integer empleadoId, LocalDate fecha, OffsetDateTime horaInicio, OffsetDateTime horaFin,
                            JsonNullable<String> observaciones) {
        if (empleadoId != null) {
            this.empleadoId = empleadoId;
        }
        if (fecha != null) {
            this.fecha = fecha;
        }
        boolean cambioHoras = horaInicio != null || horaFin != null;
        if (horaInicio != null) {
            this.horaInicio = horaInicio;
        }
        if (horaFin != null) {
            this.horaFin = horaFin;
        }
        if (cambioHoras) {
            this.horasTrabajadas = calcularHoras(this.horaInicio, this.horaFin);
        }
        if (observaciones.isPresent()) {
            this.observaciones = observaciones.get();
        }
    }

    /** Nunca confiar en un valor del cliente — siempre calculado desde hora_inicio/hora_fin reales. */
    public static BigDecimal calcularHoras(OffsetDateTime horaInicio, OffsetDateTime horaFin) {
        long minutos = Duration.between(horaInicio, horaFin).toMinutes();
        return BigDecimal.valueOf(minutos).divide(MINUTOS_POR_HORA, 2, RoundingMode.HALF_UP);
    }
}
