package com.cafepos.core.compras.infrastructure.persistence;

import com.cafepos.core.compras.domain.Compra;
import com.cafepos.core.compras.domain.CompraListadoItem;
import com.cafepos.core.compras.domain.CompraRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
class CompraRepositoryAdapter implements CompraRepository {

    private final CompraJpaRepository jpaRepository;

    CompraRepositoryAdapter(CompraJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Compra guardar(Compra compra) {
        return jpaRepository.save(compra);
    }

    @Override
    public Optional<Compra> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CompraListadoItem> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer proveedorId,
                                           String formaPago, String estado) {
        return jpaRepository.listar(fechaInicio, fechaFin, proveedorId, formaPago, estado).stream()
                .map(row -> new CompraListadoItem(row.getId(), row.getCodigo(), row.getFecha(), row.getProveedorId(),
                        row.getProveedorNombre(), row.getUsuarioId(), row.getUsuarioNombre(), row.getFormaPago(),
                        row.getEstado(), row.getTotal()))
                .toList();
    }
}
