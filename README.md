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

**2. Ejecutar las migraciones Flyway (crea el schema completo + roles de BD):**
```bash
mvn flyway:migrate -Dspring.profiles.active=dev
```

Este paso ejecuta `V1__schema_v4.sql` (todo el modelo de datos + los roles
`app_tenant`/`app_platform`) y `V2__catalogo_permisos.sql` (catálogo de
permisos poblado). Se conecta como superusuario `postgres` (ver
`application-dev.yml` → `spring.flyway.*`), porque crear roles y activar
Row Level Security requiere privilegios que la aplicación en sí **nunca**
debe tener.

**3. Fijar la contraseña real del rol `app_tenant` para desarrollo local**
(en `V1__schema_v4.sql` queda un placeholder a propósito — nunca debe haber
una contraseña real en un script versionado en Git):
```bash
docker exec -it cafepos-postgres-dev psql -U postgres -d cafepos \
  -c "ALTER ROLE app_tenant PASSWORD 'dev_only_password';"
```
(La contraseña `dev_only_password` ya coincide con `application-dev.yml`. En
cualquier ambiente real, esto se gestiona por variable de entorno / secret
manager del proveedor de hosting, nunca a mano así.)

**4. Levantar la aplicación:**
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
