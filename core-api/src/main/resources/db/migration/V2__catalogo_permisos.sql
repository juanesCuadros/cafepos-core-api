-- ============================================================================
-- CaféPOS — Catálogo de Permisos (permiso) + Matriz de Permisos por Defecto
-- v1.0 · Agosto 2026
--
-- Fuente: cafepos_MASTER.md — cada fila se deriva de las tablas
-- "Acceso por rol" ya existentes en el documento, sub-vista por sub-vista.
-- Convención (aprobada): permiso.modulo = "modulo_padre.sub_vista" (snake_case)
--                         permiso.accion = verbo snake_case
--
-- IMPORTANTE: este script solo puebla `permiso` (catálogo GLOBAL, sin tenant_id).
-- La matriz de acceso por rol que aparece más abajo, comentada, NO se inserta
-- aquí como SQL fijo porque `rol_permiso` requiere tenant_id (es tenant-scoped).
-- Debe aplicarse programáticamente cuando se crea un nuevo tenant: al registrar
-- un negocio, el backend copia esta matriz por defecto dentro de INSERT INTO
-- rol_permiso (tenant_id, rol_id, permiso_id, activo) para ese tenant nuevo.
-- Lo mismo aplica a tenant_permiso_config (columna "PIN" de la matriz).
--
-- Módulos 10 y 11 decían "Admin: Parcial" sin desglose por sub-vista en el
-- documento fuente — ya fue resuelto y confirmado explícitamente (ver matrices
-- de esos módulos más abajo, marcadas como CONFIRMADO).
-- ============================================================================

-- ============================================================================
-- MÓDULO 2 — OPERACIÓN
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('operacion.pedidos', 'ver', 'Ver panel de mesas y pedidos activos'),
    ('operacion.pedido_abierto', 'ver', 'Ver detalle de un pedido abierto'),
    ('operacion.pedido_abierto', 'agregar_producto', 'Agregar productos a un pedido'),
    ('operacion.pedido_abierto', 'enviar_comanda', 'Enviar comanda a cocina/impresora'),
    ('operacion.pedido_abierto', 'mover_mesa', 'Trasladar un pedido a otra mesa'),
    ('operacion.pedido_abierto', 'prefactura', 'Generar/imprimir prefactura'),
    ('operacion.pedido_abierto', 'cobrar', 'Cobrar directamente desde el pedido abierto'),
    ('operacion.kds', 'ver', 'Ver pantalla de cocina (KDS)'),
    ('operacion.kds', 'cambiar_estado_item', 'Cambiar estado de un ítem (pendiente/preparación/listo)'),
    ('operacion.kds', 'imprimir_comanda', 'Reimprimir comanda desde KDS'),
    ('operacion.turno', 'ver', 'Ver estado del turno propio'),
    ('operacion.turno', 'iniciar', 'Iniciar turno propio'),
    ('operacion.turno', 'cerrar', 'Cerrar turno propio');

-- Matriz por defecto — Módulo 2:
-- modulo                      | accion             | Jefe | Admin | Cajero | Mesero | Cocina | PIN
-- operacion.pedidos           | ver                |  ✓   |   ✓   |   —    |   ✓    |   —    | no
-- operacion.pedido_abierto    | ver                |  ✓   |   ✓   |   —    |   ✓    |   —    | no
-- operacion.pedido_abierto    | agregar_producto   |  ✓   |   ✓   |   —    |   ✓    |   —    | no
-- operacion.pedido_abierto    | enviar_comanda     |  ✓   |   ✓   |   —    |   ✓    |   —    | no
-- operacion.pedido_abierto    | mover_mesa         |  ✓   |   ✓   |   —    |   ✓    |   —    | no
-- operacion.pedido_abierto    | prefactura         |  ✓   |   ✓   |   —    |   ✓    |   —    | no
-- operacion.pedido_abierto    | cobrar             |  ✓   |   ✓   |   —    |   —    |   —    | no
-- operacion.kds               | ver                |  ✓   |   ✓   |   —    |   —    |   ✓    | no
-- operacion.kds               | cambiar_estado_item|  ✓   |   ✓   |   —    |   —    |   ✓    | no
-- operacion.kds               | imprimir_comanda   |  ✓   |   ✓   |   —    |   —    |   ✓    | no
-- operacion.turno             | ver/iniciar/cerrar |  ✓   |   ✓   |   ✓    |   ✓    |   ✓    | no

