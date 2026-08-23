# CaféPOS — Contrato de API
## Módulo — Restaurante
_Versión 1.0 · Agosto 2026_

> Convenciones generales, versionado y manejo de errores en `api_00_autenticacion.md`

Cubre las 5 sub-vistas: Información general, Zonas y Mesas, Métodos de pago, Facturación DIAN, Menú digital (QR).

---

## 10.1 — Información general

Es un registro único por tenant (1:1 con `restaurantes`), no una lista — por eso no lleva `{id}` en la URL.

### `GET /api/v1/restaurante`

**Response 200:**
```json
{
  "nombre_negocio": "Cafetería Demo",
  "nit": "900123456-7",
  "logo_url": "https://cdn.cafepos.co/1/uploads/logo.png",
  "direccion": "Cra 10 #15-20",
  "departamento": "Cauca",
  "ciudad": "Popayán",
  "pais": "Colombia",
  "telefono": "6028201234",
  "correo": "contacto@cafeteriademo.co",
  "telefono_representante": "3009998877",
  "redes_sociales": { "instagram": "@cafeteriademo", "facebook": null, "whatsapp": "3009998877" },
  "descripcion": "Cafetería especializada en café de origen colombiano"
}
```

### `PATCH /api/v1/restaurante`

Mismos campos, todos opcionales. `logo_url` se llena subiendo primero con `POST /api/v1/uploads` (`api_04_productos_menu.md`).

**Response 200:** el objeto actualizado, misma forma que el `GET`.

---

## 10.2 — Zonas y Mesas

### `GET /api/v1/zonas`

CRUD completo de zonas (distinto del `GET /api/v1/operacion/mesas` de `api_02_operacion.md`, que es la vista agregada de solo lectura para el panel operativo).

**Response 200:**
```json
{
  "zonas": [
    { "id": 1, "codigo": "Z-001", "icono": "sala", "nombre": "Salón Principal", "num_mesas": 8, "estado": "activa" }
  ]
}
```

### `POST /api/v1/zonas`

**Request:**
```json
{ "icono": "sala", "nombre": "Salón Principal", "estado": "activa" }
```

**Response 201:**
```json
{ "id": 1, "codigo": "Z-001", "nombre": "Salón Principal" }
```

### `PATCH /api/v1/zonas/{id}`

### `DELETE /api/v1/zonas/{id}`

**Response 409 (tiene mesas asociadas):**
```json
{ "error": "No se puede eliminar, esta zona tiene 8 mesas asociadas" }
```

---

### `GET /api/v1/zonas/{id}/mesas`

**Response 200:**
```json
{
  "mesas": [
    { "id": 6, "codigo": "M-006", "numero": "Mesa 6", "capacidad": 2, "estado": "libre" }
  ]
}
```

### `POST /api/v1/zonas/{id}/mesas`

**Request:**
```json
{ "numero": "Mesa 9", "capacidad": 4, "estado": "libre" }
```

**Response 201:**
```json
{ "id": 9, "codigo": "M-009", "numero": "Mesa 9" }
```

### `PATCH /api/v1/mesas/{id}`

**Response 409 (no se puede deshabilitar una mesa con pedido activo):**
```json
{ "error": "No se puede deshabilitar, esta mesa tiene un pedido activo" }
```

### `DELETE /api/v1/mesas/{id}`

---

## 10.3 — Métodos de pago

> Este recurso ya se referencia desde `api_03_caja.md` (`metodo_pago_id` dentro del arreglo `pagos` al cobrar). Aquí se define su CRUD completo.
>
> **Acceso:** Jefe puede ver y editar. Admin solo puede consultar (`GET`) — los endpoints `POST`/`PATCH`/`DELETE` devuelven `403` si el rol es Admin.

### `GET /api/v1/metodos-pago`

**Response 200:**
```json
{
  "metodos_pago": [
    { "id": 1, "nombre": "Efectivo", "icono": "cash", "es_efectivo": true, "estado": "activo" },
    { "id": 2, "nombre": "Nequi", "icono": "nequi", "es_efectivo": false, "estado": "activo" }
  ]
}
```

### `POST /api/v1/metodos-pago`

**Request:**
```json
{ "nombre": "Datáfono", "icono": "card", "estado": "activo" }
```

**Response 201:**
```json
{ "id": 5, "nombre": "Datáfono" }
```

### `PATCH /api/v1/metodos-pago/{id}`

**Response 403 (intento de desactivar Efectivo):**
```json
{ "error": "El método de pago Efectivo no se puede desactivar" }
```

### `DELETE /api/v1/metodos-pago/{id}`

**Response 403 (es Efectivo):**
```json
{ "error": "El método de pago Efectivo no se puede eliminar" }
```

---

## 10.4 — Facturación DIAN

**Solo lectura** para el Jefe — la configuración real la hace soporte técnico directamente en base de datos/backend.

### `GET /api/v1/restaurante/facturacion-dian`

**Response 200:**
```json
{
  "estado_conexion": "activa",
  "prefijo": "FE",
  "rango_inicio": 1,
  "rango_fin": 5000,
  "numeracion_actual": 88,
  "fecha_expedicion": "2026-01-15",
  "fecha_vencimiento": "2027-01-15",
  "ambiente": "produccion",
  "estado": "vigente"
}
```

> No expone `client_id_factus` ni `client_secret_factus` — esos nunca salen del backend hacia el frontend, ni siquiera para el Jefe.

**Response 200 (sin configurar aún):**
```json
{
  "estado_conexion": "inactiva",
  "mensaje": "La facturación electrónica aún no está configurada. Contacta a soporte."
}
```

No existe `POST`/`PATCH` para este recurso desde la app — cualquier cambio requiere contactar soporte (según lo definido en la vista).

---

## 10.5 — Menú digital (QR)

### `GET /api/v1/restaurante/menu-digital`

**Response 200:**
```json
{
  "activo": true,
  "url_publica": "https://menu.cafepos.co/cafeteria-demo",
  "qr_image_url": "https://cdn.cafepos.co/1/qr/menu.png"
}
```

### `PATCH /api/v1/restaurante/menu-digital`

Activa/desactiva el menú público.

**Request:**
```json
{ "activo": false }
```

**Response 200:**
```json
{ "activo": false, "mensaje": "Menú digital desactivado" }
```

> El `qr_image_url` siempre apunta a la misma URL fija del tenant — no se regenera. Si se activan/desactivan productos o cambian precios en Productos y Menú, el contenido detrás de esa URL se actualiza automáticamente sin tocar el QR.

---

### Endpoint público (sin autenticación) — usado por el cliente que escanea el QR

### `GET /api/v1/publico/menu/{tenant-slug}`

**Response 200:**
```json
{
  "restaurante": { "nombre": "Cafetería Demo", "logo_url": "..." },
  "categorias": [
    {
      "nombre": "Bebidas",
      "productos": [
        { "nombre": "Café Americano", "descripcion": "Café negro filtrado", "precio_venta": 4500, "imagen": "..." }
      ]
    }
  ]
}
```

**Response 404 (menú desactivado o tenant no existe):**
```json
{ "error": "Menú no disponible" }
```

> Solo lista productos con `estado: "activo"` y `visibilidad: "visible"`. Sin acciones de pedido en V1 (solo lectura, según lo definido en la vista).

---

### Notas pendientes de este módulo
Ninguna — módulo sin ambigüedades pendientes.
