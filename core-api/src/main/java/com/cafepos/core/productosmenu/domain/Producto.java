package com.cafepos.core.productosmenu.domain;

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
import java.time.OffsetDateTime;

/** Mapea producto (ver V1__schema_v4.sql, Modulo 4.1 de api_04_productos_menu.md). */
@Entity
@Table(name = "producto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Producto {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_INACTIVO = "inactivo";
    public static final String ESTADO_AGOTADO = "agotado";

    public static final String VISIBILIDAD_VISIBLE = "visible";
    public static final String VISIBILIDAD_OCULTO = "oculto";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "categoria_id", nullable = false)
    private Integer categoriaId;

    @Column(name = "area_cocina_id")
    private Integer areaCocinaId;

    @Column(nullable = false)
    private String codigo;

    @Column
    private String imagen;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(name = "precio_venta", nullable = false)
    private BigDecimal precioVenta;

    @Column(name = "costo_estimado")
    private BigDecimal costoEstimado;

    @Column(name = "tasa_impuesto")
    private String tasaImpuesto;

    @Column(name = "maneja_receta", nullable = false)
    private boolean manejaReceta;

    @Column(name = "maneja_inventario", nullable = false)
    private boolean manejaInventario;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    @Column(name = "stock_actual")
    private BigDecimal stockActual;

    @Column(name = "stock_minimo")
    private BigDecimal stockMinimo;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String visibilidad;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Producto(Integer tenantId, Integer categoriaId, Integer areaCocinaId, String nombre,
                     String descripcion, String imagen, BigDecimal precioVenta, BigDecimal costoEstimado,
                     String tasaImpuesto, Boolean manejaReceta, Boolean manejaInventario, String unidadMedida,
                     BigDecimal stockMinimo, String estado, String visibilidad) {
        this.tenantId = tenantId;
        this.categoriaId = categoriaId;
        this.areaCocinaId = areaCocinaId;
        this.codigo = "";
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.precioVenta = precioVenta;
        this.costoEstimado = costoEstimado;
        this.tasaImpuesto = tasaImpuesto;
        this.manejaReceta = manejaReceta != null && manejaReceta;
        this.manejaInventario = manejaInventario != null && manejaInventario;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
        this.estado = estado != null ? estado : ESTADO_ACTIVO;
        this.visibilidad = visibilidad != null ? visibilidad : VISIBILIDAD_VISIBLE;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver ProductoService.crear). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo descripcion/imagen/costoEstimado/areaCocinaId/tasaImpuesto/
     * unidadMedida/stockMinimo (JsonNullable): ausente = no tocar, presente
     * (aunque sea con valor null) = aplicar, incluso para borrar el campo.
     */
    public void actualizar(Integer categoriaId, JsonNullable<Integer> areaCocinaId, String nombre,
                            JsonNullable<String> descripcion, JsonNullable<String> imagen, BigDecimal precioVenta,
                            JsonNullable<BigDecimal> costoEstimado, JsonNullable<String> tasaImpuesto,
                            Boolean manejaReceta, Boolean manejaInventario, JsonNullable<String> unidadMedida,
                            JsonNullable<BigDecimal> stockMinimo, String estado, String visibilidad) {
        if (categoriaId != null) {
            this.categoriaId = categoriaId;
        }
        if (areaCocinaId.isPresent()) {
            this.areaCocinaId = areaCocinaId.get();
        }
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (descripcion.isPresent()) {
            this.descripcion = descripcion.get();
        }
        if (imagen.isPresent()) {
            this.imagen = imagen.get();
        }
        if (precioVenta != null) {
            this.precioVenta = precioVenta;
        }
        if (costoEstimado.isPresent()) {
            this.costoEstimado = costoEstimado.get();
        }
        if (tasaImpuesto.isPresent()) {
            this.tasaImpuesto = tasaImpuesto.get();
        }
        if (manejaReceta != null) {
            this.manejaReceta = manejaReceta;
        }
        if (manejaInventario != null) {
            this.manejaInventario = manejaInventario;
        }
        if (unidadMedida.isPresent()) {
            this.unidadMedida = unidadMedida.get();
        }
        if (stockMinimo.isPresent()) {
            this.stockMinimo = stockMinimo.get();
        }
        if (estado != null) {
            this.estado = estado;
        }
        if (visibilidad != null) {
            this.visibilidad = visibilidad;
        }
        this.updatedAt = OffsetDateTime.now();
    }

    /** DELETE con ventas asociadas — soft delete en vez de borrado fisico (ver ProductoService.eliminar). */
    public void marcarInactivo() {
        this.estado = ESTADO_INACTIVO;
        this.updatedAt = OffsetDateTime.now();
    }
}
