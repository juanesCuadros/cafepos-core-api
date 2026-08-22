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

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Mapea combo (ver V1__schema_v4.sql, Modulo 4.3 de api_04_productos_menu.md). */
@Entity
@Table(name = "combo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Combo {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String codigo;

    @Column
    private String imagen;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = false)
    private String estado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Combo(Integer tenantId, String nombre, String descripcion, String imagen, BigDecimal precio,
                 String estado) {
        this.tenantId = tenantId;
        this.codigo = "";
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.precio = precio;
        this.estado = estado != null ? estado : ESTADO_ACTIVO;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver ComboService.crear). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo descripcion/imagen (JsonNullable): ausente = no tocar, presente
     * (aunque sea con valor null) = aplicar, incluso para borrar el campo.
     * NO toca grupos — eso vive exclusivamente en los sub-endpoints de
     * grupos (ver ComboService), el PATCH general los ignora por completo.
     */
    public void actualizar(String nombre, JsonNullable<String> descripcion, JsonNullable<String> imagen,
                            BigDecimal precio, String estado) {
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (descripcion.isPresent()) {
            this.descripcion = descripcion.get();
        }
        if (imagen.isPresent()) {
            this.imagen = imagen.get();
        }
        if (precio != null) {
            this.precio = precio;
        }
        if (estado != null) {
            this.estado = estado;
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
