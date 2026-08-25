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

/**
 * Mapea lote_insumo (ver V1__schema_v4.sql, Modulo 5.6 de
 * api_05_inventario.md). crear()/agotar() son API publica de este modulo
 * EXCLUSIVA para com.cafepos.core.compras (ver LoteInsumoService,
 * tambien @NamedInterface) — este modulo en si sigue sin UI propia para
 * escribir lotes, solo los consulta para vencimientos.
 */
@Entity
@Table(name = "lote_insumo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoteInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "insumo_id", nullable = false)
    private Integer insumoId;

    @Column(name = "compra_detalle_id")
    private Integer compraDetalleId;

    @Column(name = "numero_lote", nullable = false)
    private String numeroLote;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "cantidad_inicial", nullable = false)
    private BigDecimal cantidadInicial;

    @Column(name = "cantidad_actual", nullable = false)
    private BigDecimal cantidadActual;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public static LoteInsumo crear(Integer tenantId, Integer insumoId, Integer compraDetalleId, String numeroLote,
                                    LocalDate fechaVencimiento, BigDecimal cantidad) {
        LoteInsumo lote = new LoteInsumo();
        lote.tenantId = tenantId;
        lote.insumoId = insumoId;
        lote.compraDetalleId = compraDetalleId;
        lote.numeroLote = numeroLote;
        lote.fechaVencimiento = fechaVencimiento;
        lote.cantidadInicial = cantidad;
        lote.cantidadActual = cantidad;
        lote.createdAt = OffsetDateTime.now();
        return lote;
    }

    /** "inactivo" para este modulo es cantidad_actual=0 — mismo criterio que ya usa VencimientoJpaRepository. */
    public void agotar() {
        this.cantidadActual = BigDecimal.ZERO;
    }
}