-- ============================================================================
-- MÓDULO 3 — CAJA
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('caja.venta_rapida', 'ver', 'Ver flujo de venta rápida'),
    ('caja.venta_rapida', 'agregar_producto', 'Agregar productos en venta rápida'),
    ('caja.venta_rapida', 'cobrar', 'Cobrar en venta rápida'),
    ('caja.pos', 'ver', 'Ver panel de cobro de mesas'),
    ('caja.pos', 'seleccionar_mesa', 'Seleccionar mesa para cobrar'),
    ('caja.pos', 'editar_cuenta', 'Agregar productos a la cuenta antes de cobrar'),
    ('caja.pos', 'eliminar_item_preparado', 'Eliminar un ítem ya preparado de la cuenta'),
    ('caja.pos', 'cobrar', 'Finalizar cobro'),
    ('caja.apertura_cierre', 'ver', 'Ver estado de la jornada de caja'),
    ('caja.apertura_cierre', 'abrir_caja', 'Abrir jornada de caja'),
    ('caja.apertura_cierre', 'registrar_ingreso', 'Registrar ingreso manual de caja'),
    ('caja.apertura_cierre', 'registrar_egreso', 'Registrar egreso manual de caja'),
    ('caja.apertura_cierre', 'cerrar_caja', 'Cerrar jornada de caja (arqueo)'),
    ('caja.historial_caja', 'ver', 'Consultar jornadas de caja anteriores'),
    ('caja.historial_ventas', 'ver', 'Consultar historial de ventas'),
    ('caja.historial_ventas', 'reimprimir', 'Reimprimir comprobante de venta'),
    ('caja.historial_ventas', 'anular', 'Anular una venta'),
    ('caja.facturacion', 'ver', 'Consultar facturas electrónicas DIAN'),
    ('caja.facturacion', 'reimprimir', 'Reimprimir factura DIAN'),
    ('caja.facturacion', 'enviar_correo', 'Enviar factura por correo'),
    ('caja.facturacion', 'reintentar_envio', 'Reintentar envío de factura a DIAN'),
    ('caja.facturacion', 'generar_nota_credito', 'Generar nota crédito sobre una factura'),
    ('caja.devoluciones', 'ver', 'Consultar devoluciones'),
    ('caja.devoluciones', 'solicitar', 'Solicitar una nueva devolución'),
    ('caja.devoluciones', 'autorizar', 'Autorizar una devolución solicitada');

-- Matriz por defecto — Módulo 3:
-- modulo                  | accion                  | Jefe | Admin | Cajero | Mesero | Cocina | PIN
-- caja.venta_rapida       | ver/agregar/cobrar      |  ✓   |   ✓   |   ✓    |   —    |   —    | no
-- caja.pos                | ver/seleccionar/editar  |  ✓   |   ✓   |   ✓    |   —    |   —    | no
-- caja.pos                | eliminar_item_preparado |  ✓   |   ✓   |   ✓    |   —    |   —    | SÍ
-- caja.pos                | cobrar                  |  ✓   |   ✓   |   ✓    |   —    |   —    | no
-- caja.apertura_cierre    | ver/abrir/ingreso/cerrar|  ✓   |   ✓   |   ✓    |   —    |   —    | no
-- caja.apertura_cierre    | registrar_egreso        |  ✓   |   ✓   |   ✓    |   —    |   —    | SÍ
-- caja.historial_caja     | ver                     |  ✓   |   ✓   |   ✓    |   —    |   —    | no
-- caja.historial_ventas   | ver                     |  ✓   |   ✓   |   ✓    |   —    |   —    | no
-- caja.historial_ventas   | reimprimir              |  ✓   |   ✓   |   —    |   —    |   —    | no
-- caja.historial_ventas   | anular                  |  ✓   |   ✓   |   —    |   —    |   —    | SÍ
-- caja.facturacion        | ver/reimprimir/correo/reintentar | ✓ | ✓ | ✓ | — | — | no
-- caja.facturacion        | generar_nota_credito    |  ✓   |   ✓   |   ✓    |   —    |   —    | SÍ
-- caja.devoluciones       | ver                     |  ✓   |   ✓   |   —    |   —    |   —    | no
-- caja.devoluciones       | solicitar               |  ✓   |   ✓   |   ✓    |   —    |   —    | SÍ (Cajero solo solicita, requiere PIN de Admin/Jefe)
-- caja.devoluciones       | autorizar               |  ✓   |   ✓   |   —    |   —    |   —    | SÍ

