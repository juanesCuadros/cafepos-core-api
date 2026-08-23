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

import java.time.OffsetDateTime;

/**
 * Mapea categoria_insumo (ver V1__schema_v4.sql, Modulo 5 de
 * api_05_inventario.md) — catalogo chico, modal rapido desde el
 * formulario de Insumos. Sin estado ni PATCH/DELETE a proposito: el
 * contrato solo pide GET/POST para este catalogo.
 */
@Entity
@Table(name = "categoria_insumo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoriaInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public CategoriaInsumo(Integer tenantId, String nombre) {
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.createdAt = OffsetDateTime.now();
    }
}
