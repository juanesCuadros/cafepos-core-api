-- Confirmado en produccion (01-sep-2026): una condicion de carrera en
-- PedidoService.abrirDeMesa (chequeo de "mesa libre" y creacion del pedido
-- sin bloqueo) permitia que dos requests casi simultaneos crearan dos
-- pedidos "abierto" para la misma mesa. Eso rompia GET /operacion/mesas
-- entero (NonUniqueResultException en PedidoRepositoryAdapter.buscarActivoPorMesa,
-- que esperaba como mucho un pedido no-cerrado por mesa) para el tenant
-- completo, no solo esa mesa.

-- Paso 1: arreglar datos ya escritos que violan la invariante "maximo un
-- pedido no-cerrado por mesa" -- cierra todos menos el mas reciente por
-- mesa. Sin esto, crear el indice unico de abajo fallaria de inmediato
-- contra cualquier tenant que ya tenga el problema.
WITH duplicados AS (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY tenant_id, mesa_id ORDER BY fecha_apertura DESC) AS orden
    FROM pedido
    WHERE mesa_id IS NOT NULL AND estado <> 'cerrado'
)
UPDATE pedido
SET estado = 'cerrado', fecha_cierre = now()
WHERE id IN (SELECT id FROM duplicados WHERE orden > 1);

-- Paso 2: indice unico parcial -- garantiza a nivel de base de datos que
-- nunca puede volver a coexistir mas de un pedido no-cerrado para la misma
-- mesa, sin importar que tan rapido lleguen dos requests concurrentes.
-- PostgreSQL rechaza el segundo INSERT con una violacion de constraint en
-- vez de dejarlo pasar -- el codigo de aplicacion (el chequeo previo en
-- abrirDeMesa) sigue siendo la primera linea de defensa para dar un error
-- legible (MesaOcupadaException), este indice es la garantia real que no
-- depende de que ese chequeo gane la carrera.
CREATE UNIQUE INDEX idx_pedido_mesa_activo_unico
    ON pedido (tenant_id, mesa_id)
    WHERE mesa_id IS NOT NULL AND estado <> 'cerrado';
