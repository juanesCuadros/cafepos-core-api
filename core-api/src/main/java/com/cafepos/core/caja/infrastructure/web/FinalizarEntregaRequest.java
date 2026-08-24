package com.cafepos.core.caja.infrastructure.web;

/** metodo_entrega se acepta por compatibilidad con el contrato pero no se usa ni se persiste (sin logica de impresora/correo real, fuera de alcance). */
public record FinalizarEntregaRequest(String metodoEntrega) {
}
