package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.Categoria;
import com.cafepos.core.productosmenu.domain.CategoriaRepository;
import com.cafepos.core.productosmenu.domain.CategoriaResumen;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class CategoriaRepositoryAdapter implements CategoriaRepository {

    private final CategoriaJpaRepository jpaRepository;

    CategoriaRepositoryAdapter(CategoriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        return jpaRepository.save(categoria);
    }

    @Override
    public Optional<Categoria> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CategoriaResumen> listarConNumProductos() {
        return jpaRepository.listarConNumProductos().stream()
                .map(row -> new CategoriaResumen(row.getId(), row.getIcono(), row.getNombre(),
                        row.getOrden(), row.getNumProductos(), row.getEstado()))
                .toList();
    }

    @Override
    public long contarProductos(Integer categoriaId) {
        return jpaRepository.contarProductos(categoriaId);
    }

    @Override
    public void eliminar(Categoria categoria) {
        jpaRepository.delete(categoria);
    }
}
