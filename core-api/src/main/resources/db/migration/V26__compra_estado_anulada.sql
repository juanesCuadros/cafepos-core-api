-- compra.estado solo permitia 'pagada'/'pendiente' (ver V1__schema_v4.sql)
-- - el modulo Compras necesita un tercer estado para anulaciones (POST
-- /compras/{id}/anular), que revierte stock/costo pero conserva la fila
-- como registro historico en vez de borrarla.
ALTER TABLE compra DROP CONSTRAINT compra_estado_check;
ALTER TABLE compra ADD CONSTRAINT compra_estado_check
    CHECK (estado IN ('pagada', 'pendiente', 'anulada'));
