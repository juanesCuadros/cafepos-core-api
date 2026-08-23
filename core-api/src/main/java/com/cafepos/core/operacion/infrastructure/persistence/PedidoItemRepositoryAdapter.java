package com.cafepos.core.operacion.infrastructure.persistence;

import com.cafepos.core.operacion.domain.PedidoItem;
import com.cafepos.core.operacion.domain.PedidoItemComboSeleccion;
import com.cafepos.core.operacion.domain.PedidoItemRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class PedidoItemRepositoryAdapter implements PedidoItemRepository {

    private final PedidoItemJpaRepository jpaRepository;
    private final PedidoItemComboSeleccionJpaRepository seleccionJpaRepository;

    PedidoItemRepositoryAdapter(PedidoItemJpaRepository jpaRepository,
                                 PedidoItemComboSeleccionJpaRepository seleccionJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.seleccionJpaRepository = seleccionJpaRepository;
    }

    @Override
    public PedidoItem guardar(PedidoItem item) {
        return jpaRepository.save(item);
    }

    @Override
    public Optional<PedidoItem> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PedidoItem> listarDePedido(Integer pedidoId) {
        return jpaRepository.findByPedidoId(pedidoId);
    }

    @Override
    public void eliminar(PedidoItem item) {
        jpaRepository.delete(item);
    }

    @Override
    public void guardarSeleccion(PedidoItemComboSeleccion seleccion) {
        seleccionJpaRepository.save(seleccion);
    }
}
