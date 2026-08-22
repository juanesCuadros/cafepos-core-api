-- ============================================================================
-- CaféPOS — Migración V13
-- Agrega usuario.intentos_fallidos y usuario.bloqueado_hasta, necesarias
-- para el bloqueo tras 5 intentos fallidos de login (RN-008, ver
-- api_00_autenticacion.md).
--
-- bloqueado_hasta NULLABLE a proposito: NULL significa "nunca bloqueado" o
-- "bloqueo ya vencido, no hace falta limpiarlo" — LoginService solo
-- compara contra now(), un timestamp en el pasado ya no bloquea nada.
-- ============================================================================

ALTER TABLE usuario
    ADD COLUMN intentos_fallidos INT NOT NULL DEFAULT 0,
    ADD COLUMN bloqueado_hasta TIMESTAMPTZ;

COMMENT ON COLUMN usuario.intentos_fallidos IS
    'Contador de logins fallidos consecutivos. Se resetea a 0 en login exitoso o al llegar a 5 (ver Usuario.registrarIntentoFallido).';
COMMENT ON COLUMN usuario.bloqueado_hasta IS
    'NULL o en el pasado = no bloqueado. Se setea a now() + 15 minutos al llegar al 5o intento fallido.';
