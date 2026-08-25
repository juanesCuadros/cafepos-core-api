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

import java.time.OffsetDateTime;

/**
 * Mapea factura_dian (ver V1__schema_v4.sql). Se crea con estado_dian=
 * 'pendiente' y numero_factura LOCAL (ver VentaService.emitirFacturaSiCorresponde)
 * — actualizarConResultadoFactus() la actualiza con los datos REALES de
 * Factus (numero_factura, cufe, qr_code, estado_dian) despues del intento
 * de transmision real, ver FacturaDianTransmisionService. Si Factus nunca
 * responde con exito, la fila se queda tal cual (pendiente/rechazada, sin
 * cufe/qr_code), sin excepcion.
 */
@Entity
@Table(name = "factura_dian")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FacturaDian {

    public static final String ESTADO_PENDIENTE = "pendiente";
    public static final String ESTADO_ACEPTADA = "aceptada";
    public static final String ESTADO_RECHAZADA = "rechazada";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "venta_id", nullable = false)
    private Integer ventaId;

    @Column(name = "resolucion_id")
    private Integer resolucionId;

    @Column(name = "numero_factura", nullable = false)
    private String numeroFactura;

    @Column
    private String cufe;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "estado_dian", nullable = false)
    private String estadoDian;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    @Column(name = "fecha_emision", nullable = false)
    private OffsetDateTime fechaEmision;

    public FacturaDian(Integer tenantId, Integer ventaId, Integer resolucionId, String numeroFactura) {
        this.tenantId = tenantId;
        this.ventaId = ventaId;
        this.resolucionId = resolucionId;
        this.numeroFactura = numeroFactura;
        this.estadoDian = ESTADO_PENDIENTE;
        this.fechaEmision = OffsetDateTime.now();
    }

    /**
     * Unico mutador tras la transmision real a Factus — ver
     * FacturaDianTransmisionService (unico caller). numeroFactura/cufe/qrCode
     * reemplazan los locales por los REALES de Factus. validado=true ->
     * 'aceptada', false -> 'rechazada' (ver DECISIONES YA TOMADAS).
     */
    public void actualizarConResultadoFactus(String numeroFactura, String cufe, String qrCode, boolean validado) {
        this.numeroFactura = numeroFactura;
        this.cufe = cufe;
        this.qrCode = qrCode;
        this.estadoDian = validado ? ESTADO_ACEPTADA : ESTADO_RECHAZADA;
    }
}
