package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.caja.domain.VentaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
class VentaRepositoryAdapter implements VentaRepository {

    private final VentaJpaRepository jpaRepository;

    VentaRepositoryAdapter(VentaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Venta guardar(Venta venta) {
        return jpaRepository.save(venta);
    }

    @Override
    public Optional<Venta> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Venta> listar(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, Integer metodoPagoId,
                               String estado, Integer cajeroId) {
        return jpaRepository.listar(fechaInicio, fechaFin, metodoPagoId, estado, cajeroId);
    }

    @Override
    public BigDecimal sumaTotalCobradoDeJornada(Integer jornadaId) {
        return jpaRepository.sumaTotalCobradoDeJornada(jornadaId);
    }
}
