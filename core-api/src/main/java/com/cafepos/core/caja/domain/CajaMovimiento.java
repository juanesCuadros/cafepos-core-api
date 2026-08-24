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

/** Mapea caja_movimiento (ver V1__schema_v4.sql) — ingreso/egreso manual dentro de una jornada. */
@Entity
@Table(name = "caja_movimiento")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CajaMovimiento {

    public static final String TIPO_INGRESO = "ingreso";
    public static final String TIPO_EGRESO = "egreso";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "jornada_id", nullable = false)
    private Integer jornadaId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    public CajaMovimiento(Integer tenantId, Integer jornadaId, Integer usuarioId, String tipo, BigDecimal monto,
                           String motivo) {
        this.tenantId = tenantId;
        this.jornadaId = jornadaId;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.monto = monto;
        this.motivo = motivo;
        this.fechaHora = OffsetDateTime.now();
    }
}
