package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.VentaPromocion;
import com.cafepos.core.caja.domain.VentaPromocionRepository;
import org.springframework.stereotype.Repository;

@Repository
class VentaPromocionRepositoryAdapter implements VentaPromocionRepository {

    private final VentaPromocionJpaRepository jpaRepository;

    VentaPromocionRepositoryAdapter(VentaPromocionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public VentaPromocion guardar(VentaPromocion ventaPromocion) {
        return jpaRepository.save(ventaPromocion);
    }
}
