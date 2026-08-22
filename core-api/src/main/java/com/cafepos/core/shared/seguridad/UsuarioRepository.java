package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.Optional;

public interface UsuarioRepository extends TenantAwareRepository<Usuario, Integer> {

    Optional<Usuario> findByTenantIdAndCorreo(Integer tenantId, String correo);
}
