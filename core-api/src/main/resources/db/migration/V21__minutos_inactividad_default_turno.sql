-- ============================================================================
-- CaféPOS — Migracion V21
-- El default de minutos_inactividad quedaba en 15 (V1__schema_v4.sql), casi
-- igual al TTL del access_token (15 min) — cualquier pausa normal de un
-- usuario (atender una mesa larga, ir al bano) tumbaba el refresh aunque el
-- refresh_token todavia tuviera horas de vigencia (ver RefreshTokenService,
-- que chequea minutos_inactividad ADEMAS de la vigencia del token). En
-- produccion CrearNegocioService (admin-api) ya pisaba ese default con 1440
-- (24h) para tenants nuevos, pero eso equivale a desactivar el chequeo de
-- inactividad (nunca dispara antes de que el refresh_token expire solo por
-- TTL) — ninguno de los dos numeros es el que se queria.
--
-- Nuevo default: 480 minutos (8h, la duracion de un turno). Reactiva el
-- chequeo de inactividad con un valor que tiene sentido para el negocio: un
-- cajero que dejo el turno sin cerrar sesion queda deslogueado al dia
-- siguiente, pero una pausa normal dentro del mismo turno no lo saca.
-- ============================================================================

ALTER TABLE tenant_rol_config ALTER COLUMN minutos_inactividad SET DEFAULT 480;

-- Solo pisa filas que siguen en alguno de los dos defaults viejos (15 o
-- 1440) — si algun tenant ya entro a Configuracion > Sistema > Sesion y puso
-- un valor propio, esa eleccion no se toca.
UPDATE tenant_rol_config SET minutos_inactividad = 480 WHERE minutos_inactividad IN (15, 1440);
