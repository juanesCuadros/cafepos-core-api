-- ============================================================================
-- CaféPOS — Esquema de Base de Datos (v4)
-- PostgreSQL 16+
--
-- v4 = v3 (aprobado) + tablas/columnas nuevas decididas durante el diseño de
-- arquitectura de backend (documento cafepos_arquitectura_backend_v1.md).
-- El schema aún no se ha ejecutado en ningún ambiente, así que los cambios
-- son ajustes de diseño, no migraciones — v4 reemplaza a v3 como fuente de
-- verdad, no lo migra.
--
-- CAMBIOS RESPECTO A v3:
--   1. superadmin: +totp_secret, +totp_habilitado (2FA obligatorio)
--   2. NUEVA TABLA superadmin_refresh_token (refresh tokens de Super Admin,
--      completamente aislados de los de tenant)
--   3. NUEVA TABLA tenant_permiso_config (PIN de step-up configurable por tenant)
--   4. NUEVA TABLA usuario_refresh_token (refresh tokens de usuarios de
--      tenant, rotados en cada uso)
--   5. NUEVA TABLA evento_auditoria (auditoría de negocio, append-only)
--   6. NUEVA TABLA evento_seguridad (intentos fallidos de login/PIN, reuso
--      de refresh token — separada de la auditoría de negocio)
--   7. Roles de base de datos app_tenant / app_platform + GRANTs, incluyendo
--      REVOKE UPDATE/DELETE sobre evento_auditoria (garantía append-only a
--      nivel de motor, no solo de disciplina en el código)
-- ============================================================================

-- ============================================================================
-- FUNCIÓN GENÉRICA PARA updated_at
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- ============================================================================
-- CAPA 1: PLATAFORMA (Super Admin — sin tenant_id)
-- ============================================================================
-- ============================================================================

