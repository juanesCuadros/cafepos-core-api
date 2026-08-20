# CaféPOS — admin-api — Instrucciones para Claude Code

Este archivo se lee automáticamente al inicio de cada sesión. Contiene las
reglas del proyecto que no deben repetirse en cada prompt.

## Estilo de código (regla estricta)

- **Comentarios mínimos**: solo cuando explican un "por qué" que no es obvio
  leyendo el código (una decisión de diseño, una restricción externa). Nunca
  comentarios que solo repiten lo que la línea ya dice.
- **Sin tildes en código, ni en comentarios de código** (Java, YAML, nombres
  de variables/métodos). Esto incluye comentarios en archivos `.java`.
- **Excepción única**: sí van tildes en texto que ve el usuario final —
  mensajes de error, respuestas de la API, textos de validación. Español
  correcto ahí, sin restricción.

## Relación con core-api (regla crítica, no negociable)

- admin-api es un proyecto Maven **separado** de core-api — propio pom.xml,
  propio ciclo de vida, propio JWT (llave de firma, issuer y audience
  distintos, `cafepos-admin-api` vs los de core-api). Sin sesión compartida
  entre los dos servicios.
- **admin-api NO tiene migraciones Flyway propias, ni las va a tener.** El
  schema completo (incluida la tabla `superadmin`) lo crea y versiona
  **core-api** (`cafepos-core-api/src/main/resources/db/migration/`). Este
  proyecto solo se conecta con `spring.jpa.hibernate.ddl-auto=validate` — si
  hace falta una columna o tabla nueva, esa migración se agrega en el
  repositorio de core-api, nunca acá.
- Se conecta a la **misma** base Postgres que core-api (`localhost:5434`,
  db `cafepos`), pero con el rol `app_platform` (`BYPASSRLS`) — nunca
  `app_tenant`. Es justamente la razón de ser de admin-api: operar entre
  tenants (alta de negocios, suscripciones), algo que Row Level Security le
  bloquea a `app_tenant` a propósito.
- La tabla `superadmin` ya trae columnas `totp_secret` / `totp_habilitado`
  (creadas por core-api, pensando en 2FA a futuro). **No mapear esas
  columnas en la entidad JPA todavía** — 2FA queda fuera de alcance hasta
  una fase posterior, no dejar campos a medio usar.

## Arquitectura del proyecto

- Servicio chico, **sin Spring Modulith** — no se justifica esa complejidad
  todavía (a diferencia de core-api). Estructura simple, un único paquete
  de dominio por ahora: `auth` con `domain/ application/
  infrastructure/(web, persistence, security)`.
- Simplificación deliberada: `Superadmin` y `SuperadminRefreshToken` son a
  la vez entidad de dominio y entidad JPA (`@Entity` directo en
  `auth.domain`), sin mapper contra un modelo de persistencia separado — el
  servicio es chico y no hay hoy una razón real para esa capa extra.
- Refresh tokens: se guarda un **hash SHA-256** del token opaco, no bcrypt
  — el token ya nace con entropía alta (32 bytes de `SecureRandom`), así
  que hace falta un `WHERE token_hash = ?` directo, no una verificación
  fila por fila como exige bcrypt.

## Stack técnico

- Spring Boot 3.5.x (mismo que core-api), Java 21, Maven.
- JWT (jjwt 0.12.x, misma librería que core-api). BCrypt vía
  `spring-security-crypto` (transitiva de `spring-boot-starter-security`),
  para `password_hash` de `superadmin` — nunca para refresh tokens.
- PostgreSQL 16 — sin Flyway en este proyecto (ver arriba).

## Entorno de desarrollo local

- Puerto **8081** (core-api ya usa 8080 — pueden correr los dos a la vez).
- Requiere el Postgres de `docker-compose.yml` de **cafepos-core-api**
  arriba (puerto 5434) — este repositorio no tiene su propio
  docker-compose.
- `JAVA_HOME` debe apuntar a JDK 21, igual que core-api.
- Como no hay Flyway, `spring-boot:run` con perfil `dev` no crea nada — si
  la tabla `superadmin` no existe, es porque las migraciones de core-api
  todavía no corrieron ahí.
