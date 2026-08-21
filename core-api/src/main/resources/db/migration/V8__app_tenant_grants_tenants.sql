-- ============================================================================
-- CaféPOS — Migracion V8
-- Otorga a app_tenant SELECT sobre tenants, que faltaba por completo.
--
-- Contexto: V1 solo otorga privilegios sobre tablas CON columna tenant_id
-- (bucle en information_schema.columns). "tenants" es la tabla padre, no
-- tiene esa columna, asi que quedo fuera de ese bucle. Hasta ahora no hacia
-- falta porque core-api no hacia ninguna query real; con login real
-- (resolucion de slug a tenant_id, chequeo de tenants.estado en cada
-- request) core-api necesita leer esta tabla.
--
-- Solo SELECT, nunca INSERT/UPDATE/DELETE: mutar el estado de un tenant
-- (activar, suspender, cancelar) es responsabilidad exclusiva de admin-api
-- (rol app_platform, con BYPASSRLS) — core-api solo lee.
-- ============================================================================

GRANT SELECT ON tenants TO app_tenant;
