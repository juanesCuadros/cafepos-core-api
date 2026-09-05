-- ============================================================================
-- CaféPOS — Migracion V30
-- 1. Agrega superadmin.intentos_fallidos y superadmin.bloqueado_hasta para
--    proteccion contra fuerza bruta en admin-api (mismo criterio que usuario en V13).
-- 2. Crea superadmin_evento_auditoria para trazabilidad de operaciones de plataforma
--    (alta/suspension de tenants, configuracion DIAN, mutacion de planes).
-- 3. Otorga privilegios a app_platform.
-- ============================================================================

ALTER TABLE superadmin
    ADD COLUMN intentos_fallidos INT NOT NULL DEFAULT 0,
    ADD COLUMN bloqueado_hasta TIMESTAMPTZ;

COMMENT ON COLUMN superadmin.intentos_fallidos IS
    'Contador de logins fallidos consecutivos. Se resetea a 0 tras login exitoso o al llegar a 5.';
COMMENT ON COLUMN superadmin.bloqueado_hasta IS
    'NULL o en el pasado = no bloqueado. Se setea a now() + 30 minutos al 5o intento fallido consecutivo.';

-- ---------------------------------------------------------------------------
-- superadmin_evento_auditoria (bitacora de operaciones de Super Administrador)
-- ---------------------------------------------------------------------------
CREATE TABLE superadmin_evento_auditoria (
    id                  BIGSERIAL PRIMARY KEY,
    superadmin_id       INT REFERENCES superadmin(id),
    accion              VARCHAR(50) NOT NULL,
    entidad_tipo        VARCHAR(50) NOT NULL,
    entidad_id          INT,
    datos_antes         JSONB,
    datos_despues       JSONB,
    ip_origen           VARCHAR(45),
    user_agent          VARCHAR(255),
    fecha_hora          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_superadmin_auditoria_fecha ON superadmin_evento_auditoria(fecha_hora DESC);
CREATE INDEX idx_superadmin_auditoria_superadmin ON superadmin_evento_auditoria(superadmin_id);
CREATE INDEX idx_superadmin_auditoria_entidad ON superadmin_evento_auditoria(entidad_tipo, entidad_id);

-- Grants a app_platform
GRANT ALL ON superadmin_evento_auditoria TO app_platform;
GRANT USAGE, SELECT ON SEQUENCE superadmin_evento_auditoria_id_seq TO app_platform;
