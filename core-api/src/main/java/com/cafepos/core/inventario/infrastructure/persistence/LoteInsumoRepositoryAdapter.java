package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.LoteInsumo;
import com.cafepos.core.inventario.domain.LoteInsumoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class LoteInsumoRepositoryAdapter implements LoteInsumoRepository {

    private final LoteInsumoJpaRepository jpaRepository;

    LoteInsumoRepositoryAdapter(LoteInsumoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LoteInsumo guardar(LoteInsumo lote) {
        return jpaRepository.save(lote);
    }

    @Override
    public Optional<LoteInsumo> buscarPorCompraDetalleId(Integer compraDetalleId) {
        return jpaRepository.findByCompraDetalleId(compraDetalleId);
    }
}
