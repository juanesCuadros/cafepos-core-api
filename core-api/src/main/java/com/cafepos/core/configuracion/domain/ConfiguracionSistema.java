package com.cafepos.core.configuracion.domain;

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

/**
 * Mapea configuracion_sistema (ver V1__schema_v4.sql + V16, Modulo 11.3) —
 * registro unico por tenant, provisionado al dar de alta el tenant (mismo
 * patron que Restaurante), este modulo solo la lee/actualiza.
 */
@Entity
@Table(name = "configuracion_sistema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfiguracionSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "modo_comanda", nullable = false)
    private String modoComanda;

    @Column(name = "tiempo_limite_prep_min")
    private Integer tiempoLimitePrepMin;

    @Column(name = "propina_tipo", nullable = false)
    private String propinaTipo;

    @Column(name = "propina_porcentaje")
    private BigDecimal propinaPorcentaje;

    @Column(name = "propina_destino")
    private String propinaDestino;

    @Column(name = "propina_pct_mesero")
    private BigDecimal propinaPctMesero;

    @Column(name = "dias_anticipacion_vencim")
    private Integer diasAnticipacionVencim;

    @Column(name = "estado_conexion_dian")
    private String estadoConexionDian;

    @Column(name = "iva_porcentaje")
    private BigDecimal ivaPorcentaje;

    @Column(name = "inc_porcentaje")
    private BigDecimal incPorcentaje;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Actualizacion parcial (PATCH) — un campo en null significa "no tocar",
     * salvo los JsonNullable (todos los campos son nullable en la tabla
     * salvo modo_comanda/propina_tipo, que tienen NOT NULL + DEFAULT).
     */
    public void actualizar(String modoComanda, JsonNullable<Integer> tiempoLimitePrepMin, String propinaTipo,
                            JsonNullable<BigDecimal> propinaPorcentaje, JsonNullable<String> propinaDestino,
                            JsonNullable<BigDecimal> propinaPctMesero, JsonNullable<Integer> diasAnticipacionVencim,
                            JsonNullable<String> estadoConexionDian, JsonNullable<BigDecimal> ivaPorcentaje,
                            JsonNullable<BigDecimal> incPorcentaje) {
        if (modoComanda != null) {
            this.modoComanda = modoComanda;
        }
        if (tiempoLimitePrepMin.isPresent()) {
            this.tiempoLimitePrepMin = tiempoLimitePrepMin.get();
        }
        if (propinaTipo != null) {
            this.propinaTipo = propinaTipo;
        }
        if (propinaPorcentaje.isPresent()) {
            this.propinaPorcentaje = propinaPorcentaje.get();
        }
        if (propinaDestino.isPresent()) {
            this.propinaDestino = propinaDestino.get();
        }
        if (propinaPctMesero.isPresent()) {
            this.propinaPctMesero = propinaPctMesero.get();
        }
        if (diasAnticipacionVencim.isPresent()) {
            this.diasAnticipacionVencim = diasAnticipacionVencim.get();
        }
        if (estadoConexionDian.isPresent()) {
            this.estadoConexionDian = estadoConexionDian.get();
        }
        if (ivaPorcentaje.isPresent()) {
            this.ivaPorcentaje = ivaPorcentaje.get();
        }
        if (incPorcentaje.isPresent()) {
            this.incPorcentaje = incPorcentaje.get();
        }
        this.updatedAt = OffsetDateTime.now();
    }
}
