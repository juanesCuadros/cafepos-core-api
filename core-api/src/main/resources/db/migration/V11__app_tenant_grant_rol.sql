-- ============================================================================
-- CaféPOS — Migracion V11
-- Otorga a app_tenant SELECT sobre rol, que faltaba por completo.
--
-- Contexto: V1 solo otorga privilegios sobre tablas CON columna tenant_id
-- (bucle en information_schema.columns). "rol" es catalogo global (5 roles
-- fijos, sin tenant_id), asi que quedo fuera de ese bucle. V5/V7 le dieron
-- SELECT a app_platform (admin-api, matriz de permisos) pero nunca a
-- app_tenant. Con el objeto "usuario" del contrato de login (incluye
-- rol.nombre), core-api necesita leer esta tabla.
--
-- Solo SELECT: el catalogo de roles es fijo y editable unicamente vía
-- admin-api (rol app_platform) — core-api solo lee.
-- ============================================================================

GRANT SELECT ON rol TO app_tenant;
