package com.cafepos.core.configuracion.domain;

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

/** Mapea area_cocina (ver V1__schema_v4.sql, Modulo 11.3). CRUD simple, sin reglas de asociacion. */
@Entity
@Table(name = "area_cocina")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AreaCocina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String estado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public AreaCocina(Integer tenantId, String nombre, String estado) {
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.estado = estado;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    public void actualizar(String nombre, String estado) {
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (estado != null) {
            this.estado = estado;
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
