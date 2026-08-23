package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.ProductoRef;
import com.cafepos.core.productosmenu.domain.Promocion;
import com.cafepos.core.productosmenu.domain.PromocionProducto;
import com.cafepos.core.productosmenu.domain.PromocionRepository;
import com.cafepos.core.productosmenu.domain.PromocionResumen;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class PromocionRepositoryAdapter implements PromocionRepository {

    private final PromocionJpaRepository jpaRepository;
    private final PromocionProductoJpaRepository promocionProductoJpaRepository;

    PromocionRepositoryAdapter(PromocionJpaRepository jpaRepository,
                                PromocionProductoJpaRepository promocionProductoJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.promocionProductoJpaRepository = promocionProductoJpaRepository;
    }

    @Override
    public Promocion guardar(Promocion promocion) {
        return jpaRepository.save(promocion);
    }

    @Override
    public Optional<Promocion> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PromocionResumen> listar() {
        return jpaRepository.listar().stream()
                .map(row -> new PromocionResumen(row.getId(), row.getNombre(), row.getTipoDescuento(),
                        row.getVigenciaInicio(), row.getVigenciaFin(), row.getEstado()))
                .toList();
    }

    @Override
    public void eliminar(Promocion promocion) {
        jpaRepository.delete(promocion);
    }

    @Override
    public void reemplazarProductos(Integer promocionId, Integer tenantId, List<Integer> productoIds) {
        promocionProductoJpaRepository.deleteAll(promocionProductoJpaRepository.findByPromocionId(promocionId));
        List<PromocionProducto> nuevos = productoIds.stream()
                .map(productoId -> new PromocionProducto(tenantId, promocionId, productoId))
                .toList();
        promocionProductoJpaRepository.saveAll(nuevos);
    }

    @Override
    public List<ProductoRef> productosDe(Integer promocionId) {
        return promocionProductoJpaRepository.productosDe(promocionId).stream()
                .map(row -> new ProductoRef(row.getId(), row.getNombre()))
                .toList();
    }

    @Override
    public List<Promocion> listarActivas() {
        return jpaRepository.findByEstado(Promocion.ESTADO_ACTIVA);
    }
}