-- ---------------------------------------------------------------------------
-- planes (catálogo de planes de suscripción)
-- ---------------------------------------------------------------------------
CREATE TABLE planes (
    id                  SERIAL PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,
    descripcion         VARCHAR(255),
    precio_mensual      DECIMAL(12,2) NOT NULL DEFAULT 0,
    limite_usuarios     INT,                              -- NULL = ilimitado
    estado              VARCHAR(20) NOT NULL DEFAULT 'activo'
                        CHECK (estado IN ('activo','inactivo')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------------
-- superadmin
-- v4: +totp_secret, +totp_habilitado — 2FA obligatorio (RNF de aislamiento
-- total del panel Super Admin). Secreto cifrado a nivel de aplicación.
-- ---------------------------------------------------------------------------
CREATE TABLE superadmin (
    id                  SERIAL PRIMARY KEY,
    nombre              VARCHAR(150) NOT NULL,
    correo              VARCHAR(150) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    totp_secret         VARCHAR(255),                     -- cifrado a nivel de aplicación (AES-256)
    totp_habilitado     BOOLEAN NOT NULL DEFAULT false,
    estado              VARCHAR(20) NOT NULL DEFAULT 'activo'
                        CHECK (estado IN ('activo','inactivo')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------------
-- superadmin_refresh_token (NUEVO v4)
-- Aislado de usuario_refresh_token a propósito: Super Admin no comparte
-- ninguna tabla de sesión con los usuarios de tenant.
-- ---------------------------------------------------------------------------
CREATE TABLE superadmin_refresh_token (
    id                  SERIAL PRIMARY KEY,
    superadmin_id       INT NOT NULL REFERENCES superadmin(id) ON DELETE CASCADE,
    token_hash          VARCHAR(255) NOT NULL,
    expira_en           TIMESTAMPTZ NOT NULL,
    ultimo_uso_en       TIMESTAMPTZ NOT NULL DEFAULT now(),
    revocado            BOOLEAN NOT NULL DEFAULT false,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_superadmin_refresh_superadmin ON superadmin_refresh_token(superadmin_id);

-- ---------------------------------------------------------------------------
-- tenants (capa plataforma — SOLO Super Admin escribe aquí)
-- La info del negocio que edita el Jefe vive en "restaurantes" (capa tenant)
-- ---------------------------------------------------------------------------
CREATE TABLE tenants (
    id                          SERIAL PRIMARY KEY,
    superadmin_aprobador_id     INT REFERENCES superadmin(id),
    plan_id                     INT REFERENCES planes(id),
    estado                      VARCHAR(20) NOT NULL DEFAULT 'prueba'
                                CHECK (estado IN ('activo','suspendido','prueba','cancelado')),
    fecha_registro               TIMESTAMPTZ NOT NULL DEFAULT now(),
    fecha_proxima_facturacion    DATE,
    created_at                   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------------
-- suscripciones_historial (log de activar/suspender/cancelar/cambiar plan)
-- ---------------------------------------------------------------------------
CREATE TABLE suscripciones_historial (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    estado_anterior     VARCHAR(20),
    estado_nuevo        VARCHAR(20) NOT NULL,
    plan_anterior_id    INT REFERENCES planes(id),
    plan_nuevo_id       INT REFERENCES planes(id),
    motivo              VARCHAR(255),
    superadmin_id       INT REFERENCES superadmin(id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_suscripciones_tenant ON suscripciones_historial(tenant_id);

-- ============================================================================
-- ============================================================================
-- CAPA 2: TENANT (todo el negocio de CaféPOS — con tenant_id, el Jefe escribe)
-- ============================================================================
-- ============================================================================

-- ---------------------------------------------------------------------------
-- restaurantes (info del negocio — el Jefe la edita desde Restaurante > Info general)
-- 1 fila por tenant. Separada de "tenants" a propósito (ver decisión de equipo #3)
-- ---------------------------------------------------------------------------
CREATE TABLE restaurantes (
    id                      SERIAL PRIMARY KEY,
    tenant_id               INT NOT NULL UNIQUE REFERENCES tenants(id) ON DELETE CASCADE,
    nombre_negocio          VARCHAR(150) NOT NULL,
    nit                     VARCHAR(30),
    direccion               VARCHAR(200),
    departamento            VARCHAR(100),
    ciudad                  VARCHAR(100),
    pais                    VARCHAR(100) DEFAULT 'Colombia',
    telefono                VARCHAR(30),
    correo                  VARCHAR(150),
    telefono_representante  VARCHAR(30),
    logo_url                VARCHAR(255),
    redes_sociales          VARCHAR(500),
    descripcion             VARCHAR(500),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------------
-- rol (catálogo global fijo — 5 roles predefinidos en V1)
-- ---------------------------------------------------------------------------
CREATE TABLE rol (
    id                  SERIAL PRIMARY KEY,
    nombre              VARCHAR(30) NOT NULL UNIQUE
                        CHECK (nombre IN ('Jefe','Admin','Cajero','Mesero','Cocina')),
    es_editable         BOOLEAN NOT NULL DEFAULT true,     -- Jefe = false (no se puede modificar)
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO rol (nombre, es_editable) VALUES
    ('Jefe', false), ('Admin', true), ('Cajero', true), ('Mesero', true), ('Cocina', true);

-- ---------------------------------------------------------------------------
-- permiso (catálogo global de módulo+acción disponibles)
-- Poblado según cafepos_catalogo_permisos_v1.sql
-- ---------------------------------------------------------------------------
CREATE TABLE permiso (
    id                  SERIAL PRIMARY KEY,
    modulo              VARCHAR(100) NOT NULL,
    accion              VARCHAR(100) NOT NULL,
    descripcion         VARCHAR(200),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (modulo, accion)
);

-- ---------------------------------------------------------------------------
-- rol_permiso (matriz de permisos editable por tenant, vía checkboxes)
-- ---------------------------------------------------------------------------
CREATE TABLE rol_permiso (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    rol_id              INT NOT NULL REFERENCES rol(id),
    permiso_id          INT NOT NULL REFERENCES permiso(id),
    activo              BOOLEAN NOT NULL DEFAULT false,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, rol_id, permiso_id)
);

CREATE INDEX idx_rol_permiso_tenant ON rol_permiso(tenant_id);

-- ---------------------------------------------------------------------------
-- tenant_permiso_config (NUEVO v4)
-- PIN de step-up configurable por tenant. No vive en "permiso" (catálogo
-- global, compartido por todos los tenants) ni en "rol_permiso" (atado a un
-- rol específico) porque el requisito de PIN es una propiedad de LA ACCIÓN
-- para ese tenant, sin importar qué rol la ejecute.
-- DEFAULT true porque el comportamiento base es el que ya describe
-- cafepos_MASTER.md; el Jefe solo la toca para aflojar el candado o
-- agregarlo a una acción que hoy no lo pide.
-- ---------------------------------------------------------------------------
CREATE TABLE tenant_permiso_config (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    permiso_id          INT NOT NULL REFERENCES permiso(id),
    requiere_pin        BOOLEAN NOT NULL DEFAULT true,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, permiso_id)
);

CREATE INDEX idx_tenant_permiso_config_tenant ON tenant_permiso_config(tenant_id);

-- ---------------------------------------------------------------------------
-- empleados
-- ---------------------------------------------------------------------------
CREATE TABLE empleado (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    codigo              VARCHAR(20) NOT NULL,
    nombre              VARCHAR(150) NOT NULL,
    cedula              VARCHAR(20) NOT NULL,
    cargo               VARCHAR(100),
    telefono            VARCHAR(30),
    estado              VARCHAR(20) NOT NULL DEFAULT 'activo'
                        CHECK (estado IN ('activo','inactivo')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo),
    UNIQUE (tenant_id, cedula)
);

CREATE INDEX idx_empleado_tenant ON empleado(tenant_id);

-- ---------------------------------------------------------------------------
-- usuario (acceso al sistema — entidad separada de empleado)
-- ---------------------------------------------------------------------------
CREATE TABLE usuario (
    id                      SERIAL PRIMARY KEY,
    tenant_id               INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    empleado_id             INT REFERENCES empleado(id) ON DELETE SET NULL,
    rol_id                  INT NOT NULL REFERENCES rol(id),
    nombre                  VARCHAR(150) NOT NULL,
    correo                  VARCHAR(150) NOT NULL,
    password_hash           VARCHAR(255) NOT NULL,
    pin_autorizacion_hash    VARCHAR(255),                 -- solo Admin/Jefe. Hasheado (RNF-010)
    estado                   VARCHAR(20) NOT NULL DEFAULT 'activo'
                             CHECK (estado IN ('activo','inactivo')),
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, correo)
);

CREATE INDEX idx_usuario_tenant ON usuario(tenant_id);
CREATE INDEX idx_usuario_rol ON usuario(rol_id);

-- ---------------------------------------------------------------------------
-- usuario_refresh_token (NUEVO v4)
-- Rotado en cada uso: al refrescar, se invalida el anterior y se emite uno
-- nuevo. Reutilizar un token ya rotado es señal de robo (ver evento_seguridad,
-- tipo_evento = 'refresh_token_reuso'). ultimo_uso_en también se usa para
-- aplicar tenant_rol_config.minutos_inactividad sin mantener sesión server-side.
-- ---------------------------------------------------------------------------
CREATE TABLE usuario_refresh_token (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    usuario_id          INT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    token_hash          VARCHAR(255) NOT NULL,
    expira_en           TIMESTAMPTZ NOT NULL,
    ultimo_uso_en       TIMESTAMPTZ NOT NULL DEFAULT now(),
    revocado            BOOLEAN NOT NULL DEFAULT false,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_usuario_refresh_tenant ON usuario_refresh_token(tenant_id);
CREATE INDEX idx_usuario_refresh_usuario ON usuario_refresh_token(usuario_id);

-- ---------------------------------------------------------------------------
-- configuracion_sistema (1 fila por tenant)
-- ---------------------------------------------------------------------------
CREATE TABLE configuracion_sistema (
    id                          SERIAL PRIMARY KEY,
    tenant_id                   INT NOT NULL UNIQUE REFERENCES tenants(id) ON DELETE CASCADE,
    modo_comanda                 VARCHAR(20) NOT NULL DEFAULT 'impresora'
                                 CHECK (modo_comanda IN ('kds','impresora','ambas')),
    tiempo_limite_prep_min        INT DEFAULT 15,
    propina_tipo                  VARCHAR(20) NOT NULL DEFAULT 'opcional'
                                  CHECK (propina_tipo IN ('obligatoria','opcional')),
    propina_porcentaje             DECIMAL(5,2) DEFAULT 10,
    propina_destino                VARCHAR(20) DEFAULT 'restaurante'
                                   CHECK (propina_destino IN ('restaurante','mesero','mixto')),
    propina_pct_mesero              DECIMAL(5,2),
    dias_anticipacion_vencim         INT DEFAULT 7,
    estado_conexion_dian              VARCHAR(20) DEFAULT 'inactiva'
                                      CHECK (estado_conexion_dian IN ('activa','inactiva','error')),
    created_at                        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------------
-- tenant_rol_config (tiempo de inactividad de sesión por rol)
-- ---------------------------------------------------------------------------
CREATE TABLE tenant_rol_config (
    id                      SERIAL PRIMARY KEY,
    tenant_id               INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    rol_id                  INT NOT NULL REFERENCES rol(id),
    minutos_inactividad     INT NOT NULL DEFAULT 15,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, rol_id)
);

-- ---------------------------------------------------------------------------
-- area_cocina
-- ---------------------------------------------------------------------------
CREATE TABLE area_cocina (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    nombre              VARCHAR(100) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'activa'
                        CHECK (estado IN ('activa','inactiva')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_area_cocina_tenant ON area_cocina(tenant_id);

-- ---------------------------------------------------------------------------
-- impresora
-- ---------------------------------------------------------------------------
CREATE TABLE impresora (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    area_cocina_id      INT REFERENCES area_cocina(id),
    tipo                VARCHAR(20) NOT NULL CHECK (tipo IN ('caja','cocina')),
    nombre              VARCHAR(100),
    tipo_conexion       VARCHAR(10) NOT NULL CHECK (tipo_conexion IN ('ip','usb')),
    ip                  VARCHAR(50),
    puerto              INT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_impresora_tenant ON impresora(tenant_id);

-- ---------------------------------------------------------------------------
-- zona
-- ---------------------------------------------------------------------------
CREATE TABLE zona (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    codigo              VARCHAR(20) NOT NULL,
    icono               VARCHAR(50),
    nombre              VARCHAR(100) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'activa'
                        CHECK (estado IN ('activa','inactiva')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);

CREATE INDEX idx_zona_tenant ON zona(tenant_id);

-- ---------------------------------------------------------------------------
-- mesa
-- ---------------------------------------------------------------------------
CREATE TABLE mesa (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    zona_id             INT NOT NULL REFERENCES zona(id),
    codigo              VARCHAR(20) NOT NULL,
    numero              VARCHAR(20) NOT NULL,
    capacidad           INT NOT NULL DEFAULT 1,
    estado              VARCHAR(30) NOT NULL DEFAULT 'libre'
                        CHECK (estado IN ('libre','ocupada','lista_cobrar','deshabilitada')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);

CREATE INDEX idx_mesa_tenant ON mesa(tenant_id);
CREATE INDEX idx_mesa_zona ON mesa(zona_id);
CREATE INDEX idx_mesa_estado ON mesa(tenant_id, estado);

-- ---------------------------------------------------------------------------
-- metodo_pago
-- ---------------------------------------------------------------------------
CREATE TABLE metodo_pago (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    nombre              VARCHAR(50) NOT NULL,
    icono               VARCHAR(50),
    es_efectivo         BOOLEAN NOT NULL DEFAULT false,
    estado              VARCHAR(20) NOT NULL DEFAULT 'activo'
                        CHECK (estado IN ('activo','inactivo')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_metodo_pago_tenant ON metodo_pago(tenant_id);

-- ---------------------------------------------------------------------------
-- facturacion_dian_resolucion (histórico 1-a-N por tenant)
-- ---------------------------------------------------------------------------
CREATE TABLE facturacion_dian_resolucion (
    id                      SERIAL PRIMARY KEY,
    tenant_id               INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    prefijo                 VARCHAR(10),
    rango_inicio            BIGINT,
    rango_fin                BIGINT,
    numeracion_actual         BIGINT DEFAULT 0,
    fecha_expedicion           DATE,
    fecha_vencimiento          DATE,
    ambiente                    VARCHAR(20) DEFAULT 'pruebas'
                                CHECK (ambiente IN ('pruebas','produccion')),
    estado                      VARCHAR(20) DEFAULT 'vigente'
                                CHECK (estado IN ('vigente','vencida','agotada')),
    client_id_factus              VARCHAR(255),
    client_secret_factus          VARCHAR(255),
    created_at                     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_facturacion_dian_tenant ON facturacion_dian_resolucion(tenant_id);

-- ---------------------------------------------------------------------------
-- menu_digital_config (1 fila por tenant)
-- ---------------------------------------------------------------------------
CREATE TABLE menu_digital_config (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL UNIQUE REFERENCES tenants(id) ON DELETE CASCADE,
    url_publica         VARCHAR(255),
    activo              BOOLEAN NOT NULL DEFAULT false,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------------
-- turno
-- ---------------------------------------------------------------------------
CREATE TABLE turno (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    empleado_id         INT NOT NULL REFERENCES empleado(id),
    usuario_id          INT REFERENCES usuario(id),
    fecha               DATE NOT NULL,
    hora_inicio         TIMESTAMPTZ NOT NULL,
    hora_fin            TIMESTAMPTZ,
    horas_trabajadas    DECIMAL(5,2),
    observaciones       VARCHAR(255),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_turno_tenant ON turno(tenant_id);
CREATE INDEX idx_turno_empleado ON turno(empleado_id);

-- ---------------------------------------------------------------------------
-- cliente
-- ---------------------------------------------------------------------------
CREATE TABLE cliente (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    codigo              VARCHAR(20) NOT NULL,
    tipo_documento      VARCHAR(10) NOT NULL CHECK (tipo_documento IN ('CC','NIT')),
    numero_documento    VARCHAR(20) NOT NULL,
    nombre              VARCHAR(150) NOT NULL,
    telefono            VARCHAR(30),
    correo              VARCHAR(150),
    direccion           VARCHAR(200),
    saldo_favor         DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo),
    UNIQUE (tenant_id, tipo_documento, numero_documento)
);

CREATE INDEX idx_cliente_tenant ON cliente(tenant_id);
CREATE INDEX idx_cliente_documento ON cliente(tenant_id, numero_documento);

-- ---------------------------------------------------------------------------
-- cliente_saldo_movimiento
-- ---------------------------------------------------------------------------
CREATE TABLE cliente_saldo_movimiento (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    cliente_id          INT NOT NULL REFERENCES cliente(id),
    usuario_id          INT REFERENCES usuario(id),
    tipo                VARCHAR(10) NOT NULL CHECK (tipo IN ('credito','debito')),
    monto               DECIMAL(12,2) NOT NULL,
    origen_tipo         VARCHAR(50),
    origen_id           INT,
    fecha               TIMESTAMPTZ NOT NULL DEFAULT now(),
    descripcion         VARCHAR(255)
);

CREATE INDEX idx_cliente_saldo_tenant ON cliente_saldo_movimiento(tenant_id);
CREATE INDEX idx_cliente_saldo_cliente ON cliente_saldo_movimiento(cliente_id);

-- ---------------------------------------------------------------------------
-- categoria (productos y menú)
-- ---------------------------------------------------------------------------
CREATE TABLE categoria (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    icono               VARCHAR(50),
    nombre              VARCHAR(100) NOT NULL,
    descripcion         VARCHAR(255),
    orden               INT NOT NULL DEFAULT 0,
    estado              VARCHAR(20) NOT NULL DEFAULT 'activa'
                        CHECK (estado IN ('activa','inactiva')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_categoria_tenant ON categoria(tenant_id);

-- ---------------------------------------------------------------------------
-- producto
-- ---------------------------------------------------------------------------
CREATE TABLE producto (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    categoria_id        INT NOT NULL REFERENCES categoria(id),
    area_cocina_id      INT REFERENCES area_cocina(id),
    codigo              VARCHAR(20) NOT NULL,
    imagen              VARCHAR(255),
    nombre              VARCHAR(150) NOT NULL,
    descripcion         VARCHAR(255),
    precio_venta        DECIMAL(12,2) NOT NULL DEFAULT 0,
    costo_estimado      DECIMAL(12,2),
    tasa_impuesto       VARCHAR(20),
    maneja_receta       BOOLEAN NOT NULL DEFAULT false,
    maneja_inventario   BOOLEAN NOT NULL DEFAULT false,
    unidad_medida       VARCHAR(20),
    stock_actual        DECIMAL(12,3),
    stock_minimo        DECIMAL(12,3),
    estado              VARCHAR(20) NOT NULL DEFAULT 'activo'
                        CHECK (estado IN ('activo','inactivo','agotado')),
    visibilidad         VARCHAR(20) NOT NULL DEFAULT 'visible'
                        CHECK (visibilidad IN ('visible','oculto')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);

CREATE INDEX idx_producto_tenant ON producto(tenant_id);
CREATE INDEX idx_producto_categoria ON producto(categoria_id);
CREATE INDEX idx_producto_estado ON producto(tenant_id, estado);

-- ---------------------------------------------------------------------------
-- combo
-- ---------------------------------------------------------------------------
CREATE TABLE combo (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    codigo              VARCHAR(20) NOT NULL,
    imagen              VARCHAR(255),
    nombre              VARCHAR(150) NOT NULL,
    descripcion         VARCHAR(255),
    precio              DECIMAL(12,2) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'activo'
                        CHECK (estado IN ('activo','inactivo')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);

CREATE INDEX idx_combo_tenant ON combo(tenant_id);

-- ---------------------------------------------------------------------------
-- combo_grupo
-- ---------------------------------------------------------------------------
CREATE TABLE combo_grupo (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    combo_id            INT NOT NULL REFERENCES combo(id) ON DELETE CASCADE,
    nombre              VARCHAR(100) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_combo_grupo_tenant ON combo_grupo(tenant_id);
CREATE INDEX idx_combo_grupo_combo ON combo_grupo(combo_id);

-- ---------------------------------------------------------------------------
-- combo_grupo_producto
-- ---------------------------------------------------------------------------
CREATE TABLE combo_grupo_producto (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    combo_grupo_id      INT NOT NULL REFERENCES combo_grupo(id) ON DELETE CASCADE,
    producto_id         INT NOT NULL REFERENCES producto(id),
    UNIQUE (combo_grupo_id, producto_id)
);

CREATE INDEX idx_combo_grupo_producto_tenant ON combo_grupo_producto(tenant_id);

-- ---------------------------------------------------------------------------
-- promocion
-- ---------------------------------------------------------------------------
CREATE TABLE promocion (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    nombre              VARCHAR(150) NOT NULL,
    tipo_descuento      VARCHAR(20) NOT NULL CHECK (tipo_descuento IN ('porcentaje','valor_fijo')),
    valor_descuento     DECIMAL(12,2) NOT NULL,
    aplica_a            VARCHAR(20) NOT NULL CHECK (aplica_a IN ('producto','venta_total')),
    vigencia_inicio     DATE NOT NULL,
    vigencia_fin        DATE NOT NULL,
    dias_semana         VARCHAR(50),
    hora_inicio         TIME,
    hora_fin            TIME,
    cantidad_minima     INT,
    monto_minimo        DECIMAL(12,2),
    estado              VARCHAR(20) NOT NULL DEFAULT 'activa'
                        CHECK (estado IN ('activa','inactiva')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_promocion_tenant ON promocion(tenant_id);
CREATE INDEX idx_promocion_vigencia ON promocion(tenant_id, vigencia_inicio, vigencia_fin);

-- ---------------------------------------------------------------------------
-- promocion_producto
-- ---------------------------------------------------------------------------
CREATE TABLE promocion_producto (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    promocion_id        INT NOT NULL REFERENCES promocion(id) ON DELETE CASCADE,
    producto_id         INT NOT NULL REFERENCES producto(id),
    UNIQUE (promocion_id, producto_id)
);

CREATE INDEX idx_promocion_producto_tenant ON promocion_producto(tenant_id);

-- ---------------------------------------------------------------------------
-- categoria_gasto
-- ---------------------------------------------------------------------------
CREATE TABLE categoria_gasto (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    nombre              VARCHAR(100) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'activa'
                        CHECK (estado IN ('activa','inactiva')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_categoria_gasto_tenant ON categoria_gasto(tenant_id);

-- ---------------------------------------------------------------------------
-- gasto
-- ---------------------------------------------------------------------------
CREATE TABLE gasto (
    id                      SERIAL PRIMARY KEY,
    tenant_id               INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    categoria_gasto_id       INT NOT NULL REFERENCES categoria_gasto(id),
    usuario_id                INT NOT NULL REFERENCES usuario(id),
    codigo                     VARCHAR(20) NOT NULL,
    descripcion                 VARCHAR(255) NOT NULL,
    monto                        DECIMAL(12,2) NOT NULL,
    metodo_pago                   VARCHAR(50) NOT NULL,
    fecha                          DATE NOT NULL,
    comprobante_imagen              VARCHAR(255),
    observaciones                    VARCHAR(255),
    created_at                        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                        TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);

CREATE INDEX idx_gasto_tenant ON gasto(tenant_id);
CREATE INDEX idx_gasto_fecha ON gasto(tenant_id, fecha);

-- ---------------------------------------------------------------------------
-- categoria_insumo
-- ---------------------------------------------------------------------------
CREATE TABLE categoria_insumo (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    nombre              VARCHAR(100) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_categoria_insumo_tenant ON categoria_insumo(tenant_id);

-- ---------------------------------------------------------------------------
-- insumo
-- ---------------------------------------------------------------------------
CREATE TABLE insumo (
    id                          SERIAL PRIMARY KEY,
    tenant_id                   INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    categoria_insumo_id         INT NOT NULL REFERENCES categoria_insumo(id),
    codigo                      VARCHAR(20) NOT NULL,
    nombre                      VARCHAR(150) NOT NULL,
    unidad_medida               VARCHAR(20) NOT NULL,
    stock_actual                DECIMAL(12,3) NOT NULL DEFAULT 0,
    stock_minimo                DECIMAL(12,3) DEFAULT 0,
    stock_maximo                DECIMAL(12,3),
    costo_actual                DECIMAL(12,2) NOT NULL DEFAULT 0,
    fecha_vencim_ref             DATE,
    fecha_registro                TIMESTAMPTZ NOT NULL DEFAULT now(),
    estado                        VARCHAR(20) NOT NULL DEFAULT 'activo'
                                  CHECK (estado IN ('activo','inactivo')),
    updated_at                     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);

CREATE INDEX idx_insumo_tenant ON insumo(tenant_id);
CREATE INDEX idx_insumo_estado ON insumo(tenant_id, estado);

-- ---------------------------------------------------------------------------
-- receta (1 receta por producto)
-- ---------------------------------------------------------------------------
CREATE TABLE receta (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    producto_id         INT NOT NULL UNIQUE REFERENCES producto(id) ON DELETE CASCADE,
    costo_total         DECIMAL(12,2) NOT NULL DEFAULT 0,
    estado              VARCHAR(20) NOT NULL DEFAULT 'activa'
                        CHECK (estado IN ('activa','inactiva')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_receta_tenant ON receta(tenant_id);

-- ---------------------------------------------------------------------------
-- receta_insumo
-- ---------------------------------------------------------------------------
CREATE TABLE receta_insumo (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    receta_id           INT NOT NULL REFERENCES receta(id) ON DELETE CASCADE,
    insumo_id           INT NOT NULL REFERENCES insumo(id),
    cantidad            DECIMAL(12,3) NOT NULL,
    unidad_medida       VARCHAR(20) NOT NULL,
    UNIQUE (receta_id, insumo_id)
);

CREATE INDEX idx_receta_insumo_tenant ON receta_insumo(tenant_id);

-- ---------------------------------------------------------------------------
-- proveedor
-- ---------------------------------------------------------------------------
CREATE TABLE proveedor (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    codigo              VARCHAR(20) NOT NULL,
    nombre              VARCHAR(150) NOT NULL,
    nit                 VARCHAR(30),
    contacto            VARCHAR(150),
    telefono            VARCHAR(30),
    correo              VARCHAR(150),
    direccion           VARCHAR(200),
    estado              VARCHAR(20) NOT NULL DEFAULT 'activo'
                        CHECK (estado IN ('activo','inactivo')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);

CREATE INDEX idx_proveedor_tenant ON proveedor(tenant_id);

-- ---------------------------------------------------------------------------
-- compra
-- ---------------------------------------------------------------------------
CREATE TABLE compra (
    id                          SERIAL PRIMARY KEY,
    tenant_id                   INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    proveedor_id                 INT NOT NULL REFERENCES proveedor(id),
    usuario_id                    INT NOT NULL REFERENCES usuario(id),
    codigo                          VARCHAR(20) NOT NULL,
    numero_factura_prov              VARCHAR(50),
    fecha                              DATE NOT NULL,
    forma_pago                          VARCHAR(20) NOT NULL CHECK (forma_pago IN ('contado','credito')),
    estado                                VARCHAR(20) NOT NULL DEFAULT 'pagada'
                                         CHECK (estado IN ('pagada','pendiente')),
    observaciones                          VARCHAR(255),
    total                                    DECIMAL(14,2) NOT NULL DEFAULT 0,
    created_at                                TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                                 TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);

CREATE INDEX idx_compra_tenant ON compra(tenant_id);
CREATE INDEX idx_compra_proveedor ON compra(proveedor_id);
CREATE INDEX idx_compra_fecha ON compra(tenant_id, fecha);

-- ---------------------------------------------------------------------------
-- compra_detalle
-- ---------------------------------------------------------------------------
CREATE TABLE compra_detalle (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    compra_id           INT NOT NULL REFERENCES compra(id) ON DELETE CASCADE,
    insumo_id           INT NOT NULL REFERENCES insumo(id),
    cantidad            DECIMAL(12,3) NOT NULL,
    costo_unitario      DECIMAL(12,2) NOT NULL,
    numero_lote         VARCHAR(50),
    fecha_vencimiento   DATE,
    subtotal            DECIMAL(14,2) NOT NULL
);

CREATE INDEX idx_compra_detalle_tenant ON compra_detalle(tenant_id);
CREATE INDEX idx_compra_detalle_compra ON compra_detalle(compra_id);
CREATE INDEX idx_compra_detalle_insumo ON compra_detalle(insumo_id);

-- ---------------------------------------------------------------------------
-- lote_insumo
-- ---------------------------------------------------------------------------
CREATE TABLE lote_insumo (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    insumo_id           INT NOT NULL REFERENCES insumo(id),
    compra_detalle_id   INT REFERENCES compra_detalle(id),
    numero_lote         VARCHAR(50) NOT NULL,
    fecha_vencimiento   DATE,
    cantidad_inicial    DECIMAL(12,3) NOT NULL,
    cantidad_actual     DECIMAL(12,3) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_lote_insumo_tenant ON lote_insumo(tenant_id);
CREATE INDEX idx_lote_insumo_insumo ON lote_insumo(insumo_id);
CREATE INDEX idx_lote_insumo_vencimiento ON lote_insumo(tenant_id, fecha_vencimiento);

-- ---------------------------------------------------------------------------
-- movimiento_inventario
-- ---------------------------------------------------------------------------
CREATE TABLE movimiento_inventario (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    insumo_id           INT REFERENCES insumo(id),
    producto_id         INT REFERENCES producto(id),
    usuario_id          INT REFERENCES usuario(id),
    tipo                VARCHAR(20) NOT NULL
                        CHECK (tipo IN ('entrada','salida','ajuste','perdida','ajuste_conteo')),
    cantidad            DECIMAL(12,3) NOT NULL,
    motivo_origen       VARCHAR(150),
    referencia_tipo     VARCHAR(50),
    referencia_id       INT,
    fecha_hora          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK ( (insumo_id IS NOT NULL AND producto_id IS NULL)
         OR (insumo_id IS NULL AND producto_id IS NOT NULL) )
);

CREATE INDEX idx_movimiento_tenant ON movimiento_inventario(tenant_id);
CREATE INDEX idx_movimiento_insumo ON movimiento_inventario(insumo_id);
CREATE INDEX idx_movimiento_producto ON movimiento_inventario(producto_id);
CREATE INDEX idx_movimiento_tipo ON movimiento_inventario(tenant_id, tipo);

-- ---------------------------------------------------------------------------
-- conteo
-- ---------------------------------------------------------------------------
CREATE TABLE conteo (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    usuario_id          INT NOT NULL REFERENCES usuario(id),
    fecha               TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_conteo_tenant ON conteo(tenant_id);

-- ---------------------------------------------------------------------------
-- conteo_detalle
-- ---------------------------------------------------------------------------
CREATE TABLE conteo_detalle (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    conteo_id           INT NOT NULL REFERENCES conteo(id) ON DELETE CASCADE,
    insumo_id           INT NOT NULL REFERENCES insumo(id),
    stock_sistema       DECIMAL(12,3) NOT NULL,
    stock_fisico        DECIMAL(12,3) NOT NULL,
    diferencia          DECIMAL(12,3) NOT NULL
);

CREATE INDEX idx_conteo_detalle_tenant ON conteo_detalle(tenant_id);
CREATE INDEX idx_conteo_detalle_conteo ON conteo_detalle(conteo_id);

-- ---------------------------------------------------------------------------
-- perdida
-- ---------------------------------------------------------------------------
CREATE TABLE perdida (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    insumo_id           INT NOT NULL REFERENCES insumo(id),
    usuario_id          INT NOT NULL REFERENCES usuario(id),
    cantidad            DECIMAL(12,3) NOT NULL,
    motivo              VARCHAR(150) NOT NULL,
    fecha               DATE NOT NULL,
    observaciones       VARCHAR(255),
    costo_calculado     DECIMAL(12,2) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_perdida_tenant ON perdida(tenant_id);
CREATE INDEX idx_perdida_fecha ON perdida(tenant_id, fecha);

-- ---------------------------------------------------------------------------
-- pedido
-- ---------------------------------------------------------------------------
CREATE TABLE pedido (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    mesa_id             INT REFERENCES mesa(id),
    usuario_id          INT NOT NULL REFERENCES usuario(id),
    tipo                VARCHAR(20) NOT NULL CHECK (tipo IN ('mesa','venta_rapida')),
    numero_orden        VARCHAR(20) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'abierto'
                        CHECK (estado IN ('abierto','enviado','listo','cerrado')),
    fecha_apertura      TIMESTAMPTZ NOT NULL DEFAULT now(),
    fecha_cierre        TIMESTAMPTZ,
    UNIQUE (tenant_id, numero_orden)
);

CREATE INDEX idx_pedido_tenant ON pedido(tenant_id);
CREATE INDEX idx_pedido_mesa ON pedido(mesa_id);
CREATE INDEX idx_pedido_estado ON pedido(tenant_id, estado);

-- ---------------------------------------------------------------------------
-- pedido_item
-- ---------------------------------------------------------------------------
CREATE TABLE pedido_item (
    id                      SERIAL PRIMARY KEY,
    tenant_id               INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    pedido_id               INT NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
    producto_id             INT REFERENCES producto(id),
    combo_id                INT REFERENCES combo(id),
    area_cocina_id          INT REFERENCES area_cocina(id),
    cantidad                DECIMAL(12,3) NOT NULL DEFAULT 1,
    observacion             VARCHAR(255),
    precio_unitario         DECIMAL(12,2) NOT NULL,
    impuesto_unitario       DECIMAL(12,2),
    estado_preparacion      VARCHAR(20) NOT NULL DEFAULT 'pendiente'
                            CHECK (estado_preparacion IN ('pendiente','en_preparacion','listo')),
    CHECK ( (producto_id IS NOT NULL AND combo_id IS NULL)
         OR (producto_id IS NULL AND combo_id IS NOT NULL) )
);

CREATE INDEX idx_pedido_item_tenant ON pedido_item(tenant_id);
CREATE INDEX idx_pedido_item_pedido ON pedido_item(pedido_id);

-- ---------------------------------------------------------------------------
-- pedido_item_combo_seleccion
-- ---------------------------------------------------------------------------
CREATE TABLE pedido_item_combo_seleccion (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    pedido_item_id      INT NOT NULL REFERENCES pedido_item(id) ON DELETE CASCADE,
    combo_grupo_id      INT NOT NULL REFERENCES combo_grupo(id),
    producto_id         INT NOT NULL REFERENCES producto(id)
);

CREATE INDEX idx_pedido_item_combo_tenant ON pedido_item_combo_seleccion(tenant_id);
CREATE INDEX idx_pedido_item_combo_item ON pedido_item_combo_seleccion(pedido_item_id);

-- ---------------------------------------------------------------------------
-- caja_jornada
-- Regla de negocio: solo una jornada 'abierta' por tenant a la vez (RN-011)
-- ---------------------------------------------------------------------------
CREATE TABLE caja_jornada (
    id                      SERIAL PRIMARY KEY,
    tenant_id               INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    usuario_apertura_id      INT NOT NULL REFERENCES usuario(id),
    usuario_cierre_id         INT REFERENCES usuario(id),
    fecha_apertura              TIMESTAMPTZ NOT NULL DEFAULT now(),
    fecha_cierre                 TIMESTAMPTZ,
    monto_inicial                  DECIMAL(12,2) NOT NULL DEFAULT 0,
    monto_final_sistema              DECIMAL(12,2),
    monto_final_fisico                 DECIMAL(12,2),
    diferencia                           DECIMAL(12,2),
    estado                                 VARCHAR(20) NOT NULL DEFAULT 'abierta'
                                           CHECK (estado IN ('abierta','cerrada'))
);

CREATE INDEX idx_caja_jornada_tenant ON caja_jornada(tenant_id);
CREATE UNIQUE INDEX idx_caja_jornada_una_abierta ON caja_jornada(tenant_id) WHERE estado = 'abierta';

-- ---------------------------------------------------------------------------
-- caja_movimiento
-- ---------------------------------------------------------------------------
CREATE TABLE caja_movimiento (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    jornada_id          INT NOT NULL REFERENCES caja_jornada(id) ON DELETE CASCADE,
    usuario_id          INT NOT NULL REFERENCES usuario(id),
    tipo                VARCHAR(20) NOT NULL CHECK (tipo IN ('ingreso','egreso')),
    monto               DECIMAL(12,2) NOT NULL,
    motivo              VARCHAR(255) NOT NULL,
    fecha_hora          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_caja_movimiento_tenant ON caja_movimiento(tenant_id);
CREATE INDEX idx_caja_movimiento_jornada ON caja_movimiento(jornada_id);

-- ---------------------------------------------------------------------------
-- venta
-- ---------------------------------------------------------------------------
CREATE TABLE venta (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    pedido_id           INT NOT NULL REFERENCES pedido(id),
    jornada_id          INT NOT NULL REFERENCES caja_jornada(id),
    cliente_id          INT REFERENCES cliente(id),
    cajero_id           INT NOT NULL REFERENCES usuario(id),
    codigo              VARCHAR(20) NOT NULL,
    subtotal            DECIMAL(14,2) NOT NULL,
    descuento_total     DECIMAL(12,2) NOT NULL DEFAULT 0,
    impuestos           DECIMAL(12,2) NOT NULL DEFAULT 0,
    propina             DECIMAL(12,2) NOT NULL DEFAULT 0,
    total               DECIMAL(14,2) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'cobrado'
                        CHECK (estado IN ('cobrado','anulado')),
    fecha_hora          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, codigo)
);

CREATE INDEX idx_venta_tenant ON venta(tenant_id);
CREATE INDEX idx_venta_jornada ON venta(jornada_id);
CREATE INDEX idx_venta_cliente ON venta(cliente_id);
CREATE INDEX idx_venta_fecha ON venta(tenant_id, fecha_hora);
CREATE INDEX idx_venta_cajero ON venta(cajero_id);

-- ---------------------------------------------------------------------------
-- venta_pago
-- ---------------------------------------------------------------------------
CREATE TABLE venta_pago (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    venta_id            INT NOT NULL REFERENCES venta(id) ON DELETE CASCADE,
    metodo_pago_id      INT NOT NULL REFERENCES metodo_pago(id),
    monto               DECIMAL(12,2) NOT NULL
);

CREATE INDEX idx_venta_pago_tenant ON venta_pago(tenant_id);
CREATE INDEX idx_venta_pago_venta ON venta_pago(venta_id);

-- ---------------------------------------------------------------------------
-- venta_promocion
-- ---------------------------------------------------------------------------
CREATE TABLE venta_promocion (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    venta_id            INT NOT NULL REFERENCES venta(id) ON DELETE CASCADE,
    promocion_id        INT NOT NULL REFERENCES promocion(id),
    monto_descuento     DECIMAL(12,2) NOT NULL
);

CREATE INDEX idx_venta_promocion_tenant ON venta_promocion(tenant_id);
CREATE INDEX idx_venta_promocion_venta ON venta_promocion(venta_id);

-- ---------------------------------------------------------------------------
-- factura_dian
-- ---------------------------------------------------------------------------
CREATE TABLE factura_dian (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    venta_id            INT NOT NULL UNIQUE REFERENCES venta(id),
    resolucion_id       INT REFERENCES facturacion_dian_resolucion(id),
    numero_factura      VARCHAR(50) NOT NULL,
    cufe                VARCHAR(255),
    qr_code             VARCHAR(255),
    estado_dian         VARCHAR(20) NOT NULL DEFAULT 'pendiente'
                        CHECK (estado_dian IN ('emitida','aceptada','rechazada','pendiente')),
    motivo_rechazo      VARCHAR(255),
    fecha_emision       TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, numero_factura)
);

CREATE INDEX idx_factura_dian_tenant ON factura_dian(tenant_id);
CREATE INDEX idx_factura_dian_estado ON factura_dian(tenant_id, estado_dian);

-- ---------------------------------------------------------------------------
-- nota_credito
-- ---------------------------------------------------------------------------
CREATE TABLE nota_credito (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    factura_id          INT NOT NULL REFERENCES factura_dian(id),
    devolucion_id       INT,
    motivo              VARCHAR(255) NOT NULL,
    monto               DECIMAL(12,2) NOT NULL,
    fecha               TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_nota_credito_tenant ON nota_credito(tenant_id);
CREATE INDEX idx_nota_credito_factura ON nota_credito(factura_id);

-- ---------------------------------------------------------------------------
-- devolucion
-- ---------------------------------------------------------------------------
CREATE TABLE devolucion (
    id                      SERIAL PRIMARY KEY,
    tenant_id               INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    venta_id                INT NOT NULL REFERENCES venta(id),
    usuario_autoriza_id      INT NOT NULL REFERENCES usuario(id),
    motivo                    VARCHAR(255) NOT NULL,
    monto_devuelto              DECIMAL(12,2) NOT NULL,
    metodo_reembolso              VARCHAR(20) NOT NULL
                                  CHECK (metodo_reembolso IN ('pago_original','saldo_favor')),
    estado                        VARCHAR(20) NOT NULL DEFAULT 'pendiente'
                                  CHECK (estado IN ('pendiente','aprobada','rechazada')),
    fecha                          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_devolucion_tenant ON devolucion(tenant_id);
CREATE INDEX idx_devolucion_venta ON devolucion(venta_id);

ALTER TABLE nota_credito
    ADD CONSTRAINT fk_nota_credito_devolucion
    FOREIGN KEY (devolucion_id) REFERENCES devolucion(id);

-- ---------------------------------------------------------------------------
-- devolucion_item
-- ---------------------------------------------------------------------------
CREATE TABLE devolucion_item (
    id                  SERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    devolucion_id       INT NOT NULL REFERENCES devolucion(id) ON DELETE CASCADE,
    pedido_item_id      INT NOT NULL REFERENCES pedido_item(id),
    cantidad            DECIMAL(12,3) NOT NULL
);

CREATE INDEX idx_devolucion_item_tenant ON devolucion_item(tenant_id);
CREATE INDEX idx_devolucion_item_devolucion ON devolucion_item(devolucion_id);

-- ============================================================================
-- ============================================================================
-- CAPA 3: AUDITORÍA Y SEGURIDAD (NUEVO v4)
-- ============================================================================
-- ============================================================================

-- ---------------------------------------------------------------------------
-- evento_auditoria (negocio)
-- Append-only garantizado a nivel de motor — ver sección de ROLES más abajo
-- (REVOKE UPDATE, DELETE del rol app_tenant). Snapshot completo antes/después.
-- usuario_id = quien ejecuta la acción. usuario_autoriza_id = quien autorizó
-- con PIN (puede ser distinto de usuario_id — ej. Cajero ejecuta, Admin autoriza).
-- Alcance: acciones con PIN (tenant_permiso_config) + lista ampliada de
-- acciones sensibles sin PIN (cambios de roles/permisos, eliminaciones).
-- ---------------------------------------------------------------------------
CREATE TABLE evento_auditoria (
    id                    BIGSERIAL PRIMARY KEY,
    tenant_id             INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    usuario_id            INT REFERENCES usuario(id),
    usuario_autoriza_id   INT REFERENCES usuario(id),
    permiso_id            INT REFERENCES permiso(id),
    entidad_tipo          VARCHAR(50) NOT NULL,
    entidad_id            INT,
    accion                VARCHAR(50) NOT NULL,
    datos_antes           JSONB,
    datos_despues         JSONB,
    ip_origen             VARCHAR(45),
    user_agent            VARCHAR(255),
    fecha_hora            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_evento_auditoria_tenant ON evento_auditoria(tenant_id);
CREATE INDEX idx_evento_auditoria_fecha ON evento_auditoria(fecha_hora);
CREATE INDEX idx_evento_auditoria_entidad ON evento_auditoria(entidad_tipo, entidad_id);

-- ---------------------------------------------------------------------------
-- evento_seguridad
-- Separada de evento_auditoria a propósito: naturaleza y ritmo de crecimiento
-- distintos (ej. un intento de fuerza bruta no debe contaminar el historial
-- de negocio limpio).
-- ---------------------------------------------------------------------------
CREATE TABLE evento_seguridad (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           INT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    usuario_id          INT REFERENCES usuario(id),
    correo_intento      VARCHAR(150),
    tipo_evento         VARCHAR(30) NOT NULL
                        CHECK (tipo_evento IN ('login_fallido','pin_fallido','refresh_token_reuso','cuenta_bloqueada')),
    ip_origen           VARCHAR(45),
    user_agent          VARCHAR(255),
    detalle             VARCHAR(255),
    fecha_hora          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_evento_seguridad_tenant ON evento_seguridad(tenant_id);
CREATE INDEX idx_evento_seguridad_fecha ON evento_seguridad(fecha_hora);
CREATE INDEX idx_evento_seguridad_tipo ON evento_seguridad(tenant_id, tipo_evento);

-- ============================================================================
-- TRIGGERS updated_at (aplicados automáticamente a toda tabla con esa columna)
-- ============================================================================
DO $$
DECLARE
    t TEXT;
BEGIN
    FOR t IN
        SELECT table_name FROM information_schema.columns
        WHERE column_name = 'updated_at' AND table_schema = 'public'
    LOOP
        EXECUTE format(
            'CREATE TRIGGER trg_set_updated_at BEFORE UPDATE ON %I
             FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();', t
        );
    END LOOP;
END;
$$;

-- ============================================================================
-- ROW LEVEL SECURITY — activado en TODAS las tablas de la capa tenant
-- (incluye automáticamente las tablas nuevas de v4: tenant_permiso_config,
-- usuario_refresh_token, evento_auditoria, evento_seguridad, porque todas
-- tienen tenant_id)
-- ============================================================================
DO $$
DECLARE
    t TEXT;
BEGIN
    FOR t IN
        SELECT table_name FROM information_schema.columns
        WHERE column_name = 'tenant_id' AND table_schema = 'public'
    LOOP
        EXECUTE format('ALTER TABLE %I ENABLE ROW LEVEL SECURITY;', t);
        EXECUTE format(
            'CREATE POLICY tenant_isolation_%1$s ON %1$I
             USING (tenant_id = current_setting(''app.current_tenant_id'')::INT);', t
        );
    END LOOP;
END;
$$;

ALTER TABLE tenants ADD COLUMN slug VARCHAR(50) UNIQUE NOT NULL;
-- El backend (middleware TenantFilter de core-api) debe ejecutar en cada
-- request autenticado: SET LOCAL app.current_tenant_id = '<id-del-tenant>';
-- (SET LOCAL, no SET SESSION, porque el pool de conexiones reutiliza
-- conexiones entre requests de distintos tenants)

-- ============================================================================
-- ============================================================================
-- ROLES DE BASE DE DATOS Y PERMISOS (NUEVO v4)
--
-- Dos roles, uno por servicio, según la arquitectura aprobada:
--   - app_tenant:   usado por core-api. Sin BYPASSRLS. Sujeto siempre a las
--                   políticas de tenant_isolation_* creadas arriba.
--   - app_platform: usado por admin-api. Con BYPASSRLS explícito, para
--                   operar entre tenants (listado de negocios, suscripciones).
--
-- Las contraseñas reales NUNCA deben quedar en este script — se gestionan
-- vía secretos de infraestructura (variables de entorno / secret manager del
-- proveedor de hosting), no hardcodeadas aquí. Los placeholders de abajo son
-- solo para dejar explícita la intención; reemplazar antes de ejecutar.
-- ============================================================================
-- ============================================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_roles WHERE rolname = 'app_tenant'
    ) THEN
        CREATE ROLE app_tenant LOGIN PASSWORD '__REEMPLAZAR_VIA_SECRET_MANAGER__';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_roles WHERE rolname = 'app_platform'
    ) THEN
        CREATE ROLE app_platform LOGIN PASSWORD '__REEMPLAZAR_VIA_SECRET_MANAGER__' BYPASSRLS;
    END IF;
END
$$;

-- app_tenant: acceso estándar a toda tabla de capa tenant, EXCEPTO
-- evento_auditoria, que solo permite INSERT y SELECT (append-only real).
DO $$
DECLARE
    t TEXT;
BEGIN
    FOR t IN
        SELECT table_name FROM information_schema.columns
        WHERE column_name = 'tenant_id' AND table_schema = 'public'
          AND table_name <> 'evento_auditoria'
    LOOP
        EXECUTE format('GRANT SELECT, INSERT, UPDATE, DELETE ON %I TO app_tenant;', t);
    END LOOP;
END;
$$;

GRANT SELECT, INSERT ON evento_auditoria TO app_tenant;
REVOKE UPDATE, DELETE ON evento_auditoria FROM app_tenant;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_tenant;

-- app_platform: acceso total a la capa de plataforma, y bypass de RLS sobre
-- la capa tenant (para el panel Super Admin — Módulo 14).
GRANT ALL ON planes, superadmin, superadmin_refresh_token, tenants,
    suscripciones_historial TO app_platform;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_platform;

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================
