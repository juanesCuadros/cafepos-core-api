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

## Repositorios JPA (regla crítica, no negociable)

- **Todo repositorio de Spring Data JPA debe extender `TenantAwareRepository<T, ID>`
  (`shared.tenant`), nunca `JpaRepository<T, ID>` directamente.**
- Motivo: un método de repositorio que vos declarás (derivado por nombre o
  `@Query`) **no** hereda transacción automáticamente como sí lo hacen los
  métodos heredados de `SimpleJpaRepository` (`findById`, `save`, ...). Sin
  una transacción propia, `TenantAwareJpaTransactionManager` nunca ejecuta
  el `SET LOCAL app.current_tenant_id` que activa Row-Level Security —
  cualquier query contra una tabla con columna `tenant_id` puede fallar con
  un error crudo de Postgres (`invalid input syntax for type integer`), o en
  el peor caso devolver datos de otro tenant si la conexión pooled trae un
  valor viejo de una transacción anterior.
- No es un problema de configuración corregible con una property (no hay
  dos `PlatformTransactionManager` compitiendo — solo existe uno) ni algo
  que dependa de `enableDefaultTransactions`: es cómo Spring Data resuelve
  la transacción de un método sin una clase de implementación real detrás
  (confirmado leyendo el bytecode de `TransactionalRepositoryProxyPostProcessor`
  en `spring-data-commons`). `TenantAwareRepository` ya lleva
  `@Transactional(readOnly = true)` a nivel de interfaz — alcanza con
  extenderla, no hace falta anotar cada método a mano. Si un método
  específico escribe datos, anotalo igual con `@Transactional` (sin
  `readOnly`) para que quede explícito.

## DTOs de PATCH (regla obligatoria)

- **Todo campo genuinamente nullable de negocio en un DTO de PATCH debe usar
  `JsonNullable<T>` (`org.openapitools.jackson.nullable`), nunca el tipo
  plano.** Java plano no distingue "el campo no vino en el JSON" de "el
  campo vino en el JSON con valor `null`" — con el tipo plano ambos casos
  llegan igual (`null`), asi que no hay forma de "borrar" un campo nullable
  que ya tiene valor via PATCH sin tambien reescribirlo por accidente cuando
  el cliente simplemente no menciona el campo.
- El modulo Jackson (`JsonNullableModule`) que habilita esto ya esta
  registrado globalmente como bean en
  `com.cafepos.core.shared.jackson.JacksonConfig` — no hace falta
  configuracion extra por DTO, alcanza con declarar el campo como
  `JsonNullable<T>` en el record.
- En el metodo que aplica el PATCH (service o entidad de dominio, segun
  donde viva la logica de "actualizar parcial" de ese modulo), el chequeo
  pasa de `if (campo != null) { entity.campo = campo; }` a
  `if (campo.isPresent()) { entity.campo = campo.get(); }` — `get()` puede
  devolver `null`, y eso es correcto: significa que el borrado fue explicito.
- Los campos obligatorios del recurso (los que no aceptan `null` como valor
  valido — ej. `nombre`, `precio_venta`, `estado`, `tipo_descuento`) NO
  necesitan este tratamiento: se quedan con el tipo plano y sin este patron,
  un `null` explicito ahi sigue siendo candidato a error de validacion, no
  una accion de "borrar".
- Ejemplo ya aplicado en produccion en `productosmenu` (Categoria, Producto,
  Promocion): ver `CategoriaActualizarRequest`, `ProductoActualizarRequest`,
  `PromocionActualizarRequest` y sus respectivos `actualizar(...)` en
  `application`/`domain`.

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
