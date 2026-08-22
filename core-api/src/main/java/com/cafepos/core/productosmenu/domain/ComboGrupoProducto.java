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

/**
 * Mapea combo_grupo_producto — tabla puente N:M entre combo_grupo y
 * producto (ON DELETE CASCADE desde combo_grupo). UNIQUE(combo_grupo_id,
 * producto_id): el mismo producto puede repetirse en grupos DISTINTOS del
 * mismo combo, pero no dos veces en el mismo grupo (ver
 * ComboRepositoryAdapter.agregarProducto).
 */
@Entity
@Table(name = "combo_grupo_producto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComboGrupoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "combo_grupo_id", nullable = false)
    private Integer comboGrupoId;

    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    public ComboGrupoProducto(Integer tenantId, Integer comboGrupoId, Integer productoId) {
        this.tenantId = tenantId;
        this.comboGrupoId = comboGrupoId;
        this.productoId = productoId;
    }
}
