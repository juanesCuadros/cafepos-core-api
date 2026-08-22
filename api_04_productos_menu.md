# CaféPOS — Contrato de API
## Módulo — Productos y Menú
_Versión 1.0 · Agosto 2026_

> Convenciones generales, versionado y manejo de errores en `api_00_autenticacion.md`

Cubre las 5 sub-vistas: Productos, Categorías, Combos, Recetas, Promociones.

---

## Utilidad compartida — Subida de imágenes

Usada por Productos, Combos, y más adelante Restaurante (logo) y Gastos (comprobante). Se define una sola vez aquí porque es el primer módulo que la necesita.

### `POST /api/v1/uploads`

**Request:** `multipart/form-data`, campo `file`

**Response 201:**
```json
{ "url": "https://cdn.cafepos.co/{tenant_id}/uploads/f3a9c1.jpg" }
```

**Response 400 (formato no soportado):**
```json
{ "error": "Formato no soportado. Usa JPG, PNG o WEBP" }
```

> El backend guarda el archivo en object storage S3-compatible (RNF-022), nunca en la base de datos. El `url` devuelto es el que se guarda en el campo `imagen`/`logo_url` correspondiente.

---

## 4.1 — Productos

### `GET /api/v1/productos`

**Query params:** `?categoria_id=&estado=&q=` (búsqueda por nombre)

**Response 200:**
```json
{
  "productos": [
    {
      "id": 15,
      "codigo": "PROD-0015",
      "nombre": "Café Americano",
      "categoria": { "id": 3, "nombre": "Bebidas" },
      "imagen": "https://cdn.cafepos.co/1/uploads/cafe.jpg",
      "precio_venta": 4500,
      "estado": "activo"
    }
  ]
}
```

### `POST /api/v1/productos`

**Request:**
```json
{
  "nombre": "Café Americano",
  "descripcion": "Café negro filtrado",
  "categoria_id": 3,
  "imagen": "https://cdn.cafepos.co/1/uploads/cafe.jpg",
  "precio_venta": 4500,
  "costo_estimado": 1800,
  "area_cocina_id": 2,
  "tasa_impuesto": null,
  "maneja_receta": false,
  "maneja_inventario": true,
  "unidad_medida": "unidad",
  "stock_minimo": 10,
  "estado": "activo",
  "visibilidad": "visible"
}
```

**Response 201:**
```json
{
  "id": 15,
  "codigo": "PROD-0015",
  "nombre": "Café Americano",
  "created_at": "2026-08-19T10:00:00Z"
}
```

**Response 400 (precio inválido):**
```json
{ "error": "precio_venta debe ser mayor a 0" }
```

### `GET /api/v1/productos/{id}`

Detalle completo con todos los campos.

### `PATCH /api/v1/productos/{id}`

Mismos campos que el `POST`, todos opcionales (solo se envían los que cambian).

### `DELETE /api/v1/productos/{id}`

**Response 200 (sin ventas asociadas — elimina de verdad):**
```json
{ "mensaje": "Producto eliminado" }
```

**Response 200 (con ventas asociadas — no se puede eliminar físicamente):**
```json
{
  "mensaje": "Este producto tiene ventas registradas, se marcó como inactivo en vez de eliminarse",
  "estado": "inactivo"
}
```

> Regla de negocio ya anotada en el esquema: nunca se hace `DELETE` físico de un producto con historial de ventas — el backend lo convierte automáticamente en soft-delete (`estado: inactivo`).

---

## 4.2 — Categorías

### `GET /api/v1/categorias`

**Response 200:**
```json
{
  "categorias": [
    { "id": 3, "icono": "coffee-cup", "nombre": "Bebidas", "orden": 1, "num_productos": 12, "estado": "activa" }
  ]
}
```

### `POST /api/v1/categorias`

**Request:**
```json
{ "icono": "coffee-cup", "nombre": "Bebidas", "descripcion": null, "orden": 1, "estado": "activa" }
```

**Response 201:**
```json
{ "id": 3, "nombre": "Bebidas" }
```

### `PATCH /api/v1/categorias/{id}`

Igual estructura, campos opcionales.

### `DELETE /api/v1/categorias/{id}`

**Response 200:**
```json
{ "mensaje": "Categoría eliminada" }
```

**Response 409 (tiene productos asociados):**
```json
{ "error": "No se puede eliminar, esta categoría tiene 12 productos asociados" }
```

---

## 4.3 — Combos

### `GET /api/v1/combos`

**Response 200:**
```json
{
  "combos": [
    { "id": 4, "codigo": "COMBO-0004", "nombre": "Combo Ejecutivo", "precio": 15000, "estado": "activo" }
  ]
}
```

### `POST /api/v1/combos`

Crea el combo con sus grupos y productos en una sola llamada.

**Request:**
```json
{
  "nombre": "Combo Ejecutivo",
  "descripcion": "Plato + bebida + postre",
  "imagen": "https://cdn.cafepos.co/1/uploads/combo.jpg",
  "precio": 15000,
  "estado": "activo",
  "grupos": [
    { "nombre": "Bebida", "productos_ids": [22, 30, 31] },
    { "nombre": "Postre", "productos_ids": [40, 41] }
  ]
}
```

**Response 201:**
```json
{
  "id": 4,
  "codigo": "COMBO-0004",
  "nombre": "Combo Ejecutivo",
  "precio": 15000,
  "grupos": [
    { "id": 1, "nombre": "Bebida", "productos": [{ "id": 22, "nombre": "Jugo natural" }, { "id": 30, "nombre": "Gaseosa" }, { "id": 31, "nombre": "Agua" }] },
    { "id": 2, "nombre": "Postre", "productos": [{ "id": 40, "nombre": "Brownie" }, { "id": 41, "nombre": "Flan" }] }
  ]
}
```

