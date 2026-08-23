package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.shared.seguridad.RolPermiso;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface RolPermisoJpaRepository extends TenantAwareRepository<RolPermiso, Integer> {

    /**
     * activo crudo (COALESCE false) — el bypass "todo activo si es Jefe" se
     * aplica en ConfiguracionRolService, no aca. Tenant scoping de rp via
     * RLS, igual que el resto de las queries nativas de este proyecto.
     */
    @Query(value = "SELECT p.id AS permiso_id, p.modulo AS modulo, p.accion AS accion, "
            + "COALESCE(rp.activo, false) AS activo "
            + "FROM permiso p LEFT JOIN rol_permiso rp ON rp.permiso_id = p.id AND rp.rol_id = :rolId "
            + "ORDER BY p.modulo, p.id", nativeQuery = true)
    List<PermisoMatrizRow> obtenerMatrizCruda(@Param("rolId") Integer rolId);

    Optional<RolPermiso> findByTenantIdAndRolIdAndPermisoId(Integer tenantId, Integer rolId, Integer permisoId);
}
