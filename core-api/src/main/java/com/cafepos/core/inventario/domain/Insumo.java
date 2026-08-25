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
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Mapea insumo (ver V1__schema_v4.sql, Modulo 5.1/5.2 de api_05_inventario.md). */
@Entity
@Table(name = "insumo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Insumo {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "categoria_insumo_id", nullable = false)
    private Integer categoriaInsumoId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;

    @Column(name = "stock_actual", nullable = false)
    private BigDecimal stockActual;

    @Column(name = "stock_minimo")
    private BigDecimal stockMinimo;

    @Column(name = "stock_maximo")
    private BigDecimal stockMaximo;

    @Column(name = "costo_actual", nullable = false)
    private BigDecimal costoActual;

    @Column(name = "fecha_vencim_ref")
    private LocalDate fechaVencimRef;

    @Column(name = "fecha_registro", nullable = false)
    private OffsetDateTime fechaRegistro;

    @Column(nullable = false)
    private String estado;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /** Se crea SIEMPRE con stockActual=0 y costoActual=0 — se llenan recien con la primera compra (ver com.cafepos.core.compras). */
    public Insumo(Integer tenantId, Integer categoriaInsumoId, String nombre, String unidadMedida,
                  BigDecimal stockMinimo, BigDecimal stockMaximo, LocalDate fechaVencimRef, String estado) {
        this.tenantId = tenantId;
        this.categoriaInsumoId = categoriaInsumoId;
        this.codigo = "";
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockActual = BigDecimal.ZERO;
        this.stockMinimo = stockMinimo != null ? stockMinimo : BigDecimal.ZERO;
        this.stockMaximo = stockMaximo;
        this.costoActual = BigDecimal.ZERO;
        this.fechaVencimRef = fechaVencimRef;
        this.fechaRegistro = OffsetDateTime.now();
        this.estado = estado != null ? estado : ESTADO_ACTIVO;
        this.updatedAt = OffsetDateTime.now();
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver InsumoService.crear). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo stockMaximo/fechaVencimRef (JsonNullable). NO incluye
     * stockActual ni costoActual a proposito - esos solo cambian via
     * ajustes/perdidas/conteos/compras, nunca por esta via.
     */
    public void actualizar(String nombre, Integer categoriaInsumoId, String unidadMedida, BigDecimal stockMinimo,
                            JsonNullable<BigDecimal> stockMaximo, JsonNullable<LocalDate> fechaVencimRef,
                            String estado) {
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (categoriaInsumoId != null) {
            this.categoriaInsumoId = categoriaInsumoId;
        }
        if (unidadMedida != null) {
            this.unidadMedida = unidadMedida;
        }
        if (stockMinimo != null) {
            this.stockMinimo = stockMinimo;
        }
        if (stockMaximo.isPresent()) {
            this.stockMaximo = stockMaximo.get();
        }
        if (fechaVencimRef.isPresent()) {
            this.fechaVencimRef = fechaVencimRef.get();
        }
        if (estado != null) {
            this.estado = estado;
        }
        this.updatedAt = OffsetDateTime.now();
    }

    /** Usado por ajustes, perdidas, conteos y compras — siempre con el valor ABSOLUTO nuevo, el caller calcula el delta. */
    public void actualizarStock(BigDecimal nuevoStock) {
        this.stockActual = nuevoStock;
        this.updatedAt = OffsetDateTime.now();
    }

    /** Unico mutador de costo_actual — exclusivo de com.cafepos.core.compras (ver InsumoService.registrarEntradaPorCompra/revertirPorAnulacionCompra). Sobreescribe, nunca promedio ponderado. */
    public void actualizarCostoActual(BigDecimal nuevoCosto) {
        this.costoActual = nuevoCosto;
        this.updatedAt = OffsetDateTime.now();
    }

    /** DELETE con movimientos asociados — soft delete en vez de borrado fisico (ver InsumoService.eliminar). */
    public void marcarInactivo() {
        this.estado = ESTADO_INACTIVO;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Calculado, no persistido — misma formula que el CASE de la query
     * nativa de listado (InsumoJpaRepository), esa SI necesita repetirla en
     * SQL para poder filtrar por estado_stock ahi mismo.
     */
    public String calcularEstadoStock() {
        if (stockActual.compareTo(BigDecimal.ZERO) <= 0) {
            return "agotado";
        }
        BigDecimal minimo = stockMinimo != null ? stockMinimo : BigDecimal.ZERO;
        if (stockActual.compareTo(minimo) < 0) {
            return "bajo_minimo";
        }
        return "normal";
    }

    /** Calculado, no persistido. */
    public BigDecimal calcularValorTotal() {
        return stockActual.multiply(costoActual);
    }
}
