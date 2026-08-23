-- ============================================================================
-- CaféPOS — Migración V18
-- Amplia el catalogo de permisos de operacion.pedido_abierto con las 4
-- acciones que todavia faltaban (ver V2__catalogo_permisos.sql — ese
-- catalogo ya traia ver/agregar_producto/enviar_comanda/mover_mesa/
-- prefactura/cobrar, pero no crear/editar_item/eliminar_item/
-- marcar_lista_cobrar, necesarias para el modulo Operacion completo).
--
-- Backfill en rol_permiso para CADA tenant existente: Jefe/Admin/Mesero
-- activo=true, Cajero/Cocina quedan sin fila (false por defecto si se
-- llegan a agregar despues) — mismo patron de la matriz ya documentada en
-- V2 para el resto de acciones de operacion.pedido_abierto.
--
-- Backfill en tenant_permiso_config: eliminar_item SI exige PIN real (a
-- diferencia de cuando tenant_permiso_config era solo metadata sin
-- enforcement) — ver PinStepUpService, ya conectado en este mismo modulo.
-- ============================================================================

INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('operacion.pedido_abierto', 'crear', 'Abrir un pedido nuevo en una mesa'),
    ('operacion.pedido_abierto', 'editar_item', 'Editar cantidad u observacion de un item'),
    ('operacion.pedido_abierto', 'eliminar_item', 'Eliminar un item del pedido'),
    ('operacion.pedido_abierto', 'marcar_lista_cobrar', 'Marcar mesa lista para cobrar');

INSERT INTO rol_permiso (tenant_id, rol_id, permiso_id, activo)
SELECT t.id, r.id, p.id, true
FROM tenants t
CROSS JOIN rol r
CROSS JOIN permiso p
WHERE r.nombre IN ('Jefe', 'Admin', 'Mesero')
  AND p.modulo = 'operacion.pedido_abierto'
  AND p.accion IN ('crear', 'editar_item', 'eliminar_item', 'marcar_lista_cobrar');

INSERT INTO tenant_permiso_config (tenant_id, permiso_id, requiere_pin)
SELECT t.id, p.id, true
FROM tenants t
CROSS JOIN permiso p
WHERE p.modulo = 'operacion.pedido_abierto' AND p.accion = 'eliminar_item';
