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

## Tests: sufijo `Test` vs `IT` (convención obligatoria)

- **Todo test que necesite Postgres real (u otra infraestructura externa)
  DEBE terminar en `IT`** (ej. `PermisoEvaluatorIT`,
  `FacturacionDianResolucionEncriptacionIT`) — nunca `Test` ni
  `IntegrationTest`. Un test unitario normal (sin infraestructura externa,
  incluido `ModularityTests` — verifica límites entre módulos con
  análisis estático de Spring Modulith, no toca la base) sigue terminando
  en `Test` como siempre.
- El motivo es el patrón de include/exclude por defecto de cada plugin:
  `maven-surefire-plugin` (`mvn test`/`mvn package`) matchea
  `**/*Test.java` (entre otros) pero NUNCA `**/*IT.java`;
  `maven-failsafe-plugin` (`mvn verify`, configurado en el `pom.xml` de
  este módulo) matchea `**/*IT.java` y corre en las fases
  `integration-test`/`verify`. Respetar el sufijo alcanza — no hace falta
  ninguna exclusión manual (`-Dtest='!Xyz'`) en ningún lado, ni en el
  `Dockerfile` ni en CI.
- El `Dockerfile` corre `./mvnw package` sin ninguna exclusión — como
  `package` nunca ejecuta `*IT`, la imagen se construye sin acceso a
  Postgres real automáticamente, por convención de nombre, no por lista
  mantenida a mano.
- Para correr las `*IT` hace falta Postgres dev levantado
  (`docker compose up -d`) y perfil `dev`: `mvn verify` (o
  `-Dtest=NombreIT mvn verify` para una sola).

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
- **Después de cualquier `git merge`/`pull` que toque archivos de
  `db/migration/`, correr SIEMPRE `mvn clean compile` antes de arrancar la
  app — nunca `spring-boot:run` directo.** Maven no limpia
  `target/classes/db/migration/` entre corridas, así que un archivo
  renombrado o eliminado en el merge puede seguir existiendo ahí como
  copia vieja. Flyway escanea el classpath (`target/classes`), no
  `src/main/resources` directamente, así que ve esa copia vieja como una
  migración "resuelta" sin fila en `flyway_schema_history` y falla la
  validación con `Detected resolved migration not applied to database` —
  un falso positivo confuso (parece un problema real de sincronización de
  versiones cuando es solo un artefacto de build viejo). Ya confirmado
  real dos veces en este proyecto: una vez con un rename propio de
  migraciones, otra con un merge real de dos personas. `mvn clean
  compile` (o `clean` antes de cualquier goal) elimina la copia vieja y
  el problema desaparece.
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
- **Proyección nativa (`@Query(nativeQuery = true)`) con columna
  `TIMESTAMPTZ`: el getter debe declararse `Instant`, nunca
  `OffsetDateTime`.** Una entidad `@Entity` completa sí convierte
  automáticamente `TIMESTAMPTZ` → `OffsetDateTime` (pasa por los
  conversores `java.time` de Hibernate), pero una interfaz de proyección
  respaldada por una query nativa no pasa por esa maquinaria — Spring Data
  la entrega tal cual la trae el driver JDBC (`Instant`) y no sabe
  convertirla sola a `OffsetDateTime`. El error (`UnsupportedOperationException:
  Cannot project java.time.Instant to java.time.OffsetDateTime`) **solo
  aparece en runtime, nunca en compilación**, y encima puede aparecer
  disfrazado de otra cosa en el cliente si la excepción sin manejar termina
  cayendo en el `/error` interno de Spring Boot antes de tener un handler
  (ver `GlobalExceptionHandler`, que ahora sí devuelve 500 real para
  cualquier excepción no relacionada a autorización). Si la respuesta
  necesita `OffsetDateTime`, convertir en el adapter con
  `row.getFechaX().atOffset(ZoneOffset.UTC)` (coincide con
  `hibernate.jdbc.time_zone: UTC`, ya usado en todo el proyecto). Un getter
  `LocalDate` para una columna `DATE` no tiene este problema, solo aplica a
  columnas con zona horaria.
