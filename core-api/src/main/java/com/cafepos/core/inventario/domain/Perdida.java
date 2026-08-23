package com.cafepos.core.inventario.domain;

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
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Mapea perdida (ver V1__schema_v4.sql, Modulo 5.5 de api_05_inventario.md) — sin actualizar/eliminar, solo se registra. */
@Entity
@Table(name = "perdida")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Perdida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "insumo_id", nullable = false)
    private Integer insumoId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column
    private String observaciones;

    @Column(name = "costo_calculado", nullable = false)
    private BigDecimal costoCalculado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public Perdida(Integer tenantId, Integer insumoId, Integer usuarioId, BigDecimal cantidad, String motivo,
                   LocalDate fecha, String observaciones, BigDecimal costoCalculado) {
        this.tenantId = tenantId;
        this.insumoId = insumoId;
        this.usuarioId = usuarioId;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.costoCalculado = costoCalculado;
        this.createdAt = OffsetDateTime.now();
    }
}
