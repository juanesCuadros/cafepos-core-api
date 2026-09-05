package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.Restaurante;
import com.cafepos.admin.negocios.domain.RestauranteRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestauranteJpaRepository extends JpaRepository<Restaurante, Integer>, RestauranteRepository {

    @Override
    Optional<Restaurante> findByTenantId(Integer tenantId);
}
