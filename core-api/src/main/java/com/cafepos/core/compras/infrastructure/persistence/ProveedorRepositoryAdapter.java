package com.cafepos.core.compras.infrastructure.persistence;

import com.cafepos.core.compras.domain.Proveedor;
import com.cafepos.core.compras.domain.ProveedorRepository;
import com.cafepos.core.compras.domain.ProveedorResumen;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class ProveedorRepositoryAdapter implements ProveedorRepository {

    private final ProveedorJpaRepository jpaRepository;

    ProveedorRepositoryAdapter(ProveedorJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Proveedor guardar(Proveedor proveedor) {
        return jpaRepository.save(proveedor);
    }

    @Override
    public Optional<Proveedor> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ProveedorResumen> listar(String estado, String q) {
        return jpaRepository.listar(estado, q).stream()
                .map(row -> new ProveedorResumen(row.getId(), row.getCodigo(), row.getNombre(), row.getNit(),
                        row.getTelefono(), row.getEstado()))
                .toList();
    }

    @Override
    public boolean tieneComprasAsociadas(Integer proveedorId) {
        return jpaRepository.tieneComprasAsociadas(proveedorId);
    }

    @Override
    public void eliminar(Proveedor proveedor) {
        jpaRepository.delete(proveedor);
    }
}
