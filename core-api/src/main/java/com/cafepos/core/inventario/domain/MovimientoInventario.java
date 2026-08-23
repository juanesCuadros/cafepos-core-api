package com.cafepos.core.inventario.domain;

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
 * Mapea movimiento_inventario (ver V1__schema_v4.sql, Modulo 5.3 de
 * api_05_inventario.md) — SIEMPRE con insumo_id (nunca producto_id) desde
 * este modulo, el CHECK de la tabla exige exactamente uno de los dos.
 *
 * cantidad viaja CON SIGNO para los tipos AJUSTE y AJUSTE_CONTEO (positivo
 * = entrada/aumento, negativo = salida/disminucion) porque tipo='ajuste'
 * no distingue direccion por si solo (ver AjusteService — "entrada"/
 * "salida" del request es solo direccion, nunca se persiste como tipo).
 * Para PERDIDA cantidad es positiva (la cantidad perdida) — tipo='perdida'
 * ya deja la direccion sin ambiguedad.
 */
@Entity
@Table(name = "movimiento_inventario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovimientoInventario {

    public static final String TIPO_AJUSTE = "ajuste";
    public static final String TIPO_PERDIDA = "perdida";
    public static final String TIPO_AJUSTE_CONTEO = "ajuste_conteo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "insumo_id")
    private Integer insumoId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(name = "motivo_origen")
    private String motivoOrigen;

    @Column(name = "referencia_tipo")
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private Integer referenciaId;

    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    public MovimientoInventario(Integer tenantId, Integer insumoId, Integer usuarioId, String tipo,
                                 BigDecimal cantidad, String motivoOrigen, String referenciaTipo,
                                 Integer referenciaId) {
        this.tenantId = tenantId;
        this.insumoId = insumoId;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.motivoOrigen = motivoOrigen;
        this.referenciaTipo = referenciaTipo;
        this.referenciaId = referenciaId;
        this.fechaHora = OffsetDateTime.now();
    }

    /** El ajuste manual no tiene tabla propia — referencia_id se autorreferencia con el id ya asignado (ver AjusteService). */
    public void autorreferenciar() {
        this.referenciaId = this.id;
    }
}
