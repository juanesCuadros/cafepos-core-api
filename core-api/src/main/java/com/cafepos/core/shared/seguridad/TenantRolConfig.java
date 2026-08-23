package com.cafepos.core.shared.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Mapea tenant_rol_config: minutos de inactividad de sesion tolerados, por rol. */
@Entity
@Table(name = "tenant_rol_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TenantRolConfig {

    @Id
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "rol_id", nullable = false)
    private Integer rolId;

    @Column(name = "minutos_inactividad", nullable = false)
    private int minutosInactividad;

    /** PATCH /roles/{id}/tiempo-sesion (ver com.cafepos.core.configuracion, Modulo 11.3). */
    public void actualizarMinutos(int minutosInactividad) {
        this.minutosInactividad = minutosInactividad;
    }
}
