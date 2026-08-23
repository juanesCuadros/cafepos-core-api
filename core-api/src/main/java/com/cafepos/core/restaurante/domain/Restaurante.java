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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.OffsetDateTime;

/**
 * Mapea restaurantes (ver V1__schema_v4.sql, Modulo 10.1 de
 * api_10_restaurante.md) — registro unico por tenant (UNIQUE(tenant_id)),
 * sin metodo crear(): la fila la provisiona el flujo de alta del tenant,
 * este modulo solo la lee/actualiza.
 */
@Entity
@Table(name = "restaurantes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "nombre_negocio", nullable = false)
    private String nombreNegocio;

    @Column
    private String nit;

    @Column
    private String direccion;

    @Column
    private String departamento;

    @Column
    private String ciudad;

    @Column
    private String pais;

    @Column
    private String telefono;

    @Column
    private String correo;

    @Column(name = "telefono_representante")
    private String telefonoRepresentante;

    @Column(name = "logo_url")
    private String logoUrl;

    /** JSONB — Hibernate 6 lo (de)serializa via Jackson solo con JdbcTypeCode(SqlTypes.JSON), sin converter a mano. */
    @Column(name = "redes_sociales")
    @JdbcTypeCode(SqlTypes.JSON)
    private RedesSociales redesSociales;

    @Column
    private String descripcion;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo los JsonNullable: ausente = no tocar, presente (aunque sea con
     * valor null) = aplicar, incluso para borrar el campo. redesSociales se
     * reemplaza entero (no hay PATCH parcial dentro del objeto anidado).
     */
    public void actualizar(String nombreNegocio, JsonNullable<String> nit, JsonNullable<String> direccion,
                            JsonNullable<String> departamento, JsonNullable<String> ciudad,
                            JsonNullable<String> pais, JsonNullable<String> telefono, JsonNullable<String> correo,
                            JsonNullable<String> telefonoRepresentante, JsonNullable<String> logoUrl,
                            JsonNullable<RedesSociales> redesSociales, JsonNullable<String> descripcion) {
        if (nombreNegocio != null) {
            this.nombreNegocio = nombreNegocio;
        }
        if (nit.isPresent()) {
            this.nit = nit.get();
        }
        if (direccion.isPresent()) {
            this.direccion = direccion.get();
        }
        if (departamento.isPresent()) {
            this.departamento = departamento.get();
        }
        if (ciudad.isPresent()) {
            this.ciudad = ciudad.get();
        }
        if (pais.isPresent()) {
            this.pais = pais.get();
        }
        if (telefono.isPresent()) {
            this.telefono = telefono.get();
        }
        if (correo.isPresent()) {
            this.correo = correo.get();
        }
        if (telefonoRepresentante.isPresent()) {
            this.telefonoRepresentante = telefonoRepresentante.get();
        }
        if (logoUrl.isPresent()) {
            this.logoUrl = logoUrl.get();
        }
        if (redesSociales.isPresent()) {
            this.redesSociales = redesSociales.get();
        }
        if (descripcion.isPresent()) {
            this.descripcion = descripcion.get();
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
