package com.cafepos.core.gastos.domain;

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

/**
 * Mapea gasto (ver V1__schema_v4.sql, Modulo 9). metodo_pago es texto
 * libre (VARCHAR(50), sin CHECK ni FK) — NO es la tabla metodo_pago de
 * restaurante/caja, son conceptos distintos aunque el nombre se parezca.
 * updated_at lo mantiene trg_set_updated_at en cada UPDATE — no se toca a
 * mano en actualizar().
 */
@Entity
@Table(name = "gasto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "categoria_gasto_id", nullable = false)
    private Integer categoriaGastoId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "comprobante_imagen")
    private String comprobanteImagen;

    @Column
    private String observaciones;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Gasto(Integer tenantId, Integer categoriaGastoId, Integer usuarioId, String descripcion, BigDecimal monto,
                 String metodoPago, LocalDate fecha, String comprobanteImagen, String observaciones) {
        this.tenantId = tenantId;
        this.categoriaGastoId = categoriaGastoId;
        this.usuarioId = usuarioId;
        this.codigo = "";
        this.descripcion = descripcion;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fecha = fecha;
        this.comprobanteImagen = comprobanteImagen;
        this.observaciones = observaciones;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver GastoService.crear). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Actualizacion parcial (PATCH) — un campo plano en null significa "no
     * tocar"; comprobanteImagen/observaciones son JsonNullable (nullable
     * de negocio real, ver CLAUDE.md).
     */
    public void actualizar(Integer categoriaGastoId, String descripcion, BigDecimal monto, String metodoPago,
                            LocalDate fecha, JsonNullable<String> comprobanteImagen,
                            JsonNullable<String> observaciones) {
        if (categoriaGastoId != null) {
            this.categoriaGastoId = categoriaGastoId;
        }
        if (descripcion != null) {
            this.descripcion = descripcion;
        }
        if (monto != null) {
            this.monto = monto;
        }
        if (metodoPago != null) {
            this.metodoPago = metodoPago;
        }
        if (fecha != null) {
            this.fecha = fecha;
        }
        if (comprobanteImagen.isPresent()) {
            this.comprobanteImagen = comprobanteImagen.get();
        }
        if (observaciones.isPresent()) {
            this.observaciones = observaciones.get();
        }
    }
}
