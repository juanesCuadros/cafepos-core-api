package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.MenuDigitalConfig;
import com.cafepos.core.restaurante.domain.MenuDigitalRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class MenuDigitalRepositoryAdapter implements MenuDigitalRepository {

    private final MenuDigitalJpaRepository jpaRepository;

    MenuDigitalRepositoryAdapter(MenuDigitalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MenuDigitalConfig guardar(MenuDigitalConfig config) {
        return jpaRepository.save(config);
    }

    @Override
    public Optional<MenuDigitalConfig> buscarPorTenantActual() {
        return jpaRepository.findAll().stream().findFirst();
    }
}
