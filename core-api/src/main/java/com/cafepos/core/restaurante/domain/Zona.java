package com.cafepos.core.restaurante.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.OffsetDateTime;

/** Mapea zona (ver V1__schema_v4.sql, Modulo 10.2 de api_10_restaurante.md). */
@Entity
@Table(name = "zona")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Zona {

    public static final String ESTADO_ACTIVA = "activa";
    public static final String ESTADO_INACTIVA = "inactiva";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String codigo;

    @Column
    private String icono;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String estado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Zona(Integer tenantId, String icono, String nombre, String estado) {
        this.tenantId = tenantId;
        this.codigo = "";
        this.icono = icono;
        this.nombre = nombre;
        this.estado = estado != null ? estado : ESTADO_ACTIVA;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver ZonaService.crear). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    /** Actualizacion parcial (PATCH) — un campo en null significa "no tocar", salvo icono (JsonNullable). */
    public void actualizar(JsonNullable<String> icono, String nombre, String estado) {
        if (icono.isPresent()) {
            this.icono = icono.get();
        }
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (estado != null) {
            this.estado = estado;
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
