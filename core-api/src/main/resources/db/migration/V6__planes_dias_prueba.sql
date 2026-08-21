-- ============================================================================
-- CaféPOS — Migracion V6
-- Agrega planes.dias_prueba, necesaria para que admin-api decida el estado
-- inicial de un tenant nuevo (prueba vs activo directo) al crear el negocio.
-- ============================================================================

ALTER TABLE planes ADD COLUMN dias_prueba INT NOT NULL DEFAULT 0;

COMMENT ON COLUMN planes.dias_prueba IS
    'dias de prueba gratuita antes de requerir pago. 0 = sin periodo de
    prueba, el negocio arranca activo directamente.';
