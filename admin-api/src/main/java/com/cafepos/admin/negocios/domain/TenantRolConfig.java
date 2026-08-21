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

/** Mapea tenant_rol_config: minutos de inactividad de sesion, por rol. */
@Entity
@Table(name = "tenant_rol_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TenantRolConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "rol_id", nullable = false)
    private Integer rolId;

    @Column(name = "minutos_inactividad", nullable = false)
    private int minutosInactividad;

    public TenantRolConfig(Integer tenantId, Integer rolId, int minutosInactividad) {
        this.tenantId = tenantId;
        this.rolId = rolId;
        this.minutosInactividad = minutosInactividad;
    }
}
