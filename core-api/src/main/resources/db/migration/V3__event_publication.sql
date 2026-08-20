-- ============================================================================
-- CaféPOS — Event Publication Registry (Spring Modulith)
--
-- Tabla requerida por spring-modulith-starter-jpa para persistir eventos de
-- aplicación entre módulos hasta que todos los listeners los procesan (ver
-- pom.xml, sección "Modularidad"). DDL oficial tomado de la documentación
-- de Spring Modulith (Appendix — Schema para PostgreSQL), versión 1.4.0.
--
-- Se gestiona vía Flyway (no vía spring.modulith.events.jdbc-schema-
-- initialization.enabled) porque, con spring.jpa.hibernate.ddl-auto=validate,
-- Hibernate valida el esquema ANTES de que la auto-inicialización de
-- Modulith llegue a crear esta tabla — ver application.yml.
-- ============================================================================

CREATE TABLE IF NOT EXISTS event_publication
(
    id                      UUID NOT NULL,
    listener_id             TEXT NOT NULL,
    event_type              TEXT NOT NULL,
    serialized_event        TEXT NOT NULL,
    publication_date        TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date         TIMESTAMP WITH TIME ZONE,
    status                  TEXT,
    completion_attempts     INT,
    last_resubmission_date  TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS event_publication_serialized_event_hash_idx
    ON event_publication USING hash (serialized_event);

CREATE INDEX IF NOT EXISTS event_publication_by_completion_date_idx
    ON event_publication (completion_date);

-- event_publication no tiene tenant_id (es la cola de eventos de TODO el
-- proceso, no de un tenant) -- el loop de GRANTs de V1__schema_v4.sql solo
-- cubre tablas con tenant_id, así que app_tenant (rol de runtime de
-- core-api, ver V1) necesita el GRANT explícito acá.
GRANT SELECT, INSERT, UPDATE, DELETE ON event_publication TO app_tenant;
