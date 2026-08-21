package com.cafepos.core.shared.seguridad;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRefreshTokenRepository extends JpaRepository<UsuarioRefreshToken, Integer> {

    Optional<UsuarioRefreshToken> findByTokenHash(String tokenHash);
}
