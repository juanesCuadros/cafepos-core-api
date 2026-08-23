package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.Restaurante;
import com.cafepos.core.restaurante.domain.RestauranteRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class RestauranteRepositoryAdapter implements RestauranteRepository {

    private final RestauranteJpaRepository jpaRepository;

    RestauranteRepositoryAdapter(RestauranteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Restaurante guardar(Restaurante restaurante) {
        return jpaRepository.save(restaurante);
    }

    @Override
    public Optional<Restaurante> buscarPorTenantActual() {
        return jpaRepository.findAll().stream().findFirst();
    }
}
