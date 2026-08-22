package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.TenantAwareRepository;

public interface PermisoRepository extends TenantAwareRepository<Permiso, Integer> {

    boolean existsByModuloAndAccion(String modulo, String accion);
}
