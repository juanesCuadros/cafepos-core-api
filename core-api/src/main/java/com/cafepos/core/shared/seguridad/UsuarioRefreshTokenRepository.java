package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.Optional;

public interface UsuarioRefreshTokenRepository extends TenantAwareRepository<UsuarioRefreshToken, Integer> {

    Optional<UsuarioRefreshToken> findByTokenHash(String tokenHash);
}