- **Filtro opcional (fecha, id, etc.) en JPQL con patrón `:param IS NULL OR
  columna >= :param`: falla en runtime con 500, nunca en compilación.**
  JPQL genera un `?` de JDBC por cada *ocurrencia* del parámetro con
  nombre (no uno solo reusado), así que `:param` usado primero en
  `? IS NULL` y de nuevo en `columna >= ?` son dos bind slots
  *distintos* para Postgres. Si algún bind slot solo aparece en un
  `IS NULL` desnudo (sin ningún otro contexto que dé tipo), Postgres no
  puede inferir su tipo vía el protocolo extendido de JDBC y tira
  `ERROR: could not determine data type of parameter $1` — se ve como
  `InvalidDataAccessResourceUsageException` envuelta, cae en el
  catch-all de `GlobalExceptionHandler` como 500 genérico. Solución:
  pasar esa query a SQL nativo (`nativeQuery = true`) con
  `CAST(:param AS tipo)` explícito en **cada** ocurrencia del parámetro,
  no solo la primera. Ejemplo real ya resuelto: `GET /caja/jornadas` y
  `GET /ventas`, ambos con `fecha_inicio`/`fecha_fin` opcionales (ver
  `CajaJornadaJpaRepository.listarEnRango` y `VentaJpaRepository.listar`).
- **Columna JSON/JSONB mapeada como `JsonNode` vía `@JdbcTypeCode(SqlTypes.JSON)`
  que nunca se actualiza después de creada: DEBE llevar `@Immutable`
  (`org.hibernate.annotations`) en el campo.** Sin esto, el
  dirty-checking de Hibernate 6 no logra un snapshot estable de
  `JsonNode` y genera un `UPDATE` redundante en cada `INSERT`, aunque el
  valor nunca haya cambiado. Si la tabla además tiene `REVOKE UPDATE`
  para el rol de runtime (como `evento_auditoria`, append-only a
  propósito), ese `UPDATE` fantasma falla con `permission denied` y
  tumba la transacción completa — un bug que se ve como si la protección
  append-only estuviera "funcionando", pero por la razón equivocada (el
  `INSERT` real ya se había ejecutado bien; lo único que fallaba era el
  `UPDATE` que nunca debió emitirse). Caso real ya resuelto:
  `EventoAuditoria.datosAntes`/`datosDespues` (`shared.auditoria`).

## Cifrado de credenciales Factus (riesgo conocido, no resuelto)

- **`FacturacionDianResolucion` descifra `client_id_factus`/`client_secret_factus`/
  `username_factus`/`password_factus` de forma EAGER en cualquier carga de la
  entidad vía JPA** (`@Convert` con `FactusCredencialAttributeConverter`) —
  esto incluye lecturas que solo necesitan campos NO sensibles, como
  `prefijo`/`numeracion_actual` durante `POST /ventas` normal
  (`FacturacionDianService.reservarSiguienteNumeroFactura`). Hibernate
  descifra los 4 campos convertidos al hidratar la entidad desde el
  `ResultSet`, sin importar cuáles de sus getters vaya a usar el código
  que la llamó.
- **Consecuencia real**: si el texto cifrado se corrompe (ej. una rotación
  de llave mal ejecutada, o un dato manipulado directamente en la base),
  `decrypt()` tira `CifradoException` al cargar la fila — y como
  `reservarSiguienteNumeroFactura()` corre DENTRO de la transacción de
  `POST /ventas`, esto rompe el cobro COMPLETO (500), no solo el paso que
  llama a Factus. Confirmado real durante las pruebas de
  `reintentar-envio` (corromper el ciphertext de `password_factus` hizo
  fallar `POST /ventas` entero, no solo la transmisión asíncrona a
  Factus).
- **Solución correcta, todavía no implementada**: separar el acceso a los
  campos sensibles de los no sensibles — por ejemplo, `@Basic(fetch =
  LAZY)` en los 4 campos convertidos (requiere bytecode instrumentation
  para que Hibernate respete el lazy loading en campos básicos), o una
  proyección/entidad separada que solo mapee `prefijo`/`numeracion_actual`
  para el camino de numeración, dejando la lectura de credenciales
  aislada al único caller real (`credencialesFactus()`).
