-- ============================================================================
-- CaféPOS — Migracion V7
-- Reemplaza el enfoque manual de V5 (lista fija de 7 tablas) por un bucle
-- dinamico sobre TODAS las tablas con tenant_id, igual que ya hace el
-- bucle de RLS en V1. Es idempotente: re-otorgar sobre las tablas que V5
-- ya cubrio no causa ningun error ni efecto secundario.
--
-- Motivo: con la lista manual de V5, cualquier tabla nueva con tenant_id
-- que se agregue en el futuro (via una migracion Vn cualquiera) quedaria
-- SIN privilegios para app_platform hasta que alguien se acuerde de
-- escribir un GRANT especifico para ella - exactamente el mismo bug que
-- ya tuvimos dos veces. Este bucle lo hace automatico, para siempre.
-- ============================================================================

DO $$
DECLARE
    t TEXT;
BEGIN
    FOR t IN
        SELECT table_name FROM information_schema.columns
        WHERE column_name = 'tenant_id' AND table_schema = 'public'
    LOOP
        EXECUTE format('GRANT SELECT, INSERT, UPDATE, DELETE ON %I TO app_platform;', t);
    END LOOP;
END;
$$;

-- rol y permiso son catalogos globales de solo lectura (sin tenant_id),
-- el bucle de arriba no los cubre. Ya estaban otorgados en V5, se repite
-- aca por completitud del script y porque es idempotente.
GRANT SELECT ON rol, permiso TO app_platform;