-- ============================================================================
-- MÓDULO 4 — PRODUCTOS Y MENÚ  (Jefe✓Total, Admin✓Total, resto sin acceso)
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('productos_menu.productos', 'ver', 'Ver catálogo de productos'),
    ('productos_menu.productos', 'crear', 'Crear producto'),
    ('productos_menu.productos', 'editar', 'Editar producto'),
    ('productos_menu.productos', 'eliminar', 'Eliminar producto'),
    ('productos_menu.categorias', 'ver', 'Ver categorías'),
    ('productos_menu.categorias', 'crear', 'Crear categoría'),
    ('productos_menu.categorias', 'editar', 'Editar categoría'),
    ('productos_menu.categorias', 'eliminar', 'Eliminar categoría'),
    ('productos_menu.combos', 'ver', 'Ver combos'),
    ('productos_menu.combos', 'crear', 'Crear combo'),
    ('productos_menu.combos', 'editar', 'Editar combo'),
    ('productos_menu.combos', 'eliminar', 'Eliminar combo'),
    ('productos_menu.recetas', 'ver', 'Ver recetas'),
    ('productos_menu.recetas', 'crear', 'Crear receta'),
    ('productos_menu.recetas', 'editar', 'Editar receta'),
    ('productos_menu.recetas', 'eliminar', 'Eliminar receta'),
    ('productos_menu.promociones', 'ver', 'Ver promociones'),
    ('productos_menu.promociones', 'crear', 'Crear promoción'),
    ('productos_menu.promociones', 'editar', 'Editar promoción'),
    ('productos_menu.promociones', 'eliminar', 'Eliminar promoción');

-- Matriz por defecto — Módulo 4:
-- Todas las acciones ver/crear/editar: Jefe ✓ | Admin ✓ | Cajero/Mesero/Cocina —  | PIN: no
-- productos_menu.productos | eliminar: Jefe ✓ | Admin — (doc: "solo Jefe, requiere confirmación") | PIN: no
-- (el resto de "eliminar" en este módulo: Jefe ✓ | Admin ✓, salvo producto)

-- ============================================================================
-- MÓDULO 5 — INVENTARIO  (Jefe✓, Admin✓, resto sin acceso)
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('inventario.existencias', 'ver', 'Ver existencias'),
    ('inventario.existencias', 'ajustar', 'Ajuste manual de stock'),
    ('inventario.insumos', 'ver', 'Ver insumos'),
    ('inventario.insumos', 'crear', 'Crear insumo'),
    ('inventario.insumos', 'editar', 'Editar insumo'),
    ('inventario.insumos', 'eliminar', 'Eliminar insumo'),
    ('inventario.historial_movimientos', 'ver', 'Consultar historial de movimientos'),
    ('inventario.conteos', 'ver', 'Ver conteos'),
    ('inventario.conteos', 'crear', 'Registrar nuevo conteo'),
    ('inventario.perdidas', 'ver', 'Ver pérdidas'),
    ('inventario.perdidas', 'registrar', 'Registrar pérdida'),
    ('inventario.vencimientos', 'ver', 'Ver vencimientos');

-- Matriz por defecto — Módulo 5:
-- Todas: Jefe ✓ | Admin ✓ | Cajero/Mesero/Cocina —
-- inventario.existencias | ajustar → PIN: SÍ
-- resto → PIN: no

