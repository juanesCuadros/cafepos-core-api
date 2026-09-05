-- Factus rechaza POST /v2/bills/validate con 422 si no se manda
-- numbering_range_id (confirmado en vivo 02-sep-2026) - la doc de Factus dice
-- que es opcional "si tienes un unico rango activo", pero en la practica la
-- cuenta sandbox de este proyecto ya no cae en ese caso por defecto. Se
-- guarda el id que Factus asigna al rango de numeracion (GET /v2/numbering-ranges,
-- campo "id") - no es el mismo dato que rango_inicio/rango_fin (esos son
-- "from"/"to" del rango, ya existentes).
ALTER TABLE facturacion_dian_resolucion ADD COLUMN numbering_range_id BIGINT;
