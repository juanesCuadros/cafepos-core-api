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
import org.openapitools.jackson.nullable.JsonNullable;

/** Mapea categoria (ver V1__schema_v4.sql, Modulo 4.2 de api_04_productos_menu.md). */
@Entity
@Table(name = "categoria")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Categoria {

    public static final String ESTADO_ACTIVA = "activa";
    public static final String ESTADO_INACTIVA = "inactiva";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column
    private String icono;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false)
    private String estado;

    public Categoria(Integer tenantId, String icono, String nombre, String descripcion,
                      Integer orden, String estado) {
        this.tenantId = tenantId;
        this.icono = icono;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.orden = orden != null ? orden : 0;
        this.estado = estado != null ? estado : ESTADO_ACTIVA;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo descripcion (JsonNullable): ausente = no tocar, presente (aunque
     * sea con valor null) = aplicar, incluso para borrar el campo.
     */
    public void actualizar(String icono, String nombre, JsonNullable<String> descripcion, Integer orden,
                            String estado) {
        if (icono != null) {
            this.icono = icono;
        }
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (descripcion.isPresent()) {
            this.descripcion = descripcion.get();
        }
        if (orden != null) {
            this.orden = orden;
        }
        if (estado != null) {
            this.estado = estado;
        }
    }
}
