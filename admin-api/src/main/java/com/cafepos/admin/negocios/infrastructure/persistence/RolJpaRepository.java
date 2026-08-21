package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.Rol;
import com.cafepos.admin.negocios.domain.RolRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolJpaRepository extends JpaRepository<Rol, Integer>, RolRepository {

    @Override
    Optional<Rol> findByNombre(String nombre);
}
