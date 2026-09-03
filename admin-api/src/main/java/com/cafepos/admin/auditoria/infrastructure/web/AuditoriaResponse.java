package com.cafepos.admin.auditoria.infrastructure.web;

import com.cafepos.admin.auditoria.domain.SuperadminEventoAuditoria;

import java.time.OffsetDateTime;

public record AuditoriaResponse(
        Long id,
        Integer superadminId,
        String accion,
        String entidadTipo,
        Integer entidadId,
        String datosAntes,
        String datosDespues,
        String ipOrigen,
        String userAgent,
        OffsetDateTime fechaHora
) {
    public static AuditoriaResponse de(SuperadminEventoAuditoria e) {
        return new AuditoriaResponse(
                e.getId(),
                e.getSuperadminId(),
                e.getAccion(),
                e.getEntidadTipo(),
                e.getEntidadId(),
                e.getDatosAntes(),
                e.getDatosDespues(),
                e.getIpOrigen(),
                e.getUserAgent(),
                e.getFechaHora()
        );
    }
}
