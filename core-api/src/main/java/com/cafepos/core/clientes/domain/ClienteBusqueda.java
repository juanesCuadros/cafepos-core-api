package com.cafepos.core.clientes.domain;

import java.math.BigDecimal;

/**
 * Fila de GET /clientes/buscar — version liviana para el modal de
 * seleccion de cliente en el POS. INFERENCIA PENDIENTE DE VERIFICAR:
 * api_03_caja.md (donde este endpoint esta documentado originalmente)
 * todavia no existe en el repo — estos campos (id, nombre, tipoDocumento,
 * numeroDocumentoEnmascarado, saldoFavor; sin telefono/correo, que no
 * aportan al flujo de elegir cliente en una venta) son la mejor inferencia
 * razonable hoy. Verificar contra ese contrato en cuanto este disponible
 * y ajustar si difiere.
 */
public record ClienteBusqueda(Integer id, String nombre, String tipoDocumento, String numeroDocumentoEnmascarado,
                               BigDecimal saldoFavor) {
}
