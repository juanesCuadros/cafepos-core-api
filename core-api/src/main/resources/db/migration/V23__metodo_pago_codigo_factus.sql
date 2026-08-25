-- codigo_factus: payment_method_code que exige Factus en payment_details[].
-- NULL para metodos existentes salvo Efectivo (codigo estandar Factus '10')
-- - los demas se configuran despues desde PATCH /metodos-pago (ver
-- MetodoPagoActualizarRequest).
ALTER TABLE metodo_pago ADD COLUMN codigo_factus VARCHAR(10);

UPDATE metodo_pago SET codigo_factus = '10' WHERE es_efectivo = true;
