package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.NotaCredito;
import com.cafepos.core.caja.domain.NotaCreditoRepository;
import org.springframework.stereotype.Repository;

@Repository
class NotaCreditoRepositoryAdapter implements NotaCreditoRepository {

    private final NotaCreditoJpaRepository jpaRepository;

    NotaCreditoRepositoryAdapter(NotaCreditoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public NotaCredito guardar(NotaCredito notaCredito) {
        return jpaRepository.save(notaCredito);
    }

    @Override
    public boolean existePorFacturaId(Integer facturaId) {
        return jpaRepository.existsByFacturaId(facturaId);
    }
}