-- ============================================================================
-- MÓDULO 6 — COMPRAS  (Jefe✓, Admin✓, resto sin acceso)
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('compras.registrar_compra', 'ver', 'Ver formulario de registrar compra'),
    ('compras.registrar_compra', 'crear', 'Registrar nueva compra'),
    ('compras.historial_compras', 'ver', 'Consultar historial de compras'),
    ('compras.historial_compras', 'editar', 'Editar compra (solo del día actual)'),
    ('compras.historial_compras', 'eliminar', 'Eliminar compra'),
    ('compras.historial_compras', 'marcar_pagada', 'Marcar compra a crédito como pagada'),
    ('compras.proveedores', 'ver', 'Ver proveedores'),
    ('compras.proveedores', 'crear', 'Crear proveedor'),
    ('compras.proveedores', 'editar', 'Editar proveedor'),
    ('compras.proveedores', 'eliminar', 'Eliminar proveedor');

-- Matriz por defecto — Módulo 6:
-- Todas: Jefe ✓ | Admin ✓ | Cajero/Mesero/Cocina —
-- compras.historial_compras | eliminar → PIN: SÍ
-- resto → PIN: no

-- ============================================================================
-- MÓDULO 7 — CLIENTES
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('clientes', 'ver', 'Ver listado de clientes'),
    ('clientes', 'crear', 'Crear cliente'),
    ('clientes', 'editar', 'Editar cliente'),
    ('clientes', 'eliminar', 'Eliminar cliente');

-- Matriz por defecto — Módulo 7:
-- clientes | ver/crear   | Jefe ✓ | Admin ✓ | Cajero ✓ (básico, desde POS) | Mesero — | Cocina — | PIN: no
-- clientes | editar/eliminar | Jefe ✓ | Admin ✓ | Cajero — | Mesero — | Cocina — | PIN: no

-- ============================================================================
-- MÓDULO 8 — PERSONAL
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('personal.empleados', 'ver', 'Ver empleados'),
    ('personal.empleados', 'crear', 'Crear empleado'),
    ('personal.empleados', 'editar', 'Editar empleado'),
    ('personal.empleados', 'eliminar', 'Eliminar empleado'),
    ('personal.turnos', 'ver', 'Ver turnos (gestión, no el registro propio)'),
    ('personal.turnos', 'crear', 'Crear turno manualmente'),
    ('personal.turnos', 'editar', 'Editar turno'),
    ('personal.turnos', 'eliminar', 'Eliminar turno');

-- Matriz por defecto — Módulo 8:
-- personal.empleados | ver/crear/editar | Jefe ✓ | Admin ✓ | resto — | PIN: no
-- personal.empleados | eliminar         | Jefe ✓ | Admin —  | resto — | PIN: no
-- personal.turnos    | ver/crear/editar | Jefe ✓ | Admin ✓ | resto — | PIN: no
-- personal.turnos    | eliminar         | Jefe ✓ | Admin —  | resto — | PIN: no

-- ============================================================================
-- MÓDULO 9 — GASTOS  (Jefe✓Total, Admin✓Total, resto sin acceso)
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('gastos.registrar_gasto', 'ver', 'Ver formulario de registrar gasto'),
    ('gastos.registrar_gasto', 'crear', 'Registrar gasto'),
    ('gastos.historial_gastos', 'ver', 'Consultar historial de gastos'),
    ('gastos.historial_gastos', 'editar', 'Editar gasto'),
    ('gastos.historial_gastos', 'eliminar', 'Eliminar gasto');

-- Matriz por defecto — Módulo 9:
-- gastos.* ver/crear/editar | Jefe ✓ | Admin ✓ | resto — | PIN: no
-- gastos.historial_gastos | eliminar | Jefe ✓ | Admin — (doc: "solo Jefe") | resto — | PIN: no

