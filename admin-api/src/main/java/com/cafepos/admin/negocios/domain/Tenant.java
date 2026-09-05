package com.cafepos.admin.negocios.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Mapea tenants (capa plataforma, creada por core-api en V1__schema_v4.sql).
 */
@Entity
@Table(name = "tenants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tenant {

    public static final String ESTADO_PRUEBA = "prueba";
    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_SUSPENDIDO = "suspendido";
    public static final String ESTADO_CANCELADO = "cancelado";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "superadmin_aprobador_id")
    private Integer superadminAprobadorId;

    @Column(name = "plan_id", nullable = false)
    private Integer planId;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private OffsetDateTime fechaRegistro;

    @Column(name = "fecha_proxima_facturacion")
    private LocalDate fechaProximaFacturacion;

    public Tenant(Integer superadminAprobadorId, Integer planId, String slug,
                  String estado, LocalDate fechaProximaFacturacion) {
        this.superadminAprobadorId = superadminAprobadorId;
        this.planId = planId;
        this.slug = slug;
        this.estado = estado;
        this.fechaProximaFacturacion = fechaProximaFacturacion;
    }

    public void suspenderPorPruebaVencida() {
        this.estado = ESTADO_SUSPENDIDO;
    }

    public void suspender() {
        this.estado = ESTADO_SUSPENDIDO;
    }

    public void reactivar(LocalDate proximaFacturacion) {
        this.estado = ESTADO_ACTIVO;
        this.fechaProximaFacturacion = proximaFacturacion;
    }

    public void cancelar() {
        this.estado = ESTADO_CANCELADO;
    }

    public void extenderPrueba(LocalDate nuevaFecha) {
        if (!ESTADO_PRUEBA.equals(this.estado)) {
            throw new OperacionTenantInvalidaException("Solo se puede extender el período de prueba a negocios en estado 'prueba'");
        }
        this.fechaProximaFacturacion = nuevaFecha;
    }

    public void cambiarPlan(Integer nuevoPlanId) {
        this.planId = nuevoPlanId;
    }
}
