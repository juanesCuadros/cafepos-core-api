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

/** Mapea pedido_item_combo_seleccion (ver V1__schema_v4.sql) — que producto eligio el cliente por cada grupo del combo. */
@Entity
@Table(name = "pedido_item_combo_seleccion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PedidoItemComboSeleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "pedido_item_id", nullable = false)
    private Integer pedidoItemId;

    @Column(name = "combo_grupo_id", nullable = false)
    private Integer comboGrupoId;

    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    public PedidoItemComboSeleccion(Integer tenantId, Integer pedidoItemId, Integer comboGrupoId,
                                     Integer productoId) {
        this.tenantId = tenantId;
        this.pedidoItemId = pedidoItemId;
        this.comboGrupoId = comboGrupoId;
        this.productoId = productoId;
    }
}
