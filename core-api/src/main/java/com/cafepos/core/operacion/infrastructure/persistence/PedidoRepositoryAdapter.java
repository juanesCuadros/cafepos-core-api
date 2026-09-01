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
        List<Pedido> activos = jpaRepository.findByMesaIdAndEstadoNotOrderByFechaAperturaDesc(mesaId,
                Pedido.ESTADO_CERRADO);
        return activos.isEmpty() ? Optional.empty() : Optional.of(activos.get(0));
    }

    @Override
    public List<Pedido> listarEnviadosOListos() {
        return jpaRepository.findByEstadoInOrderByIdAsc(List.of(Pedido.ESTADO_ENVIADO, Pedido.ESTADO_LISTO));
    }
}
