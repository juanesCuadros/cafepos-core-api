package com.cafepos.admin.auth.infrastructure.persistence;

import com.cafepos.admin.auth.domain.SuperadminRefreshToken;
import com.cafepos.admin.auth.domain.SuperadminRefreshTokenRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperadminRefreshTokenJpaRepository
        extends JpaRepository<SuperadminRefreshToken, Integer>, SuperadminRefreshTokenRepository {

    @Override
    Optional<SuperadminRefreshToken> findByTokenHash(String tokenHash);
}