-- ============================================================================
-- MÓDULO 10 — RESTAURANTE
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('restaurante.info_general', 'ver', 'Ver información general del negocio'),
    ('restaurante.info_general', 'editar', 'Editar información general del negocio'),
    ('restaurante.zonas_mesas', 'ver', 'Ver zonas y mesas'),
    ('restaurante.zonas_mesas', 'crear', 'Crear zona o mesa'),
    ('restaurante.zonas_mesas', 'editar', 'Editar zona o mesa'),
    ('restaurante.zonas_mesas', 'eliminar', 'Eliminar zona o mesa'),
    ('restaurante.metodos_pago', 'ver', 'Ver métodos de pago configurados'),
    ('restaurante.metodos_pago', 'crear', 'Agregar método de pago'),
    ('restaurante.metodos_pago', 'editar', 'Editar método de pago'),
    ('restaurante.metodos_pago', 'eliminar', 'Eliminar método de pago (no aplica a Efectivo)'),
    ('restaurante.facturacion_dian', 'ver', 'Ver estado de configuración DIAN (solo lectura)'),
    ('restaurante.menu_digital', 'ver', 'Ver vista previa del menú digital'),
    ('restaurante.menu_digital', 'descargar_qr', 'Descargar código QR del menú'),
    ('restaurante.menu_digital', 'copiar_link', 'Copiar link del menú digital'),
    ('restaurante.menu_digital', 'activar_desactivar', 'Activar o desactivar el menú digital');

-- Matriz por defecto — Módulo 10 (CONFIRMADO):
-- modulo                        | accion              | Jefe | Admin | Cajero/Mesero/Cocina | PIN
-- restaurante.info_general      | ver, editar         |  ✓   |   ✓   |          —            | no
-- restaurante.zonas_mesas       | ver, crear, editar, |  ✓   |   ✓   |          —            | no
--                                  eliminar
-- restaurante.metodos_pago      | ver                 |  ✓   |   ✓   |          —            | no
-- restaurante.metodos_pago      | crear, editar,      |  ✓   |   —   |          —            | no
--                                  eliminar
--   (decisión financiera del negocio, exclusiva de Jefe)
-- restaurante.facturacion_dian  | ver                 |  ✓   |   ✓   |          —            | no
--   (solo lectura para todos, ni el Jefe puede editar aquí — la configura el desarrollador al crear el tenant)
-- restaurante.menu_digital      | ver, descargar_qr,  |  ✓   |   ✓   |          —            | no
--                                  copiar_link,
--                                  activar_desactivar
--   (operativo, no financiero)

-- ============================================================================
-- MÓDULO 11 — CONFIGURACIÓN
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('configuracion.usuarios', 'ver', 'Ver usuarios del sistema'),
    ('configuracion.usuarios', 'crear', 'Crear usuario'),
    ('configuracion.usuarios', 'editar', 'Editar usuario'),
    ('configuracion.usuarios', 'eliminar', 'Eliminar usuario'),
    ('configuracion.usuarios', 'resetear_password', 'Resetear contraseña de un usuario'),
    ('configuracion.roles_permisos', 'ver', 'Ver matriz de roles y permisos'),
    ('configuracion.roles_permisos', 'editar', 'Editar matriz de roles y permisos'),
    ('configuracion.sistema', 'ver', 'Ver configuración del sistema'),
    ('configuracion.sistema', 'editar', 'Editar configuración del sistema');

-- Matriz por defecto — Módulo 11 (CONFIRMADO):
-- modulo                          | accion               | Jefe | Admin | Cajero/Mesero/Cocina | PIN
-- configuracion.usuarios          | ver, crear, editar,  |  ✓   |   ✓   |          —            | no
--                                    resetear_password
-- configuracion.usuarios          | eliminar             |  ✓   |   —   |          —            | no
--   (consistente con la misma regla de Personal → Empleados: solo Jefe elimina)
-- configuracion.roles_permisos    | ver, editar          |  ✓   |   —   |          —            | no
--   (exclusivo de Jefe — evita que Admin se autoasigne o le asigne a otro Admin
--    permisos que el Jefe no quería dar)
-- configuracion.sistema           | ver, editar          |  ✓   |   ✓   |          —            | no
--   (operativo: impresoras, propina, tiempos de sesión — no financiero ni de seguridad)
-- Cajero/Mesero/Cocina: sin acceso en todo el módulo.

