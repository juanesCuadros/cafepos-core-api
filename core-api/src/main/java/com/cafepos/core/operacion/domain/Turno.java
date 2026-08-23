package com.cafepos.core.operacion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/** Mapea turno (ver V1__schema_v4.sql). */
@Entity
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

    public Turno(Integer tenantId, Integer empleadoId, Integer usuarioId) {
        this.tenantId = tenantId;
        this.empleadoId = empleadoId;
        this.usuarioId = usuarioId;
        this.horaInicio = OffsetDateTime.now();
        this.fecha = horaInicio.atZoneSameInstant(ZoneOffset.UTC).toLocalDate();
    }

    public boolean estaActivo() {
        return horaFin == null;
    }

    public void cerrar() {
        this.horaFin = OffsetDateTime.now();
        long minutos = Duration.between(horaInicio, horaFin).toMinutes();
        this.horasTrabajadas = BigDecimal.valueOf(minutos).divide(MINUTOS_POR_HORA, 2, RoundingMode.HALF_UP);
    }
}
