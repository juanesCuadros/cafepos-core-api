package com.cafepos.core.caja.domain;

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

/** Mapea venta_promocion (ver V1__schema_v4.sql) — promociones_aplicadas del request de POST /ventas. */
@Entity
@Table(name = "venta_promocion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VentaPromocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "venta_id", nullable = false)
    private Integer ventaId;

    @Column(name = "promocion_id", nullable = false)
    private Integer promocionId;

    @Column(name = "monto_descuento", nullable = false)
    private BigDecimal montoDescuento;

    public VentaPromocion(Integer tenantId, Integer ventaId, Integer promocionId, BigDecimal montoDescuento) {
        this.tenantId = tenantId;
        this.ventaId = ventaId;
        this.promocionId = promocionId;
        this.montoDescuento = montoDescuento;
    }
}
