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

@Entity
@Table(name = "devolucion_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DevolucionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "devolucion_id", nullable = false)
    private Integer devolucionId;

    @Column(name = "pedido_item_id", nullable = false)
    private Integer pedidoItemId;

    @Column(nullable = false)
    private BigDecimal cantidad;

    public DevolucionItem(Integer tenantId, Integer devolucionId, Integer pedidoItemId, BigDecimal cantidad) {
        this.tenantId = tenantId;
        this.devolucionId = devolucionId;
        this.pedidoItemId = pedidoItemId;
        this.cantidad = cantidad;
    }
}
