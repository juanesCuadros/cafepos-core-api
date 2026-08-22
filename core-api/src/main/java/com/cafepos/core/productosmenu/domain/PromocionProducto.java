package com.cafepos.core.productosmenu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Mapea promocion_producto — tabla puente N:M entre promocion y producto (ON DELETE CASCADE desde promocion). */
@Entity
@Table(name = "promocion_producto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromocionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "promocion_id", nullable = false)
    private Integer promocionId;

    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    public PromocionProducto(Integer tenantId, Integer promocionId, Integer productoId) {
        this.tenantId = tenantId;
        this.promocionId = promocionId;
        this.productoId = productoId;
    }
}
