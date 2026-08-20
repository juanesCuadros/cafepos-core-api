package com.cafepos.admin.auth.domain;

import java.util.Optional;

/** Puerto del dominio — implementado por infrastructure.persistence via Spring Data. */
public interface SuperadminRefreshTokenRepository {

    Optional<SuperadminRefreshToken> findByTokenHash(String tokenHash);

    SuperadminRefreshToken save(SuperadminRefreshToken token);
}
