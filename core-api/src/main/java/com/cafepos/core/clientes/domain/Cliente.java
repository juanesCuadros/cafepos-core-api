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
import org.openapitools.jackson.nullable.JsonNullable;
import com.cafepos.core.shared.texto.MascaraDocumento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Mapea cliente (ver V1__schema_v4.sql, Modulo 7 de api_07_clientes.md). */
@Entity
@Table(name = "cliente")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(nullable = false)
    private String codigo;

    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento;

    @Column(name = "numero_documento", nullable = false)
    private String numeroDocumento;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String telefono;

    @Column
    private String correo;

    @Column
    private String direccion;

    @Column(name = "saldo_favor", nullable = false)
    private BigDecimal saldoFavor;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Cliente(Integer tenantId, String tipoDocumento, String numeroDocumento, String nombre, String telefono,
                    String correo, String direccion) {
        this.tenantId = tenantId;
        this.codigo = "";
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.saldoFavor = BigDecimal.ZERO;
        OffsetDateTime ahora = OffsetDateTime.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /** El codigo se arma DESPUES del INSERT, con el id ya asignado (ver ClienteService.crear). */
    public void asignarCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNumeroDocumentoEnmascarado() {
        return MascaraDocumento.enmascarar(numeroDocumento);
    }

    /** Devolucion con item ya preparado — ver ClienteService.acreditarSaldoFavorPorDevolucion. */
    public void acreditarSaldoFavor(BigDecimal monto) {
        this.saldoFavor = this.saldoFavor.add(monto);
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo telefono/correo/direccion (JsonNullable). tipoDocumento y
     * numeroDocumento NO son JsonNullable a proposito: son NOT NULL en la
     * tabla, un PATCH nunca los "borra", solo los reemplaza o los deja
     * igual — la restriccion de "no cambiar si hay ventas" la valida
     * ClienteService ANTES de llamar aca (esta entidad no conoce venta).
     */
    public void actualizar(String nombre, String tipoDocumento, String numeroDocumento,
                            JsonNullable<String> telefono, JsonNullable<String> correo,
                            JsonNullable<String> direccion) {
        if (nombre != null) {
            this.nombre = nombre;
        }
        if (tipoDocumento != null) {
            this.tipoDocumento = tipoDocumento;
        }
        if (numeroDocumento != null) {
            this.numeroDocumento = numeroDocumento;
        }
        if (telefono.isPresent()) {
            this.telefono = telefono.get();
        }
        if (correo.isPresent()) {
            this.correo = correo.get();
        }
        if (direccion.isPresent()) {
            this.direccion = direccion.get();
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
