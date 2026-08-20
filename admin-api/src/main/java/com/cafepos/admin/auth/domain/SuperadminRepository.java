package com.cafepos.admin.auth.domain;

import java.util.Optional;

/** Puerto del dominio — implementado por infrastructure.persistence via Spring Data. */
public interface SuperadminRepository {

    long count();

    Optional<Superadmin> findByCorreo(String correo);

    Superadmin save(Superadmin superadmin);
}
