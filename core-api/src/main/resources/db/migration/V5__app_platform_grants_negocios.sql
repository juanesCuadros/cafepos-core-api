-- ============================================================================
-- CaféPOS — Migracion V5
-- Otorga a app_platform los privilegios que faltaban sobre tablas de la capa
-- tenant necesarias para aprovisionar un negocio nuevo (admin-api,
-- POST /admin/negocios).
--
-- BYPASSRLS le permite a app_platform saltarse las politicas de Row Level
-- Security, pero Postgres exige el GRANT de tabla de todas formas, RLS y
-- privilegios son mecanismos independientes. V1 solo le dio privilegios
-- sobre la capa plataforma (planes, superadmin, superadmin_refresh_token,
-- tenants, suscripciones_historial) — nunca sobre las tablas tenant que el
-- aprovisionamiento de un negocio nuevo necesita poblar en la misma
-- transaccion: restaurantes, usuario, rol_permiso, tenant_permiso_config,
-- tenant_rol_config, configuracion_sistema, metodo_pago. rol y permiso son
-- catalogos globales de solo lectura para admin-api.
-- ============================================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON restaurantes, usuario, rol_permiso,
    tenant_permiso_config, tenant_rol_config, configuracion_sistema, metodo_pago
    TO app_platform;

GRANT SELECT ON rol, permiso TO app_platform;
