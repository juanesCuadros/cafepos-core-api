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

/**
 * Mapea metodo_pago (ver V1__schema_v4.sql, Modulo 10.3 de
 * api_10_restaurante.md). es_efectivo no se expone como campo editable via
 * API — lo provisiona el flujo de alta del tenant, un metodo de pago nuevo
 * creado desde /metodos-pago siempre nace con es_efectivo=false.
 */
@Entity
@Table(name = "metodo_pago")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetodoPago {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String icono;

    @Column(name = "es_efectivo", nullable = false)
    private boolean esEfectivo;

    @Column(nullable = false)
    private String estado;

    /** payment_method_code que exige Factus en payment_details[] al transmitir una factura — ver caja.infrastructure.factus. */
    @Column(name = "codigo_factus")
    private String codigoFactus;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public MetodoPago(Integer tenantId, String nombre, String icono, String estado, String codigoFactus) {
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.icono = icono;
        this.esEfectivo = false;
        this.estado = estado != null ? estado : ESTADO_ACTIVO;
        this.codigoFactus = codigoFactus;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo icono (JsonNullable). La regla especial de Efectivo (no se
     * puede poner estado=inactivo) la valida MetodoPagoService ANTES de
     * llamar aca, no esta entity method — asi el 403 se lanza sin tocar
     * ningun campo, ni siquiera los que si serian validos.
     */
    public void actualizar(JsonNullable<String> icono, String nombre, String estado,
                            JsonNullable<String> codigoFactus) {
        if (icono.isPresent()) {
            this.icono = icono.get();
        }
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (estado != null) {
            this.estado = estado;
        }
        if (codigoFactus.isPresent()) {
            this.codigoFactus = codigoFactus.get();
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
