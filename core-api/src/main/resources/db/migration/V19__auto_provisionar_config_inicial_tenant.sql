-- ============================================================================
-- CaféPOS — Migracion V19
-- Completa una intencion que ya estaba en el codigo pero nunca se implemento:
-- ConfiguracionSistemaNoConfiguradaException / RestauranteNoConfiguradoException
-- dicen literalmente "en teoria todo tenant tiene su fila... provisionada al
-- darse de alta" — pero no existia ningun mecanismo que la creara. Sin ella,
-- un tenant nuevo queda bloqueado en enviar-comanda y cobrar (ver hallazgo
-- 2.7 en INTEGRACION.md).
--
-- Efecto colateral real ya confirmado en produccion (no solo teorico): sin
-- esta migracion, un tenant nuevo queda bloqueado hasta que alguien inserte
-- las filas a mano.
-- ============================================================================

-- Backfill: cualquier tenant que ya exista y le falte alguna de las dos filas.
INSERT INTO configuracion_sistema (tenant_id)
SELECT t.id FROM tenants t
WHERE NOT EXISTS (SELECT 1 FROM configuracion_sistema cs WHERE cs.tenant_id = t.id);

INSERT INTO restaurantes (tenant_id, nombre_negocio)
SELECT t.id, t.slug FROM tenants t
WHERE NOT EXISTS (SELECT 1 FROM restaurantes r WHERE r.tenant_id = t.id);

-- De aca en adelante: todo tenant nuevo se provisiona automaticamente en la
-- misma transaccion que lo crea, sin importar que la cree (admin-api, un
-- script, o un insert a mano) — un solo lugar que garantiza que nunca vuelva
-- a faltar, en vez de que cada modulo que lee esta config tenga que manejar
-- el caso "no existe todavia".
CREATE FUNCTION fn_provisionar_config_inicial_tenant() RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO configuracion_sistema (tenant_id) VALUES (NEW.id);
    -- nombre_negocio es el unico NOT NULL sin default en restaurantes — el
    -- slug es un placeholder razonable, se edita despues desde Restaurante ->
    -- Informacion general.
    INSERT INTO restaurantes (tenant_id, nombre_negocio) VALUES (NEW.id, NEW.slug);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_provisionar_config_inicial_tenant
    AFTER INSERT ON tenants
    FOR EACH ROW
    EXECUTE FUNCTION fn_provisionar_config_inicial_tenant();
