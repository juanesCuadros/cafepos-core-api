package com.cafepos.core.clientes.domain;

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
 * Mapea cliente_saldo_movimiento (ver V1__schema_v4.sql) — hasta ahora
 * solo se leia via proyeccion nativa (ver SaldoMovimientoItemRow, GET
 * /clientes/{id}/saldo-movimientos); esta es la primera escritura real,
 * disparada por com.cafepos.core.caja al acreditar saldo a favor en una
 * devolucion (ver ClienteService.acreditarSaldoFavorPorDevolucion).
 */
@Entity
@Table(name = "cliente_saldo_movimiento")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClienteSaldoMovimiento {

    public static final String TIPO_CREDITO = "credito";
    public static final String TIPO_DEBITO = "debito";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(name = "origen_tipo")
    private String origenTipo;

    @Column(name = "origen_id")
    private Integer origenId;

    @Column(nullable = false)
    private OffsetDateTime fecha;

    @Column
    private String descripcion;

    public ClienteSaldoMovimiento(Integer tenantId, Integer clienteId, Integer usuarioId, String tipo,
                                   BigDecimal monto, String origenTipo, Integer origenId, String descripcion) {
        this.tenantId = tenantId;
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.monto = monto;
        this.origenTipo = origenTipo;
        this.origenId = origenId;
        this.fecha = OffsetDateTime.now();
        this.descripcion = descripcion;
    }
}
