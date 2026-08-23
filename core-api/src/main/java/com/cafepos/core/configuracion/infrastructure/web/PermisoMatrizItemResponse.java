package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.PermisoMatrizItem;

/** modulo va COMPLETO (ej "caja.pos") — deviacion deliberada ya aprobada respecto al contrato original. */
public record PermisoMatrizItemResponse(Integer permisoId, String modulo, String accion, boolean activo) {

    public static PermisoMatrizItemResponse de(PermisoMatrizItem item) {
        return new PermisoMatrizItemResponse(item.permisoId(), item.modulo(), item.accion(), item.activo());
    }
}
