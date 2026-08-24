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
 * Mapea factura_dian (ver V1__schema_v4.sql). En este prompt se crea con
 * estado_dian='pendiente' y se queda ahi — SIN transmision real a Factus
 * (eso es un prompt futuro, y aun ahi sera un stub). cufe/qr_code/
 * motivo_rechazo siempre null aca.
 */
@Entity
@Table(name = "factura_dian")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FacturaDian {

    public static final String ESTADO_PENDIENTE = "pendiente";

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
}
