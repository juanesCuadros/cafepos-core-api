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

import java.time.OffsetDateTime;

/** Mapea mesa (ver V1__schema_v4.sql, Modulo 10.2 de api_10_restaurante.md). */
@Entity
@Table(name = "mesa")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mesa {

    public static final String ESTADO_LIBRE = "libre";
    public static final String ESTADO_OCUPADA = "ocupada";
    public static final String ESTADO_LISTA_COBRAR = "lista_cobrar";
    public static final String ESTADO_DESHABILITADA = "deshabilitada";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "zona_id", nullable = false)
    private Integer zonaId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private int capacidad;

    @Column(nullable = false)
    private String estado;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Mesa(Integer tenantId, Integer zonaId, String numero, Integer capacidad, String estado) {
        this.tenantId = tenantId;
        this.zonaId = zonaId;
        this.codigo = "";
        this.numero = numero;
        this.capacidad = capacidad != null ? capacidad : 1;
        this.estado = estado != null ? estado : ESTADO_LIBRE;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver ZonaService.crearMesa). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar".
     * Ninguno de estos campos es nullable en la tabla mesa, por eso ninguno
     * usa JsonNullable (a diferencia de Zona.icono).
     *
     * PENDIENTE (a proposito, NO implementado todavia): el contrato pide un
     * 409 "no se puede deshabilitar - esta mesa tiene un pedido activo" al
     * intentar poner estado=deshabilitada. El modulo Operacion (donde vive
     * el concepto de "pedido activo") todavia no existe en este proyecto
     * por lo que hoy no hay forma de que una mesa tenga un pedido activo
     * real - la validacion es imposible de disparar y se deja sin
     * implementar hasta que Operacion exista (ver conversacion de
     * implementacion del modulo Restaurante Parte 2).
     */
    public void actualizar(Integer zonaId, String numero, Integer capacidad, String estado) {
        if (zonaId != null) {
            this.zonaId = zonaId;
        }
        if (numero != null) {
            this.numero = numero;
        }
        if (capacidad != null) {
            this.capacidad = capacidad;
        }
        if (estado != null) {
            this.estado = estado;
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
