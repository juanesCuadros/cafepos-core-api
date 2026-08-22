package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolPermisoRepository extends TenantAwareRepository<RolPermiso, Integer> {

    @Query("SELECT CONCAT(p.modulo, ':', p.accion) FROM RolPermiso rp JOIN Permiso p ON p.id = rp.permisoId "
            + "WHERE rp.tenantId = :tenantId AND rp.rolId = :rolId AND rp.activo = true")
    List<String> findClavesActivas(@Param("tenantId") Integer tenantId, @Param("rolId") Integer rolId);
}
