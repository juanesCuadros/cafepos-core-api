package com.cafepos.core.operacion.infrastructure.persistence;

import com.cafepos.core.operacion.domain.Pedido;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.List;
import java.util.Optional;

interface PedidoJpaRepository extends TenantAwareRepository<Pedido, Integer> {

    Optional<Pedido> findByMesaIdAndEstadoNot(Integer mesaId, String estado);

    /**
     * OrderByIdAsc explicito a proposito (ver conversacion "Confirmar orden de
     * KDS"): id autoincremental como proxy barato y determinista de "orden de
     * llegada" mientras no exista una columna real de cuando se envio la
     * comanda (pedido.fecha_envio, fuera de alcance por ahora). Antes de esto
     * no habia NINGUN ORDER BY explicito - el orden dependia de lo que
     * Postgres devolviera para el WHERE, no garantizado por el motor.
     */
    List<Pedido> findByEstadoInOrderByIdAsc(List<String> estados);
}
