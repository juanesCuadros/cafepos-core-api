package com.cafepos.core.operacion.infrastructure.persistence;

import com.cafepos.core.operacion.domain.Pedido;
import com.cafepos.core.operacion.domain.PedidoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class PedidoRepositoryAdapter implements PedidoRepository {

    private final PedidoJpaRepository jpaRepository;

    PedidoRepositoryAdapter(PedidoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        return jpaRepository.save(pedido);
    }

    @Override
    public Optional<Pedido> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Pedido> buscarActivoPorMesa(Integer mesaId) {
        return jpaRepository.findByMesaIdAndEstadoNot(mesaId, Pedido.ESTADO_CERRADO);
    }

    @Override
    public List<Pedido> listarEnviadosOListos() {
        return jpaRepository.findByEstadoInOrderByIdAsc(List.of(Pedido.ESTADO_ENVIADO, Pedido.ESTADO_LISTO));
    }
}
