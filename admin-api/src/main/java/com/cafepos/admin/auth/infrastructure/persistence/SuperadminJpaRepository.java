package com.cafepos.admin.auth.infrastructure.persistence;

import com.cafepos.admin.auth.domain.Superadmin;
import com.cafepos.admin.auth.domain.SuperadminRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperadminJpaRepository extends JpaRepository<Superadmin, Integer>, SuperadminRepository {

    @Override
    Optional<Superadmin> findByCorreo(String correo);
}
