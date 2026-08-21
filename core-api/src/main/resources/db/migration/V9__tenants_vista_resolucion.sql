-- ============================================================================
-- CaféPOS — Migracion V9
-- Restringe el acceso de app_tenant a "tenants" a traves de una vista
-- angosta, reemplazando el GRANT SELECT completo que otorgo V8.
--
-- Motivo: "tenants" es la unica tabla del sistema sin columna tenant_id,
-- por lo tanto NUNCA entra en el bucle que aplica RLS a todas las demas
-- tablas. Un GRANT SELECT completo ahi deja visibles plan_id,
-- superadmin_aprobador_id, fecha_registro y fecha_proxima_facturacion de
-- TODOS los negocios del sistema a cualquier query bajo app_tenant, sin
-- ninguna politica de fila que lo evite - una fuga de confidencialidad
-- entre cafeterias, aunque acotada a datos de plataforma, no operativos.
--
-- core-api solo necesita id, slug y estado para resolver el tenant desde
-- el subdominio (o X-Tenant-Slug en dev) y chequear si esta suspendido.
-- Nada mas de esta tabla le pertenece a core-api.
-- ============================================================================

CREATE VIEW tenants_resolucion AS
SELECT id, slug, estado
FROM tenants;

REVOKE SELECT ON tenants FROM app_tenant;
GRANT SELECT ON tenants_resolucion TO app_tenant;
