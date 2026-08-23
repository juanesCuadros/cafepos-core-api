package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.LoteVencimiento;
import com.cafepos.core.inventario.domain.VencimientoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class VencimientoRepositoryAdapter implements VencimientoRepository {

    private final VencimientoJpaRepository jpaRepository;

    VencimientoRepositoryAdapter(VencimientoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<LoteVencimiento> listar(String estado, Integer categoriaInsumoId) {
        return jpaRepository.listar(estado, categoriaInsumoId).stream()
                .map(row -> new LoteVencimiento(row.getLoteId(), row.getInsumoId(), row.getInsumoCodigo(),
                        row.getInsumoNombre(), row.getStockActualInsumo(), row.getNumeroLote(),
                        row.getFechaVencimiento(), row.getDiasRestantes(), row.getEstadoCalc()))
                .toList();
    }
}
