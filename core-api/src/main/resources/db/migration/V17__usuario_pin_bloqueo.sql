-- ============================================================================
-- CaféPOS — Migración V17
-- Agrega usuario.pin_intentos_fallidos y usuario.pin_bloqueado_hasta,
-- contador SEPARADO del de login (intentos_fallidos/bloqueado_hasta, ver
-- V13) — un PIN de step-up incorrecto no debe afectar ni compartir el
-- contador de login fallido, son mecanismos de bloqueo independientes.
--
-- pin_bloqueado_hasta NULLABLE a proposito, mismo criterio que
-- bloqueado_hasta (V13): NULL o en el pasado = no bloqueado, PinVerificarService
-- solo compara contra now().
--
-- El CHECK de evento_seguridad.tipo_evento ya permite 'pin_fallido' desde
-- V1 (schema v4) — confirmado contra flyway_schema_history antes de asumir,
-- no hace falta tocarlo aca.
-- ============================================================================

ALTER TABLE usuario
    ADD COLUMN pin_intentos_fallidos INT NOT NULL DEFAULT 0,
    ADD COLUMN pin_bloqueado_hasta TIMESTAMPTZ;

COMMENT ON COLUMN usuario.pin_intentos_fallidos IS
    'Contador de PIN de step-up fallidos consecutivos. Se resetea a 0 en verificacion exitosa o al llegar a 5 (ver Usuario.registrarPinIntentoFallido).';
COMMENT ON COLUMN usuario.pin_bloqueado_hasta IS
    'NULL o en el pasado = no bloqueado. Se setea a now() + 30 minutos al llegar al 5o intento fallido de PIN.';
