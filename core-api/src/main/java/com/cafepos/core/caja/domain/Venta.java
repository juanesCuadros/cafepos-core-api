package com.cafepos.core.caja.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Mapea venta (ver V1__schema_v4.sql). */
@Entity
@Table(name = "venta")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venta {

    public static final String ESTADO_COBRADO = "cobrado";
    public static final String ESTADO_ANULADO = "anulado";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "pedido_id", nullable = false)
    private Integer pedidoId;

    @Column(name = "jornada_id", nullable = false)
    private Integer jornadaId;

    @Column(name = "cliente_id")
    private Integer clienteId;

    @Column(name = "cajero_id", nullable = false)
    private Integer cajeroId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(name = "descuento_total", nullable = false)
    private BigDecimal descuentoTotal;

    @Column(nullable = false)
    private BigDecimal impuestos;

    @Column(nullable = false)
    private BigDecimal propina;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    public Venta(Integer tenantId, Integer pedidoId, Integer jornadaId, Integer clienteId, Integer cajeroId,
                 BigDecimal subtotal, BigDecimal descuentoTotal, BigDecimal impuestos, BigDecimal propina,
                 BigDecimal total) {
        this.tenantId = tenantId;
        this.pedidoId = pedidoId;
        this.jornadaId = jornadaId;
        this.clienteId = clienteId;
        this.cajeroId = cajeroId;
        this.codigo = "";
        this.subtotal = subtotal;
        this.descuentoTotal = descuentoTotal;
        this.impuestos = impuestos;
        this.propina = propina;
        this.total = total;
        this.estado = ESTADO_COBRADO;
        this.fechaHora = OffsetDateTime.now();
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver VentaService.cobrar). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    public boolean estaCobrada() {
        return ESTADO_COBRADO.equals(estado);
    }

    public void anular() {
        this.estado = ESTADO_ANULADO;
    }
}
