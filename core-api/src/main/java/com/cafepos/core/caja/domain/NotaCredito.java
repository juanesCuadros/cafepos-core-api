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
import java.time.OffsetDateTime;

/** Mapea nota_credito (ver V1__schema_v4.sql) — generada al anular una venta que tenia factura DIAN. */
@Entity
@Table(name = "nota_credito")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotaCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "factura_id", nullable = false)
    private Integer facturaId;

    @Column(name = "devolucion_id")
    private Integer devolucionId;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private OffsetDateTime fecha;

    /** devolucionId siempre null aca — nota credito por anulacion directa de venta, no por devolucion (modulo futuro). */
    public NotaCredito(Integer tenantId, Integer facturaId, String motivo, BigDecimal monto) {
        this.tenantId = tenantId;
        this.facturaId = facturaId;
        this.motivo = motivo;
        this.monto = monto;
        this.fecha = OffsetDateTime.now();
    }
}
