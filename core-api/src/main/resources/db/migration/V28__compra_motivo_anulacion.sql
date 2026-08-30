-- ============================================================================
-- CaféPOS — Migracion V28
-- POST /compras/{id}/anular nunca acepto un body — el motivo que el
-- frontend ya obligaba a escribir se mandaba y se ignoraba en silencio, sin
-- persistir en ningun lado (ver INTEGRACION.md hallazgo 3.17). Mismo patron
-- que AnularVentaRequest/GenerarNotaCreditoRequest (Caja), que si lo hacen.
-- ============================================================================

ALTER TABLE compra ADD COLUMN motivo_anulacion VARCHAR(255);
