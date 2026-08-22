package com.cafepos.core.shared.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Mapea rol_permiso: matriz de permisos activos por tenant+rol, editable por el Jefe. */
@Entity
@Table(name = "rol_permiso")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolPermiso {

    @Id
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "rol_id", nullable = false)
    private Integer rolId;

    @Column(name = "permiso_id", nullable = false)
    private Integer permisoId;

    @Column(nullable = false)
    private boolean activo;
}
