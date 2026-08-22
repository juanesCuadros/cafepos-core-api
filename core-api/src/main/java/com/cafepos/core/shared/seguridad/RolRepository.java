package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.TenantAwareRepository;

public interface RolRepository extends TenantAwareRepository<Rol, Integer> {
}
