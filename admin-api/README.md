# CaféPOS — admin-api

Backend del Panel Super Admin de CaféPOS. Proyecto Maven separado de
`core-api` (mismo repositorio padre, propio ciclo de vida) — ver
`CLAUDE.md` para las decisiones de arquitectura.

## Requisitos

- Java 21, Maven 3.9+.
- `cafepos-core-api` con su Postgres levantado (`docker compose up -d` en
  ese repositorio, puerto 5434) y sus migraciones Flyway ya aplicadas —
  este proyecto no tiene Flyway propio, depende del schema que crea
  core-api.

## Arrancar en local

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Puerto **8081** (core-api usa 8080, pueden correr los dos a la vez).

## Probar desde cero

**1. Bootstrap (una sola vez, para siempre):**
```bash
curl -X POST http://localhost:8081/admin/auth/bootstrap \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana Superadmin","correo":"ana@cafepos.com","password":"ClaveSegura2026"}'
```
Responde `201` con `{id, nombre, correo}` (nunca el password ni el hash).

**2. Confirmar que un segundo bootstrap falla siempre:**
```bash
curl -i -X POST http://localhost:8081/admin/auth/bootstrap \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Otro","correo":"otro@cafepos.com","password":"OtraClaveSegura2026"}'
```
Responde `403`, sin importar los datos que se manden.

**3. Login:**
```bash
curl -X POST http://localhost:8081/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"ana@cafepos.com","password":"ClaveSegura2026"}'
```
Responde `200` con `{accessToken, refreshToken, expiresIn}` (`expiresIn` en
segundos, 600 = 10 minutos).

**4. Refresh (rota el par, el refresh token usado queda revocado):**
```bash
curl -X POST http://localhost:8081/admin/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refreshToken del paso 3>"}'
```
Responde `200` con un par `accessToken`/`refreshToken` nuevos. Repetir la
misma llamada con el `refreshToken` viejo debe dar `401` — quedó revocado
al usarse.

## Qué falta después de esto

`POST /admin/negocios` (creación de cafeterías) — próximo paso, no
implementado todavía.
