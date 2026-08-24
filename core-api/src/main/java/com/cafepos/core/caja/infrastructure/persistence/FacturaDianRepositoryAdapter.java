package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.caja.domain.FacturaDianRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class FacturaDianRepositoryAdapter implements FacturaDianRepository {

    private final FacturaDianJpaRepository jpaRepository;

    FacturaDianRepositoryAdapter(FacturaDianJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FacturaDian guardar(FacturaDian factura) {
        return jpaRepository.save(factura);
    }

    @Override
    public Optional<FacturaDian> buscarPorVentaId(Integer ventaId) {
        return jpaRepository.findByVentaId(ventaId);
    }
}
