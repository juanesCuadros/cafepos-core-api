package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.configuracion.domain.ConfiguracionSistema;
import com.cafepos.core.configuracion.domain.ConfiguracionSistemaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class ConfiguracionSistemaRepositoryAdapter implements ConfiguracionSistemaRepository {

    private final ConfiguracionSistemaJpaRepository jpaRepository;

    ConfiguracionSistemaRepositoryAdapter(ConfiguracionSistemaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ConfiguracionSistema guardar(ConfiguracionSistema configuracionSistema) {
        return jpaRepository.save(configuracionSistema);
    }

    @Override
    public Optional<ConfiguracionSistema> buscarPorTenantActual() {
        return jpaRepository.findAll().stream().findFirst();
    }
}
