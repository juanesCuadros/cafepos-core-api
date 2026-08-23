package com.cafepos.core.operacion.domain;

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
import java.util.List;

/** Mapea pedido_item (ver V1__schema_v4.sql). */
@Entity
@Table(name = "pedido_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PedidoItem {

    public static final String ESTADO_PENDIENTE = "pendiente";
    public static final String ESTADO_EN_PREPARACION = "en_preparacion";
    public static final String ESTADO_LISTO = "listo";

    /** Orden de avance de estado_preparacion — solo se permite ir hacia adelante (ver transicionar). */
    private static final List<String> ORDEN_ESTADOS = List.of(ESTADO_PENDIENTE, ESTADO_EN_PREPARACION, ESTADO_LISTO);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "pedido_id", nullable = false)
    private Integer pedidoId;

    @Column(name = "producto_id")
    private Integer productoId;

    @Column(name = "combo_id")
    private Integer comboId;

    @Column(name = "area_cocina_id")
    private Integer areaCocinaId;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column
    private String observacion;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "impuesto_unitario")
    private BigDecimal impuestoUnitario;

    @Column(name = "estado_preparacion", nullable = false)
    private String estadoPreparacion;

    public PedidoItem(Integer tenantId, Integer pedidoId, Integer productoId, Integer comboId,
                       Integer areaCocinaId, BigDecimal cantidad, String observacion, BigDecimal precioUnitario) {
        this.tenantId = tenantId;
        this.pedidoId = pedidoId;
        this.productoId = productoId;
        this.comboId = comboId;
        this.areaCocinaId = areaCocinaId;
        this.cantidad = cantidad;
        this.observacion = observacion;
        this.precioUnitario = precioUnitario;
        this.estadoPreparacion = ESTADO_PENDIENTE;
    }

    public BigDecimal subtotal() {
        return precioUnitario.multiply(cantidad);
    }

    /** cantidad nunca se "borra" (siempre requiere un valor real); observacion si es genuinamente nullable de negocio. */
    public void actualizar(BigDecimal cantidad, JsonNullable<String> observacion) {
        if (cantidad != null) {
            this.cantidad = cantidad;
        }
        if (observacion.isPresent()) {
            this.observacion = observacion.get();
        }
    }

    /**
     * Solo hacia adelante (pendiente -> en_preparacion -> listo) — nunca
     * retroceder ni quedarse en el mismo estado. Lanza
     * TransicionEstadoInvalidaException si no es estrictamente hacia
     * adelante (ver KdsService.cambiarEstadoItem).
     */
    public void transicionarEstado(String nuevoEstado) {
        int actual = ORDEN_ESTADOS.indexOf(estadoPreparacion);
        int nuevo = ORDEN_ESTADOS.indexOf(nuevoEstado);
        if (nuevo <= actual) {
            throw new TransicionEstadoInvalidaException(estadoPreparacion, nuevoEstado);
        }
        this.estadoPreparacion = nuevoEstado;
    }

    public boolean estaListo() {
        return ESTADO_LISTO.equals(estadoPreparacion);
    }
}
