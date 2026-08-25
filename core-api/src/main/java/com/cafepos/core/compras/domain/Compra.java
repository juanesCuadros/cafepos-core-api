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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Mapea compra (ver V1__schema_v4.sql + V26, que agrego estado='anulada' al CHECK original). */
@Entity
@Table(name = "compra")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Compra {

    public static final String ESTADO_PAGADA = "pagada";
    public static final String ESTADO_PENDIENTE = "pendiente";
    public static final String ESTADO_ANULADA = "anulada";

    public static final String FORMA_PAGO_CONTADO = "contado";
    public static final String FORMA_PAGO_CREDITO = "credito";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "proveedor_id", nullable = false)
    private Integer proveedorId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private String codigo;

    @Column(name = "numero_factura_prov")
    private String numeroFacturaProv;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "forma_pago", nullable = false)
    private String formaPago;

    @Column(nullable = false)
    private String estado;

    @Column
    private String observaciones;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /** estado se deriva de formaPago al nacer — 'pagada' si contado, 'pendiente' si credito (ver DECISIONES YA TOMADAS). */
    public Compra(Integer tenantId, Integer proveedorId, Integer usuarioId, String numeroFacturaProv, LocalDate fecha,
                  String formaPago, String observaciones, BigDecimal total) {
        this.tenantId = tenantId;
        this.proveedorId = proveedorId;
        this.usuarioId = usuarioId;
        this.codigo = "";
        this.numeroFacturaProv = numeroFacturaProv;
        this.fecha = fecha;
        this.formaPago = formaPago;
        this.estado = FORMA_PAGO_CONTADO.equals(formaPago) ? ESTADO_PAGADA : ESTADO_PENDIENTE;
        this.observaciones = observaciones;
        this.total = total;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver CompraService.registrar). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Bloqueo de negocio para anular — SOLO si es de credito Y ya esta
     * pagada. Una de contado, que siempre nace pagada, SIEMPRE se puede
     * anular sin importar su estado — nunca una condicion generica de tipo
     * "esta pagada entonces bloquear" (ver DECISIONES YA TOMADAS).
     */
    public boolean bloqueadaParaAnular() {
        return FORMA_PAGO_CREDITO.equals(formaPago) && ESTADO_PAGADA.equals(estado);
    }

    public void anular() {
        this.estado = ESTADO_ANULADA;
        this.updatedAt = OffsetDateTime.now();
    }

    public void marcarPagada() {
        this.estado = ESTADO_PAGADA;
        this.updatedAt = OffsetDateTime.now();
    }
}
