# CaféPOS — Contrato de API
## Módulo — Clientes
_Versión 1.0 · Agosto 2026_

> Convenciones generales, versionado y manejo de errores en `api_00_autenticacion.md`

Vista única (sin sub-vistas). El mismo formulario se reutiliza como modal en el POS al momento de cobrar.

> **Corrección de consistencia:** en `api_03_caja.md` se había definido `POST /api/v1/clientes/rapido` como si fuera un endpoint aparte para el registro rápido del POS. Es innecesario — es el **mismo formulario**, solo que el modal del POS muestra menos campos visibles. Se elimina esa ruta duplicada, todo pasa por `POST /api/v1/clientes` definido aquí.

---

### `GET /api/v1/clientes`

**Query params:** `?q=` (busca por nombre o documento)

**Response 200:**
```json
{
  "clientes": [
    {
      "id": 40,
      "codigo": "CLI-0040",
      "nombre": "María Gómez",
      "tipo_documento": "CC",
      "numero_documento_enmascarado": "••••4521",
      "telefono": "3001234567",
      "correo": "maria@correo.com",
      "saldo_favor": 5000
    }
  ]
}
```

> El número de documento **nunca** se devuelve completo en el listado, solo enmascarado. El completo solo aparece en `GET /clientes/{id}` (detalle explícito).

---

### `GET /api/v1/clientes/buscar?q={texto}`

Versión liviana usada en el modal del POS (ya documentada en `api_03_caja.md`, referenciada aquí como la misma). Devuelve menos campos, pensada para una lista rápida de selección.

---

### `POST /api/v1/clientes`

Mismo endpoint tanto para el formulario completo del módulo Clientes como para el registro rápido dentro del POS — el frontend simplemente muestra menos campos en el modal del POS, pero todos son válidos aquí.

**Request:**
```json
{
  "tipo_documento": "CC",
  "numero_documento": "1094567890",
  "nombre": "María Gómez",
  "telefono": "3001234567",
  "correo": "maria@correo.com",
  "direccion": "Calle 5 #10-20, Popayán"
}
```

> `telefono`, `correo` y `direccion` son opcionales — el registro rápido del POS normalmente solo envía `tipo_documento`, `numero_documento` y `nombre`.

**Response 201:**
```json
{
  "id": 41,
  "codigo": "CLI-0041",
  "nombre": "María Gómez"
}
```

**Response 409 (documento ya existe en este tenant):**
```json
{ "error": "Ya existe un cliente con este documento" }
```

---

### `GET /api/v1/clientes/{id}`

Detalle completo, incluyendo el número de documento sin enmascarar (solo aquí).

**Response 200:**
```json
{
  "id": 40,
  "codigo": "CLI-0040",
  "tipo_documento": "CC",
  "numero_documento": "1004567890",
  "nombre": "María Gómez",
  "telefono": "3001234567",
  "correo": "maria@correo.com",
  "direccion": "Calle 5 #10-20, Popayán",
  "saldo_favor": 5000
}
```

---

### `PATCH /api/v1/clientes/{id}`

Mismos campos del `POST`, opcionales. No permite editar `tipo_documento`/`numero_documento` si el cliente ya tiene ventas registradas (evita romper la trazabilidad de facturas ya emitidas a ese documento).

**Response 403 (intento de cambiar documento con historial):**
```json
{ "error": "No se puede cambiar el documento de un cliente con ventas registradas" }
```

---

### `DELETE /api/v1/clientes/{id}`

**Response 200 (sin ventas asociadas):**
```json
{ "mensaje": "Cliente eliminado" }
```

**Response 409 (con ventas o saldo a favor pendiente):**
```json
{ "error": "No se puede eliminar, este cliente tiene historial de compras o saldo a favor pendiente" }
```

---

### `GET /api/v1/clientes/{id}/historial`

Historial de compras/pedidos del cliente, para el panel de detalle.

**Response 200:**
```json
{
  "compras": [
    {
      "venta_id": 501,
      "fecha_hora": "2026-08-19T15:40:00Z",
      "total": 19360,
      "factura_numero": "FE-000088",
      "estado": "cobrado"
    }
  ]
}
```

---

### `GET /api/v1/clientes/{id}/saldo-movimientos`

Historial de movimientos del saldo a favor (tabla `cliente_saldo_movimiento`), útil para auditar de dónde viene el saldo actual.

**Response 200:**
```json
{
  "saldo_actual": 5000,
  "movimientos": [
    {
      "id": 12,
      "tipo": "credito",
      "monto": 9000,
      "origen_tipo": "devolucion",
      "origen_id": 20,
      "fecha": "2026-08-19T16:00:00Z",
      "descripcion": "Devolución de producto en mal estado"
    },
    {
      "id": 13,
      "tipo": "debito",
      "monto": 4000,
      "origen_tipo": "venta",
      "origen_id": 505,
      "fecha": "2026-08-20T12:00:00Z",
      "descripcion": "Aplicado como pago en venta VTA-0505"
    }
  ]
}
```

> Este endpoint no se llena manualmente — cada devolución con saldo a favor y cada venta que use saldo como método de pago genera automáticamente una fila aquí.

---

### Notas pendientes de este módulo
- **Corrección aplicada:** eliminar `POST /api/v1/clientes/rapido` de `api_03_caja.md`, reemplazar la referencia por `POST /api/v1/clientes` (este archivo)
