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

/** Mapea venta_pago (ver V1__schema_v4.sql) — un pago mixto es N filas por la misma venta_id. */
@Entity
@Table(name = "venta_pago")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VentaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "venta_id", nullable = false)
    private Integer ventaId;

    @Column(name = "metodo_pago_id", nullable = false)
    private Integer metodoPagoId;

    @Column(nullable = false)
    private BigDecimal monto;

    public VentaPago(Integer tenantId, Integer ventaId, Integer metodoPagoId, BigDecimal monto) {
        this.tenantId = tenantId;
        this.ventaId = ventaId;
        this.metodoPagoId = metodoPagoId;
        this.monto = monto;
    }
}
