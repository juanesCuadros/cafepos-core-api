package com.cafepos.core.configuracion.domain;

/** modulo va COMPLETO (ej "caja.pos"), no solo la accion — evita ambiguedad de multiples "ver" en un mismo modulo padre. */
public record PermisoMatrizItem(Integer permisoId, String modulo, String accion, boolean activo) {
}
