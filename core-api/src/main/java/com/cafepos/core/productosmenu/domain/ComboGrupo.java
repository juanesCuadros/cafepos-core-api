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

import java.time.OffsetDateTime;

/**
 * Mapea combo_grupo — agrupacion de opciones dentro de un combo (ej.
 * "Bebida", "Postre"), ON DELETE CASCADE desde combo. No tiene updated_at
 * en el schema (solo created_at) — renombrar no deja rastro de "ultima
 * edicion" a nivel de fila.
 */
@Entity
@Table(name = "combo_grupo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComboGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "combo_id", nullable = false)
    private Integer comboId;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public ComboGrupo(Integer tenantId, Integer comboId, String nombre) {
        this.tenantId = tenantId;
        this.comboId = comboId;
        this.nombre = nombre;
        this.createdAt = OffsetDateTime.now();
    }

    public void renombrar(String nombre) {
        this.nombre = nombre;
    }
}
