package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.Optional;

public interface PermisoRepository extends TenantAwareRepository<Permiso, Integer> {

    boolean existsByModuloAndAccion(String modulo, String accion);

    Optional<Permiso> findByModuloAndAccion(String modulo, String accion);
}
