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
 * Mapea caja_jornada (ver V1__schema_v4.sql). RN-011: a lo sumo una jornada
 * 'abierta' por tenant — reforzado por el indice unico parcial
 * idx_caja_jornada_una_abierta, ver CajaJornadaRepository.guardar (captura
 * DataIntegrityViolationException en vez de un SELECT previo).
 */
@Entity
@Table(name = "caja_jornada")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CajaJornada {

    public static final String ESTADO_ABIERTA = "abierta";
    public static final String ESTADO_CERRADA = "cerrada";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "usuario_apertura_id", nullable = false)
    private Integer usuarioAperturaId;

    @Column(name = "usuario_cierre_id")
    private Integer usuarioCierreId;

    @Column(name = "fecha_apertura", nullable = false)
    private OffsetDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private OffsetDateTime fechaCierre;

    @Column(name = "monto_inicial", nullable = false)
    private BigDecimal montoInicial;

    @Column(name = "monto_final_sistema")
    private BigDecimal montoFinalSistema;

    @Column(name = "monto_final_fisico")
    private BigDecimal montoFinalFisico;

    @Column
    private BigDecimal diferencia;

    @Column(nullable = false)
    private String estado;

    public CajaJornada(Integer tenantId, Integer usuarioAperturaId, BigDecimal montoInicial) {
        this.tenantId = tenantId;
        this.usuarioAperturaId = usuarioAperturaId;
        this.montoInicial = montoInicial;
        this.fechaApertura = OffsetDateTime.now();
        this.estado = ESTADO_ABIERTA;
    }

    /** diferencia NUNCA bloquea el cierre, sin importar el valor (RN-013) — ver CajaJornadaService.cerrar. */
    public void cerrar(Integer usuarioCierreId, BigDecimal montoFinalSistema, BigDecimal montoFinalFisico) {
        this.usuarioCierreId = usuarioCierreId;
        this.fechaCierre = OffsetDateTime.now();
        this.montoFinalSistema = montoFinalSistema;
        this.montoFinalFisico = montoFinalFisico;
        this.diferencia = montoFinalFisico.subtract(montoFinalSistema);
        this.estado = ESTADO_CERRADA;
    }
}
