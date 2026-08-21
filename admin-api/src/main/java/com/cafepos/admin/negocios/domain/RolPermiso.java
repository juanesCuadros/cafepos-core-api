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

/** Mapea rol_permiso. Solo se insertan combinaciones activo=true (ver matriz-permisos-default.csv). */
@Entity
@Table(name = "rol_permiso")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "rol_id", nullable = false)
    private Integer rolId;

    @Column(name = "permiso_id", nullable = false)
    private Integer permisoId;

    @Column(nullable = false)
    private boolean activo;

    public RolPermiso(Integer tenantId, Integer rolId, Integer permisoId) {
        this.tenantId = tenantId;
        this.rolId = rolId;
        this.permisoId = permisoId;
        this.activo = true;
    }
}
