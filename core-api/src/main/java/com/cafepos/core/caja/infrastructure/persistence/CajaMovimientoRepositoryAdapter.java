package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.CajaMovimiento;
import com.cafepos.core.caja.domain.CajaMovimientoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class CajaMovimientoRepositoryAdapter implements CajaMovimientoRepository {

    private final CajaMovimientoJpaRepository jpaRepository;

    CajaMovimientoRepositoryAdapter(CajaMovimientoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CajaMovimiento guardar(CajaMovimiento movimiento) {
        return jpaRepository.save(movimiento);
    }

    @Override
    public List<CajaMovimiento> listarDeJornada(Integer jornadaId) {
        return jpaRepository.findByJornadaIdOrderByFechaHoraAsc(jornadaId);
    }
}
