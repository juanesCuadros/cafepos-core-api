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

import java.time.OffsetDateTime;

/** Mapea suscripciones_historial (capa plataforma). */
@Entity
@Table(name = "suscripciones_historial")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SuscripcionesHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "estado_anterior")
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false)
    private String estadoNuevo;

    @Column(name = "plan_anterior_id")
    private Integer planAnteriorId;

    @Column(name = "plan_nuevo_id")
    private Integer planNuevoId;

    @Column(name = "superadmin_id")
    private Integer superadminId;

    @Column
    private String motivo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public SuscripcionesHistorial(Integer tenantId, String estadoAnterior, String estadoNuevo,
                                   Integer superadminId, String motivo) {
        this.tenantId = tenantId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.superadminId = superadminId;
        this.motivo = motivo;
    }

    public SuscripcionesHistorial(Integer tenantId, String estadoAnterior, String estadoNuevo,
                                   Integer planAnteriorId, Integer planNuevoId,
                                   Integer superadminId, String motivo) {
        this.tenantId = tenantId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.planAnteriorId = planAnteriorId;
        this.planNuevoId = planNuevoId;
        this.superadminId = superadminId;
        this.motivo = motivo;
    }
}
