package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.shared.seguridad.TenantRolConfig;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface TenantRolConfigJpaRepository extends TenantAwareRepository<TenantRolConfig, Integer> {

    @Query(value = "SELECT trc.rol_id AS rol_id, r.nombre AS rol, trc.minutos_inactividad AS minutos_inactividad "
            + "FROM tenant_rol_config trc JOIN rol r ON r.id = trc.rol_id "
            + "ORDER BY r.id", nativeQuery = true)
    List<RolTiempoSesionRow> listar();

    Optional<TenantRolConfig> findByRolId(Integer rolId);
}
