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

/**
 * Mapea devolucion (ver V1__schema_v4.sql). metodo_reembolso persiste
 * 'pago_original' (valor real de la columna, CHECK constraint) — el
 * contrato api_03_caja.md usa el texto "metodo_original" en sus ejemplos,
 * discrepancia entre contrato y schema real, se sigue el schema (ver
 * conversacion "Facturacion y Devoluciones").
 */
@Entity
@Table(name = "devolucion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Devolucion {

    public static final String METODO_PAGO_ORIGINAL = "pago_original";
    public static final String METODO_SALDO_FAVOR = "saldo_favor";

    public static final String ESTADO_APROBADA = "aprobada";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "venta_id", nullable = false)
    private Integer ventaId;

    @Column(name = "usuario_autoriza_id", nullable = false)
    private Integer usuarioAutorizaId;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "monto_devuelto", nullable = false)
    private BigDecimal montoDevuelto;

    @Column(name = "metodo_reembolso", nullable = false)
    private String metodoReembolso;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private OffsetDateTime fecha;

    /**
     * Se crea directamente 'aprobada' — este prompt no implementa un flujo
     * separado de solicitar-y-luego-aprobar, el PIN de step-up YA es la
     * autorizacion (ver DevolucionService.solicitar).
     */
    public Devolucion(Integer tenantId, Integer ventaId, Integer usuarioAutorizaId, String motivo,
                       BigDecimal montoDevuelto, String metodoReembolso) {
        this.tenantId = tenantId;
        this.ventaId = ventaId;
        this.usuarioAutorizaId = usuarioAutorizaId;
        this.motivo = motivo;
        this.montoDevuelto = montoDevuelto;
        this.metodoReembolso = metodoReembolso;
        this.estado = ESTADO_APROBADA;
        this.fecha = OffsetDateTime.now();
    }
}
