package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.Producto;
import com.cafepos.core.productosmenu.domain.ProductoPublico;
import com.cafepos.core.productosmenu.domain.ProductoRepository;
import com.cafepos.core.productosmenu.domain.ProductoResumen;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class ProductoRepositoryAdapter implements ProductoRepository {

    private final ProductoJpaRepository jpaRepository;

    ProductoRepositoryAdapter(ProductoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Producto guardar(Producto producto) {
        return jpaRepository.save(producto);
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ProductoResumen> listar(Integer categoriaId, String estado, String q) {
        return jpaRepository.listar(categoriaId, estado, q).stream()
                .map(row -> new ProductoResumen(row.getId(), row.getCodigo(), row.getNombre(),
                        row.getCategoriaId(), row.getCategoriaNombre(), row.getImagen(),
                        row.getPrecioVenta(), row.getEstado(), row.getVisibilidad(),
                        Boolean.TRUE.equals(row.getManejaReceta())))
                .toList();
    }

    @Override
    public boolean existeAreaCocina(Integer areaCocinaId) {
        return jpaRepository.existeAreaCocina(areaCocinaId);
    }

    @Override
    public List<ProductoPublico> listarVisiblesParaMenuPublico() {
        return jpaRepository.listarVisiblesParaMenuPublico().stream()
                .map(row -> new ProductoPublico(row.getCategoriaNombre(), row.getCategoriaOrden(), row.getNombre(),
                        row.getDescripcion(), row.getPrecioVenta(), row.getImagen()))
                .toList();
    }

    @Override
    public boolean tieneVentasAsociadas(Integer productoId) {
        return jpaRepository.tieneVentasAsociadas(productoId);
    }

    @Override
    public void eliminar(Producto producto) {
        jpaRepository.delete(producto);
    }
}
