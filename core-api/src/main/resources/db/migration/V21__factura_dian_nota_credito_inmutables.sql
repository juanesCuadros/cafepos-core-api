-- ============================================================================
-- CafePOS - Migracion V21
-- Endurece factura_dian y nota_credito contra mutaciones indebidas de
-- app_tenant, ahora que existe transmision real a Factus (ver
-- caja.application.FacturaDianTransmisionService).
-- ============================================================================

-- factura_dian nunca se borra desde la aplicacion, aceptada o no - una
-- rechazada queda como registro historico del intento de transmision.
-- V1 le dio DELETE a app_tenant junto con el resto de tablas de capa
-- tenant (bucle generico) - nunca hubo una razon legitima para usarlo.
REVOKE DELETE ON factura_dian FROM app_tenant;

-- Una vez que una factura fue aceptada por la DIAN, ningun campo de esa
-- fila debe volver a cambiar - es el registro legal del documento
-- emitido (numero_factura/cufe/qr_code incluidos). Bloquea el UPDATE
-- completo sin excepcion de campo, no solo los sensibles.
CREATE OR REPLACE FUNCTION fn_bloquear_update_factura_aceptada()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.estado_dian = 'aceptada' THEN
    RAISE EXCEPTION 'No se puede modificar una factura que ya fue aceptada por la DIAN';
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bloquear_update_factura_aceptada
BEFORE UPDATE ON factura_dian
FOR EACH ROW
EXECUTE FUNCTION fn_bloquear_update_factura_aceptada();

-- nota_credito solo se inserta, nunca se actualiza ni se borra despues de
-- creada (confirmado revisando FacturacionService.anular y
-- DevolucionService.solicitar - el unico metodo de escritura del
-- repositorio es guardar(), siempre con una fila nueva, nunca releida
-- para actualizar). Mismo tratamiento ya aplicado a evento_auditoria en V1.
REVOKE UPDATE, DELETE ON nota_credito FROM app_tenant;
