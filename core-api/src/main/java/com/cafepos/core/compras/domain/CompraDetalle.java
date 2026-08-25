package com.cafepos.core.compras.domain;

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

/** Mapea compra_detalle (ver V1__schema_v4.sql) — una fila por linea de insumo de una compra, INSERT-only. */
@Entity
@Table(name = "compra_detalle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "compra_id", nullable = false)
    private Integer compraId;

    @Column(name = "insumo_id", nullable = false)
    private Integer insumoId;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(name = "costo_unitario", nullable = false)
    private BigDecimal costoUnitario;

    @Column(name = "numero_lote")
    private String numeroLote;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private BigDecimal subtotal;

    public CompraDetalle(Integer tenantId, Integer compraId, Integer insumoId, BigDecimal cantidad,
                          BigDecimal costoUnitario, String numeroLote, LocalDate fechaVencimiento,
                          BigDecimal subtotal) {
        this.tenantId = tenantId;
        this.compraId = compraId;
        this.insumoId = insumoId;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.subtotal = subtotal;
    }
}
