-- ============================================================================
-- CaféPOS — Migracion V10
-- evento_seguridad guarda evidencia sensible (intentos de login fallidos,
-- reuso de refresh token) igual que evento_auditoria, pero nunca recibio
-- la misma garantia append-only a nivel de motor - quedo cubierta por el
-- bucle general de GRANTs, que le da UPDATE/DELETE completos a app_tenant.
--
-- Un log de seguridad que se puede editar o borrar desde la misma
-- aplicacion que audita no sirve como evidencia confiable - mismo
-- razonamiento que ya se aplico a evento_auditoria.
-- ============================================================================

REVOKE UPDATE, DELETE ON evento_seguridad FROM app_tenant;
