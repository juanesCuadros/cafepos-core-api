-- ============================================================================
-- CaféPOS — Migracion V20
-- GET /operacion/kds/pedidos no traia cuando se envio la comanda a cocina —
-- el frontend (kds.types.ts) ya esperaba este dato (enviado_at), pero nunca
-- se guardaba en ningun lado. Sin columna nueva no hay forma de saberlo.
--
-- Nullable a proposito: pedidos ya enviados ANTES de esta migracion no
-- tienen forma de reconstruir retroactivamente cuando se enviaron — quedan
-- en NULL. Todo pedido que se envie a comanda de aca en adelante SI la va a
-- tener (ver Pedido.enviarComanda()).
-- ============================================================================

ALTER TABLE pedido ADD COLUMN fecha_enviado TIMESTAMPTZ;
