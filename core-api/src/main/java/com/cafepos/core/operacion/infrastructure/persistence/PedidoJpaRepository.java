package com.cafepos.core.operacion.infrastructure.persistence;

import com.cafepos.core.operacion.domain.Pedido;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.List;
import java.util.Optional;

interface PedidoJpaRepository extends TenantAwareRepository<Pedido, Integer> {

    /**
     * List, NO Optional/single-result — bug real confirmado en produccion
     * (01-sep-2026): un Optional/getSingleResult() tira NonUniqueResultException
     * en cuanto una mesa tiene mas de un pedido no-cerrado, tumbando TODO
     * GET /operacion/mesas para el tenant entero (MesasPanelService.aMesaPanel
     * itera todas las mesas, una sola con este problema rompe la respuesta
     * completa). La invariante real ("maximo un pedido activo por mesa") la
     * garantiza ahora un indice unico parcial (ver V29__pedido_mesa_activo_unico.sql)
     * — este metodo solo necesita ser resistente a que esa invariante haya
     * podido romperse ANTES de que existiera el indice (datos ya escritos).
     * OrderByFechaAperturaDesc: si por algun motivo igual llegan a coexistir
     * dos, el mas reciente es la interpretacion correcta de "cual esta activo
     * de verdad" — mismo criterio de siempre-ORDER-BY-explicito que
     * findByEstadoInOrderByIdAsc de abajo.
     */
    List<Pedido> findByMesaIdAndEstadoNotOrderByFechaAperturaDesc(Integer mesaId, String estado);

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