### `GET /api/v1/combos/{id}`

Detalle completo con grupos y productos anidados (misma forma que la respuesta del `POST`).

### `PATCH /api/v1/combos/{id}`

Edita solo los datos generales del combo (nombre, precio, imagen, estado) — **no** los grupos, esos se editan con los endpoints de abajo.

### `DELETE /api/v1/combos/{id}`

**Response 200:**
```json
{ "mensaje": "Combo eliminado" }
```

### Gestión de grupos dentro de un combo existente

```
POST   /api/v1/combos/{id}/grupos                              body: { "nombre": "Acompañamiento" }
PATCH  /api/v1/combos/{id}/grupos/{grupo_id}                    body: { "nombre": "Acompañamiento" }
DELETE /api/v1/combos/{id}/grupos/{grupo_id}
POST   /api/v1/combos/{id}/grupos/{grupo_id}/productos          body: { "producto_id": 45 }
DELETE /api/v1/combos/{id}/grupos/{grupo_id}/productos/{producto_id}
```

Todos devuelven el combo completo actualizado (misma forma que `GET /combos/{id}`).

---

## 4.4 — Recetas

### `GET /api/v1/recetas`

**Query params:** `?q=` (búsqueda por nombre de producto)

**Response 200:**
```json
{
  "recetas": [
    { "id": 7, "producto": "Alfajor casero", "num_insumos": 4, "costo_total": 1250, "estado": "activa" }
  ]
}
```

### `POST /api/v1/recetas`

**Request:**
```json
{
  "producto_id": 55,
  "estado": "activa",
  "insumos": [
    { "insumo_id": 10, "cantidad": 150, "unidad_medida": "gramo" },
    { "insumo_id": 12, "cantidad": 50, "unidad_medida": "ml" }
  ]
}
```

**Response 201:**
```json
{
  "id": 7,
  "producto_id": 55,
  "costo_total": 1250,
  "estado": "activa",
  "insumos": [
    { "id": 20, "insumo": "Harina de trigo", "cantidad": 150, "unidad_medida": "gramo" },
    { "id": 21, "insumo": "Leche", "cantidad": 50, "unidad_medida": "ml" }
  ]
}
```

> `costo_total` se calcula automáticamente en el backend sumando `cantidad × costo_actual` de cada insumo — no se envía desde el frontend.

**Response 409 (el producto ya tiene una receta):**
```json
{ "error": "Este producto ya tiene una receta asignada" }
```

### `GET /api/v1/recetas/{id}`

Detalle completo, misma forma que la respuesta del `POST`.

### `PATCH /api/v1/recetas/{id}`

Edita el estado de la receta (activa/inactiva). Para modificar insumos, usar los endpoints de abajo.

### `DELETE /api/v1/recetas/{id}`

**Response 200:**
```json
{ "mensaje": "Receta eliminada" }
```

### Gestión de insumos dentro de una receta existente

```
POST   /api/v1/recetas/{id}/insumos          body: { "insumo_id": 14, "cantidad": 20, "unidad_medida": "gramo" }
DELETE /api/v1/recetas/{id}/insumos/{insumo_id}
```

Ambos devuelven la receta completa actualizada, con `costo_total` recalculado.

---

## 4.5 — Promociones

### `GET /api/v1/promociones`

**Response 200:**
```json
{
  "promociones": [
    { "id": 3, "nombre": "2x1 Bebidas Martes", "tipo_descuento": "porcentaje", "vigencia_inicio": "2026-08-01", "vigencia_fin": "2026-12-31", "estado": "activa" }
  ]
}
```

### `POST /api/v1/promociones`

**Request:**
```json
{
  "nombre": "2x1 Bebidas Martes",
  "tipo_descuento": "porcentaje",
  "valor_descuento": 50,
  "aplica_a": "producto",
  "productos_ids": [15, 22, 30],
  "vigencia_inicio": "2026-08-01",
  "vigencia_fin": "2026-12-31",
  "dias_semana": "martes",
  "hora_inicio": "12:00",
  "hora_fin": "15:00",
  "cantidad_minima": 2,
  "monto_minimo": null,
  "estado": "activa"
}
```

**Response 201:**
```json
{
  "id": 3,
  "nombre": "2x1 Bebidas Martes",
  "productos": [{ "id": 15, "nombre": "Café Americano" }, { "id": 22, "nombre": "Jugo natural" }, { "id": 30, "nombre": "Gaseosa" }]
}
```

### `GET /api/v1/promociones/{id}`

Detalle completo.

### `PATCH /api/v1/promociones/{id}`

Mismos campos, opcionales.

### `DELETE /api/v1/promociones/{id}`

**Response 200:**
```json
{ "mensaje": "Promoción eliminada" }
```

---

### Endpoint interno relacionado (ya usado en Caja)

`POST /api/v1/pedidos/{id}/promociones-sugeridas` — evaluado internamente por el backend al agregar ítems al pedido, no lo llama el frontend directamente; el backend revisa qué promociones activas aplican y las incluye en la respuesta de `GET /api/v1/pedidos/{id}` como `promociones_sugeridas[]`. **Ajuste pendiente:** agregar este campo a la respuesta de detalle de pedido en `api_02_operacion.md`.

### Notas pendientes de este módulo
- Confirmar si `dias_semana` en promociones se maneja como texto simple (`"martes"` o `"lunes,martes"`) o como arreglo `["martes"]` — por consistencia con el resto del contrato (que usa arreglos para listas), recomiendo cambiarlo a arreglo antes de que backend empiece a construir
