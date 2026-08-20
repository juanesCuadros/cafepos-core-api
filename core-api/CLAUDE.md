# CaféPOS — core-api — Instrucciones para Claude Code

Este archivo se lee automáticamente al inicio de cada sesión. Contiene las
reglas del proyecto que no deben repetirse en cada prompt.

## Estilo de código (regla estricta)

- **Comentarios mínimos**: solo cuando explican un "por qué" que no es obvio
  leyendo el código (una decisión de diseño, una restricción externa). Nunca
  comentarios que solo repiten lo que la línea ya dice.
- **Sin tildes en código, ni en comentarios de código** (Java, SQL, YAML,
  nombres de variables/métodos/tablas/columnas). Esto incluye comentarios en
  archivos `.java` y `.sql`.
- **Excepción única**: sí van tildes en texto que ve el usuario final —
  mensajes de error, respuestas de la API, textos de validación, logs
  pensados para lectura humana en soporte. Español correcto ahí, sin
  restricción.
- Ejemplo:
  ```java
  // mal: mapa de permisos por rol, se actualiza cuando el Jefe edita la matriz
  // bien: mapa de permisos por rol, actualiza cuando Jefe edita matriz
  ```
  ```java
  throw new AccesoDenegadoException("No tienes permiso para realizar esta acción");
  // el mensaje de excepcion SI lleva tildes, es texto para el usuario
  ```

## Migraciones Flyway (regla crítica, no negociable)

- **Nunca editar un archivo de migración ya aplicado.** Antes de tocar
  cualquier `V*.sql` existente, verificar en `flyway_schema_history`
  (`docker exec -it cafepos-postgres-dev psql -U postgres -d cafepos -c
  "SELECT version, description, success FROM flyway_schema_history ORDER BY
  installed_rank;"`) si ya fue aplicado.
- Todo cambio de schema (columna nueva, tabla nueva, constraint nuevo) es
  **siempre** un archivo nuevo `V{n+1}__descripcion.sql`, nunca una edición
  retroactiva.
- Migraciones aplicadas hasta ahora: V1 (schema completo), V2 (catálogo de
  permisos), V3 (tabla `event_publication` de Spring Modulith), V4
  (`usuario.debe_cambiar_password`).
- `spring.jpa.hibernate.ddl-auto` es **siempre** `validate`. Nunca cambiar a
  `update` o `create` — el schema real vive únicamente en las migraciones
  Flyway, Hibernate solo confirma que las entidades coinciden con él.

## Arquitectura del proyecto

- Monolito modular con **Spring Modulith** — 12 módulos de negocio, cada uno
  con arquitectura hexagonal completa: `domain/`, `application/`,
  `infrastructure/web/`, `infrastructure/persistence/`.
- Comunicación entre módulos: llamada directa síncrona solo cuando se
  necesita consistencia transaccional atómica (ej. venta descuenta
  inventario en la misma transacción). Eventos de aplicación de Modulith
  para todo lo que tolera consistencia eventual.
- Nunca acceder a `domain`/`application`/`infrastructure` de otro módulo
  directamente — correr `mvn test -Dtest=ModularityTests` antes de cualquier
  commit importante para confirmar que los límites se respetan.
- `shared/` es el único paquete `OPEN` de Modulith (tenant, seguridad,
  auditoría, excepciones) — accesible libremente desde cualquier módulo.
- Multi-tenancy vía Row Level Security de Postgres, `SET LOCAL
  app.current_tenant_id` por transacción. Dos roles de base de datos:
  `app_tenant` (runtime de esta app, sin `BYPASSRLS`) y `app_platform`
  (exclusivo de `admin-api`, con `BYPASSRLS`).
- RBAC dinámico vía `PermissionEvaluator` nativo de Spring Security
  (`@PreAuthorize("hasPermission('modulo.subvista', 'accion')")`), con caché
  Caffeine por `(tenant_id, rol_id)`.

## Stack técnico

- Spring Boot 3.5.x (no 4.0 — ecosistema todavía inmaduro sobre Boot 4 para
  este proyecto), Java 21, Maven.
- Spring Modulith 1.4.x.
- PostgreSQL 16, Flyway para migraciones.
- JWT (jjwt) para autenticación, Caffeine para caché de permisos.

## Entorno de desarrollo local

- Postgres en `docker-compose.yml`, mapeado a **puerto 5434** (5432 y 5433
  ocupados por otros servicios en esta máquina).
- `JAVA_HOME` debe apuntar a JDK 21. No queda persistido entre sesiones de
  terminal salvo que se configure con `setx` — si un build falla con
  `UnsupportedClassVersionError`, es casi siempre esto.
- Las migraciones corren vía `spring-boot:run` (`FlywayAutoConfiguration`),
  **no** vía `mvn flyway:migrate` standalone (ese plugin no lee
  `spring.flyway.*` del `application-dev.yml`).
- Conexión de runtime de la app: usuario `app_tenant`. Conexión de
  migraciones: usuario `postgres` (superusuario, configurado por separado en
  `spring.flyway.*`).

## Documentos de referencia en el repositorio

- `cafepos_arquitectura_backend_v1.md` — decisiones de arquitectura backend
  completas, con su justificación.
- `src/main/resources/db/migration/` — fuente de verdad del schema real.
