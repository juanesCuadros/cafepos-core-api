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

/** Mapea tenant_permiso_config: PIN de step-up por permiso, configurable por tenant. */
@Entity
@Table(name = "tenant_permiso_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TenantPermisoConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "permiso_id", nullable = false)
    private Integer permisoId;

    @Column(name = "requiere_pin", nullable = false)
    private boolean requierePin;

    public TenantPermisoConfig(Integer tenantId, Integer permisoId, boolean requierePin) {
        this.tenantId = tenantId;
        this.permisoId = permisoId;
        this.requierePin = requierePin;
    }
}