- **Cómo reproducir esto a propósito para probar** (sin tocar datos
  reales): nunca corromper el ciphertext directamente — eso rompe
  decrypt() por completo y no representa un escenario realista de
  "credenciales incorrectas". Para simular credenciales Factus inválidas
  de forma realista, cifrar un valor incorrecto CON LA LLAVE REAL (usando
  `FactusCredencialesCryptoService.encrypt(...)` desde un test) y guardar
  ese ciphertext — así decrypt() funciona bien localmente y solo falla la
  autenticación contra Factus (el escenario async, no el síncrono).

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
- Mecanismo técnico para la llamada directa síncrona: Spring Modulith
  rechaza por defecto cualquier llamada a un paquete no expuesto, incluso
  entre módulos ya autorizados a comunicarse sincrónicamente (`verify()`
  trata `domain`/`application`/`infrastructure` como internos salvo que se
  exponga algo explícitamente). El módulo LLAMADO debe exponer SOLO lo que
  el otro necesita, con `@org.springframework.modulith.NamedInterface`
  puesto sobre las clases/records puntuales (ej. el service y el DTO de
  salida) — NUNCA sobre el paquete completo vía `package-info.java`.
  Exponer el paquete entero rompe el propósito de Modulith: cualquier otro
  módulo futuro podría empezar a usar cualquier otra cosa de ese paquete
  sin autorización explícita. Ejemplo real ya resuelto: `restaurante.MenuPublicoService`
  llama a `productosmenu.ProductoService` para armar el menú digital
  público — `ProductoService` y el record `ProductoPublico` llevan
  `@NamedInterface`, nada más del paquete `productosmenu.application` está
  expuesto.
- Nunca acceder a `domain`/`application`/`infrastructure` de otro módulo
  directamente — correr `mvn test -Dtest=ModularityTests` antes de cualquier
  commit importante para confirmar que los límites se respetan.
- **Dos módulos distintos mapeando la MISMA tabla física con propósito
  distinto** (ej. `operacion.Turno` para autoregistro del empleado vs
  `personal.Turno` para gestión Admin/Jefe, ambas mapean `turno`): hay que
  nombrar EXPLÍCITAMENTE desde el principio tanto el bean de Spring
  (`@Service("nombreX")` en la clase de servicio, y cualquier otro
  componente con nombre de clase repetido — controller, repositorio JPA,
  adapter) como el nombre lógico de la entidad JPA (`@Entity(name =
  "NombreX")`). El nombre de clase por defecto choca en silencio en
  ambos casos, pero por mecanismos distintos aunque el síntoma se vea
  parecido: Spring tira `ConflictingBeanDefinitionException` (bean name
  duplicado, resuelto por nombre de clase en minúscula) y Hibernate tira
  `DuplicateMappingException` (registro de nombres de entidad, mecanismo
  separado del bean name de Spring). Caso real ya resuelto:
  `operacion.Turno` vs `personal.Turno` (`PersonalTurno` como nombre de
  entidad JPA y bean de servicio).
- `shared/` es el único paquete `OPEN` de Modulith (tenant, seguridad,
  auditoría, excepciones) — accesible libremente desde cualquier módulo.
- Multi-tenancy vía Row Level Security de Postgres, `SET LOCAL
  app.current_tenant_id` por transacción. Dos roles de base de datos:
  `app_tenant` (runtime de esta app, sin `BYPASSRLS`) y `app_platform`
  (exclusivo de `admin-api`, con `BYPASSRLS`).
- RBAC dinámico vía `PermissionEvaluator` nativo de Spring Security
  (`@PreAuthorize("hasPermission('modulo.subvista', 'accion')")`), con caché
  Caffeine por `(tenant_id, rol_id)`.

## Manejo de `AccessDeniedException` (regla crítica, no negociable)

