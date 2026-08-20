# CaféPOS — core-api

Servicio principal de CaféPOS: Operación, Caja, Productos y Menú, Inventario,
Compras, Clientes, Personal, Gastos, Contabilidad, Reportes, Configuración,
Restaurante. Monolito modular (Spring Modulith) con arquitectura hexagonal
completa en todos los módulos.

Ver `cafepos_arquitectura_backend_v1.md` para el detalle y la justificación
de cada decisión reflejada en este esqueleto.

Este repositorio es solo `core-api`. `admin-api` (Panel Super Admin) y
`billing-worker` (facturación DIAN) son proyectos Maven separados, aún no
generados.

## Requisitos

- Java 21
- Maven 3.9+
- Docker (para Postgres local)

## Arrancar en local — primera vez

**1. Levantar Postgres:**
```bash
docker compose up -d
```

**2. Arrancar la aplicación por primera vez (corre las migraciones Flyway):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

No hay `flyway-maven-plugin` en el `pom.xml`, ni falta: `spring-boot:run`
ya dispara las migraciones automáticamente al arrancar, vía
`FlywayAutoConfiguration`, conectado como superusuario `postgres` (ver
`application-dev.yml` → `spring.flyway.*`) — porque crear roles y activar
Row Level Security requiere privilegios que la aplicación en sí **nunca**
debe tener. Este paso ejecuta `V1__schema_v4.sql` (todo el modelo de datos
+ los roles `app_tenant`/`app_platform`), `V2__catalogo_permisos.sql`
(catálogo de permisos poblado) y `V3__event_publication.sql`.

**Este primer arranque va a fallar** justo después de que las migraciones
terminen (el mensaje de Flyway en consola dirá éxito): el datasource de la
app (`spring.datasource.*`) se conecta como `app_tenant`, que todavía tiene
el password placeholder de la migración, no el real. Es esperado — seguir
con el paso 3.

**3. Fijar la contraseña real de los roles para desarrollo local**
(en `V1__schema_v4.sql` quedan con un placeholder a propósito — nunca debe
haber una contraseña real en un script versionado en Git):
```bash
docker exec -it cafepos-postgres-dev psql -U postgres -d cafepos \
  -c "ALTER ROLE app_tenant PASSWORD 'dev_only_password';" \
  -c "ALTER ROLE app_platform PASSWORD 'dev_only_password_platform';"
```
(`dev_only_password` ya coincide con `application-dev.yml`. La de
`app_platform` no se usa todavía — `admin-api` aún no existe — pero queda
lista para cuando se construya. En cualquier ambiente real, esto se
gestiona por variable de entorno / secret manager del proveedor de
hosting, nunca a mano así.)

**4. Levantar la aplicación de nuevo:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Verificar los límites entre módulos (Spring Modulith)

```bash
mvn test -Dtest=ModularityTests
```

Este test falla el build si algún módulo accede directamente a las clases
internas de otro módulo, sin pasar por su API pública o por eventos de
aplicación. Es la garantía automática de que el monolito modular no
degenera en un monolito de espagueti con el tiempo — correrlo antes de
cada commit importante.

## Qué falta después de este esqueleto

Este commit inicial **no incluye lógica de negocio todavía** — es la base
sobre la que se construye. Lo siguiente, en orden, según lo acordado:

1. Implementar el flujo de login como primera "rebanada vertical" completa:
   resolución de tenant por subdominio → validación de credenciales →
   emisión de tokens (`shared.seguridad`) → primer endpoint protegido de
   prueba.
2. Diseñar y construir `admin-api` (Panel Super Admin).
3. Diseñar y construir `billing-worker` (facturación DIAN).

## Estructura de un módulo de negocio

```
<modulo>/
├── domain/           entidades, agregados, puertos (interfaces)
├── application/      casos de uso, orquesta la transacción, publica eventos
└── infrastructure/
    ├── web/          controllers (adaptador de entrada HTTP)
    └── persistence/   implementación JPA de los puertos del dominio
```

Los 12 módulos de negocio (`operacion`, `caja`, `productosmenu`,
`inventario`, `compras`, `clientes`, `personal`, `gastos`, `contabilidad`,
`reportes`, `configuracion`, `restaurante`) siguen exactamente esta misma
plantilla, sin excepción — decisión explícita para priorizar escalabilidad
sobre velocidad inicial.

`shared/` es el kernel compartido (tenant, seguridad, auditoría,
excepciones), declarado como módulo `OPEN` de Spring Modulith: cualquier
módulo de negocio puede depender de él libremente.
