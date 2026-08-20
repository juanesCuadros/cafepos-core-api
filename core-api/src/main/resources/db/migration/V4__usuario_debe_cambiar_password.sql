-- ============================================================================
-- CaféPOS — Migración V4
-- Agrega usuario.debe_cambiar_password, necesaria para el flujo de
-- aprovisionamiento de negocios nuevos (admin-api: POST /admin/negocios).
--
-- Contexto: el Jefe recibe una contraseña temporal generada por el sistema
-- al crear su negocio. Mientras este flag sea true, el backend debe
-- rechazar (403) cualquier endpoint que no sea el de cambio de contraseña
-- — ver shared.seguridad para el filtro que aplica esta regla.
-- ============================================================================

ALTER TABLE usuario
    ADD COLUMN debe_cambiar_password BOOLEAN NOT NULL DEFAULT true;

COMMENT ON COLUMN usuario.debe_cambiar_password IS
    'true tras aprovisionamiento con contraseña temporal (admin-api). Bloquea todo endpoint salvo el de cambio de contraseña hasta que el usuario la cambie.';
