package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.Insumo;
import com.cafepos.core.inventario.domain.InsumoRepository;
import com.cafepos.core.inventario.domain.InsumoResumen;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
class InsumoRepositoryAdapter implements InsumoRepository {

    private final InsumoJpaRepository jpaRepository;

    InsumoRepositoryAdapter(InsumoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Insumo guardar(Insumo insumo) {
        return jpaRepository.save(insumo);
    }

    @Override
    public Optional<Insumo> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<InsumoResumen> listar(Integer categoriaInsumoId, String estado, String estadoStock, String q) {
        return jpaRepository.listar(categoriaInsumoId, estado, estadoStock, q).stream()
                .map(row -> new InsumoResumen(row.getId(), row.getCodigo(), row.getNombre(),
                        row.getCategoriaInsumoId(), row.getCategoriaInsumoNombre(), row.getUnidadMedida(),
                        row.getStockActual(), row.getStockMinimo(), row.getCostoActual(), row.getValorTotal(),
                        row.getEstadoStock(), row.getEstado(), row.getFechaRegistro().atOffset(ZoneOffset.UTC)))
                .toList();
    }

    @Override
    public boolean tieneMovimientosAsociados(Integer insumoId) {
        return jpaRepository.tieneMovimientosAsociados(insumoId);
    }

    @Override
    public void eliminar(Insumo insumo) {
        jpaRepository.delete(insumo);
    }
}