- **`GlobalExceptionHandler.handleAccessDenied` DEBE resolver el 403
  directo (devolver un `ResponseEntity`), NUNCA relanzar la excepción.**
  Bug real ya confirmado y arreglado: relanzarla asumiendo que
  `ExceptionTranslationFilter` la traduce a 403 dentro del mismo
  dispatch es una suposición falsa en este proyecto — cuando la
  excepción se origina en un `@PreAuthorize` de un método de
  controller, escapa sin resolver, Spring Boot la reenvía a `/error`
  (dispatch tipo ERROR), y en ese dispatch los filtros de seguridad NO
  vuelven a correr → `SecurityContext` vacío → `ExceptionTranslationFilter`
  lo clasifica como "necesita autenticación" y devuelve 401 "Sesión
  inválida o expirada" en vez de un 403 real. Afecta a CUALQUIER
  `@PreAuthorize` denegado de cualquier módulo, no depende del catálogo
  de permisos de ese módulo puntual — descubierto probando RBAC real
  del módulo Gastos, pero preexistía en todo el proyecto.
- **Relación con el catch-all `Exception.class` de la misma clase
  (fix anterior, ver su Javadoc): son el MISMO mecanismo, síntomas
  inversos.** El catch-all ya resuelve cualquier excepción no manejada
  ADENTRO del ciclo normal de `DispatcherServlet` para evitar el reenvío
  a `/error` (que ahí convertía un 500 real en un 403 vacío sin
  relación con permisos). Este fix aplica el mismo principio a
  `AccessDeniedException` específicamente. **Si se toca cualquiera de
  los dos handlers en el futuro, verificar AMBOS síntomas con una
  prueba real**: una excepción de negación de permiso real da 403 (no
  401), Y una excepción genérica no relacionada con seguridad sigue
  dando 500 real (no 403 ni 401) — confirmado con un
  `RuntimeException` de prueba forzado a propósito, no asumido.

## PIN de step-up (regla obligatoria para acciones con `requiere_pin=true`)

- Cualquier endpoint que mute datos marcados con `requiere_pin=true` en
  `tenant_permiso_config` debe exigir el header `X-Pin-Token` y llamar a
  `PinStepUpService.validar(pinTokenHeaderValue, modulo, accion,
  recursoTipo, recursoId)` (`shared.seguridad`) **al inicio del método**,
  antes de ejecutar la mutación — si la validación falla, la mutación no
  debe correr. `modulo`/`accion` son los del catálogo `permiso` de ese
  endpoint; `recursoTipo`/`recursoId` identifican el recurso puntual de
  esa petición (ej. `"insumo"` + el `insumo_id` del request). Ver
  `AjusteController` (`inventario`) como ejemplo ya conectado.
- El `pin_token` se obtiene de `POST /auth/pin/verificar` (`modulo` +
  `accion` en vez de un nombre de acción de negocio plano — mismo formato
  del catálogo `permiso` ya existente). Es un JWT firmado con la MISMA
  llave de core-api que un access token normal, diferenciado SOLO por el
  claim `"typ": "pin_stepup"` — `PinStepUpService` rechaza cualquier token
  sin ese claim exacto, incluyendo un access token normal presentado por
  error. TTL corto (`cafepos.pin-authorization.token-ttl-minutes`, 2
  minutos por defecto) + atado a `permiso_id` + `recurso_tipo` +
  `recurso_id` específicos (comparados EXACTOS, ver `JwtService`).
- NOTA DE DISEÑO: no hay enforcement de un solo uso real (no existe tabla
  de tokens consumidos) — la protección es el TTL corto más estar atado al
  recurso específico. Si se necesita single-use estricto en el futuro,
  hace falta una tabla de tokens ya usados — fuera de alcance por ahora.
- El contador de bloqueo de PIN (`usuario.pin_intentos_fallidos` /
  `pin_bloqueado_hasta`, ver V17) es independiente del contador de login
  (RN-008) — 5 PIN fallidos consecutivos bloquean el PIN por 30 minutos,
  sin afectar el login.

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
