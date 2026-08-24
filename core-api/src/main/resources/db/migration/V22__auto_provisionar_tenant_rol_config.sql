-- ============================================================================
-- CaféPOS — Migracion V22
-- Cierra hallazgo 2.12 (INTEGRACION.md): el trigger de V19 provisiona
-- configuracion_sistema y restaurantes para todo tenant nuevo, pero nunca
-- cubrio tenant_rol_config. Un tenant creado por fuera del flujo normal de
-- admin-api (CrearNegocioService, que si la llena) queda sin ninguna fila
-- ahi — y RefreshTokenService.ejecutar() hace .orElseThrow() si no encuentra
-- la fila del rol del usuario, asi que ese tenant no puede refrescar su
-- sesion NUNCA, no solo despues de X minutos de inactividad.
-- ============================================================================

-- Backfill: cualquier tenant al que le falte alguna fila de tenant_rol_config
-- para alguno de los 5 roles del catalogo (usa el DEFAULT de la columna,
-- 480 min desde V21, para minutos_inactividad).
INSERT INTO tenant_rol_config (tenant_id, rol_id)
SELECT t.id, r.id
FROM tenants t
CROSS JOIN rol r
WHERE NOT EXISTS (
    SELECT 1 FROM tenant_rol_config trc
    WHERE trc.tenant_id = t.id AND trc.rol_id = r.id
);

-- De aca en adelante: todo tenant nuevo se provisiona con las 5 filas en la
-- misma transaccion que lo crea, sin importar quien lo cree.
CREATE OR REPLACE FUNCTION fn_provisionar_config_inicial_tenant() RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO configuracion_sistema (tenant_id) VALUES (NEW.id);
    INSERT INTO restaurantes (tenant_id, nombre_negocio) VALUES (NEW.id, NEW.slug);
    INSERT INTO tenant_rol_config (tenant_id, rol_id)
    SELECT NEW.id, r.id FROM rol r;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
