package com.cafepos.core.compras.infrastructure.persistence;

import com.cafepos.core.compras.domain.CompraDetalle;
import com.cafepos.core.compras.domain.CompraDetalleItemVista;
import com.cafepos.core.compras.domain.CompraDetalleRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
class CompraDetalleRepositoryAdapter implements CompraDetalleRepository {

    private final CompraDetalleJpaRepository jpaRepository;

    CompraDetalleRepositoryAdapter(CompraDetalleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CompraDetalle guardar(CompraDetalle detalle) {
        return jpaRepository.save(detalle);
    }

    @Override
    public List<CompraDetalle> listarPorCompraId(Integer compraId) {
        return jpaRepository.findByCompraId(compraId);
    }

    @Override
    public List<CompraDetalleItemVista> listarVistaPorCompraId(Integer compraId) {
        return jpaRepository.listarVistaPorCompraId(compraId).stream()
                .map(row -> new CompraDetalleItemVista(row.getId(), row.getInsumoId(), row.getInsumoCodigo(),
                        row.getInsumoNombre(), row.getUnidadMedida(), row.getCantidad(), row.getCostoUnitario(),
                        row.getNumeroLote(), row.getFechaVencimiento(), row.getSubtotal()))
                .toList();
    }

    @Override
    public Optional<BigDecimal> buscarCostoUnitarioMasRecientePorInsumo(Integer insumoId, Integer compraIdExcluir) {
        return jpaRepository.buscarCostoUnitarioMasRecientePorInsumo(insumoId, compraIdExcluir).stream().findFirst();
    }
}