-- ============================================================================
-- MÓDULO 12 — REPORTES  (Jefe✓Total ÚNICAMENTE — Admin, Cajero, Mesero,
-- Cocina sin acceso a ninguna sub-vista de este módulo)
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('reportes.ventas', 'ver', 'Ver reporte de ventas'),
    ('reportes.ventas', 'exportar_pdf', 'Exportar reporte de ventas a PDF'),
    ('reportes.ventas', 'exportar_excel', 'Exportar reporte de ventas a Excel'),
    ('reportes.productos_mas_vendidos', 'ver', 'Ver reporte de productos más vendidos'),
    ('reportes.productos_mas_vendidos', 'exportar_pdf', 'Exportar a PDF'),
    ('reportes.productos_mas_vendidos', 'exportar_excel', 'Exportar a Excel'),
    ('reportes.ingredientes_mas_usados', 'ver', 'Ver reporte de ingredientes más usados'),
    ('reportes.ingredientes_mas_usados', 'exportar_pdf', 'Exportar a PDF'),
    ('reportes.ingredientes_mas_usados', 'exportar_excel', 'Exportar a Excel'),
    ('reportes.ventas_por_mesero', 'ver', 'Ver reporte de ventas por mesero'),
    ('reportes.ventas_por_mesero', 'exportar_pdf', 'Exportar a PDF'),
    ('reportes.ventas_por_mesero', 'exportar_excel', 'Exportar a Excel'),
    ('reportes.ticket_por_dia', 'ver', 'Ver reporte de ticket por día'),
    ('reportes.ticket_por_dia', 'exportar_pdf', 'Exportar a PDF'),
    ('reportes.ticket_por_dia', 'exportar_excel', 'Exportar a Excel'),
    ('reportes.clientes_frecuentes', 'ver', 'Ver reporte de clientes frecuentes'),
    ('reportes.clientes_frecuentes', 'exportar_pdf', 'Exportar a PDF'),
    ('reportes.clientes_frecuentes', 'exportar_excel', 'Exportar a Excel'),
    ('reportes.hora_dia_demanda', 'ver', 'Ver reporte de hora/día de demanda'),
    ('reportes.hora_dia_demanda', 'exportar_pdf', 'Exportar a PDF'),
    ('reportes.hora_dia_demanda', 'exportar_excel', 'Exportar a Excel');

-- Matriz por defecto — Módulo 12: TODAS las acciones → Jefe ✓ | Admin/Cajero/Mesero/Cocina — | PIN: no

-- ============================================================================
-- MÓDULO 13 — CONTABILIDAD  (Jefe✓Total ÚNICAMENTE)
-- ============================================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
    ('contabilidad.balance_general', 'ver', 'Ver balance general'),
    ('contabilidad.balance_general', 'exportar_pdf', 'Exportar a PDF'),
    ('contabilidad.balance_general', 'exportar_excel', 'Exportar a Excel'),
    ('contabilidad.flujo_caja', 'ver', 'Ver flujo de caja'),
    ('contabilidad.flujo_caja', 'exportar_pdf', 'Exportar a PDF'),
    ('contabilidad.flujo_caja', 'exportar_excel', 'Exportar a Excel'),
    ('contabilidad.transacciones', 'ver', 'Ver historial unificado de transacciones'),
    ('contabilidad.transacciones', 'exportar_pdf', 'Exportar a PDF'),
    ('contabilidad.transacciones', 'exportar_excel', 'Exportar a Excel');

-- Matriz por defecto — Módulo 13: TODAS las acciones → Jefe ✓ | Admin/Cajero/Mesero/Cocina — | PIN: no

-- ============================================================================
-- NOTA — Módulo 1 (Home) y Módulo 0 (bloqueo por suscripción)
-- No generan permisos propios: Home solo redirige a módulos ya cubiertos
-- arriba, y el bloqueo por suscripción es automático (no depende de rol).
--
-- NOTA — Módulo 14 (Panel Super Admin)
-- Fuera de este catálogo intencionalmente: `rol` (Jefe/Admin/Cajero/Mesero/
-- Cocina) y `permiso`/`rol_permiso` son el sistema RBAC de TENANT. Super
-- Admin es una entidad de plataforma separada (tabla `superadmin`), sin
-- relación con este sistema de permisos dinámicos.
-- ============================================================================
