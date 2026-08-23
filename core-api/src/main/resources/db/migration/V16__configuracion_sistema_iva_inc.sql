-- ---------------------------------------------------------------------------
-- configuracion_sistema: iva_porcentaje / inc_porcentaje (Modulo 11.3)
-- nullable porque no todo negocio tiene INC (impuesto al consumo), y
-- iva_porcentaje puede no estar configurado todavia al momento del alta.
-- ---------------------------------------------------------------------------
ALTER TABLE configuracion_sistema ADD COLUMN iva_porcentaje DECIMAL(5,2);
ALTER TABLE configuracion_sistema ADD COLUMN inc_porcentaje DECIMAL(5,2);
