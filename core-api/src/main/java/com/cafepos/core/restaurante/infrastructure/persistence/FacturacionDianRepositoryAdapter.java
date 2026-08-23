package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.FacturacionDianRepository;
import com.cafepos.core.restaurante.domain.FacturacionDianResolucion;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class FacturacionDianRepositoryAdapter implements FacturacionDianRepository {

    private final FacturacionDianJpaRepository jpaRepository;

    FacturacionDianRepositoryAdapter(FacturacionDianJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<FacturacionDianResolucion> buscarVigente() {
        return jpaRepository.findTopByOrderByIdDesc();
    }

    @Override
    public Optional<String> buscarEstadoConexion() {
        return jpaRepository.buscarEstadoConexionDian();
    }
}
