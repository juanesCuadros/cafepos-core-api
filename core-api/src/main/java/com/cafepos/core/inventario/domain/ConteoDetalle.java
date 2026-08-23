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

/**
 * Mapea conteo_detalle — una fila por insumo contado. stockSistema es una
 * fotografia tomada ANTES de aplicar ningun UPDATE (ver ConteoService),
 * nunca se recalcula ni edita despues.
 */
@Entity
@Table(name = "conteo_detalle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConteoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "conteo_id", nullable = false)
    private Integer conteoId;

    @Column(name = "insumo_id", nullable = false)
    private Integer insumoId;

    @Column(name = "stock_sistema", nullable = false)
    private BigDecimal stockSistema;

    @Column(name = "stock_fisico", nullable = false)
    private BigDecimal stockFisico;

    @Column(nullable = false)
    private BigDecimal diferencia;

    public ConteoDetalle(Integer tenantId, Integer conteoId, Integer insumoId, BigDecimal stockSistema,
                          BigDecimal stockFisico) {
        this.tenantId = tenantId;
        this.conteoId = conteoId;
        this.insumoId = insumoId;
        this.stockSistema = stockSistema;
        this.stockFisico = stockFisico;
        this.diferencia = stockFisico.subtract(stockSistema);
    }
}
