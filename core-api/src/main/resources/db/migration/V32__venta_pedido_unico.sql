-- Mismo problema que ya se arreglo para mesas (V29) y turno (V31):
-- VentaService.cobrar() hace un chequeo "check-then-act" sin bloqueo
-- (pedido.estaCerrado() y despues el INSERT de la venta) — dos cobros casi
-- simultaneos del mismo pedido pueden pasar el chequeo antes de que ninguno
-- confirme su venta, generando doble venta/doble cobro/posible doble
-- factura DIAN para el mismo pedido. Ver
-- FASE1_AUDITORIA_OPERACION_CAJA_PRODUCTOS.md 3.1.1.
--
-- A diferencia de mesa/turno, acá NO hay un paso automatico de "limpieza de
-- duplicados existentes": una venta duplicada ya es dinero real cobrado dos
-- veces, no un estado que se pueda simplemente cerrar/descartar con un
-- UPDATE. Si esta migracion falla por datos duplicados ya existentes,
-- hace falta revisar esos casos a mano (probablemente una devolucion o un
-- ajuste contable) antes de poder aplicar el indice.
DO $$
DECLARE
    duplicados INT;
BEGIN
    SELECT COUNT(*) INTO duplicados FROM (
        SELECT pedido_id FROM venta GROUP BY pedido_id HAVING COUNT(*) > 1
    ) t;
    IF duplicados > 0 THEN
        RAISE EXCEPTION 'Hay % pedido(s) con mas de una venta asociada — revisar a mano antes de aplicar el indice unico (ver V32__venta_pedido_unico.sql)', duplicados;
    END IF;
END $$;

-- Un pedido nunca se reabre despues de cerrado (confirmado: ningun codigo
-- del modulo operacion vuelve a poner estado='abierto' sobre un pedido ya
-- cerrado) — a lo sumo una venta por pedido, siempre, sin excepcion de
-- fecha ni de estado de la venta (ni siquiera una venta anulada libera el
-- pedido para una venta nueva).
CREATE UNIQUE INDEX idx_venta_pedido_unico ON venta (pedido_id);
