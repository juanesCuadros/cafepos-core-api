package com.cafepos.core.compras.domain;

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

/** Mapea proveedor (ver V1__schema_v4.sql). */
@Entity
@Table(name = "proveedor")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Proveedor {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String nit;

    @Column
    private String contacto;

    @Column
    private String telefono;

    @Column
    private String correo;

    @Column
    private String direccion;

    @Column(nullable = false)
    private String estado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Proveedor(Integer tenantId, String nombre, String nit, String contacto, String telefono, String correo,
                      String direccion, String estado) {
        this.tenantId = tenantId;
        this.codigo = "";
        this.nombre = nombre;
        this.nit = nit;
        this.contacto = contacto;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.estado = estado != null ? estado : ESTADO_ACTIVO;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver ProveedorService.crear). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo contacto/telefono/correo/direccion (JsonNullable, ver
     * DECISIONES YA TOMADAS de la conversacion Compras). nombre/nit/estado
     * NO son JsonNullable a proposito.
     */
    public void actualizar(String nombre, String nit, JsonNullable<String> contacto, JsonNullable<String> telefono,
                            JsonNullable<String> correo, JsonNullable<String> direccion, String estado) {
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (nit != null) {
            this.nit = nit;
        }
        if (contacto.isPresent()) {
            this.contacto = contacto.get();
        }
        if (telefono.isPresent()) {
            this.telefono = telefono.get();
        }
        if (correo.isPresent()) {
            this.correo = correo.get();
        }
        if (direccion.isPresent()) {
            this.direccion = direccion.get();
        }
        if (estado != null) {
            this.estado = estado;
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
