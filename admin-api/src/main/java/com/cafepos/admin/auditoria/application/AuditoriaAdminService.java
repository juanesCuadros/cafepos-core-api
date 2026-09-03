package com.cafepos.admin.auditoria.application;

import com.cafepos.admin.auditoria.domain.SuperadminEventoAuditoria;
import com.cafepos.admin.auditoria.domain.SuperadminEventoAuditoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaAdminService {

    private final SuperadminEventoAuditoriaRepository auditoriaRepository;

    public AuditoriaAdminService(SuperadminEventoAuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(Integer superadminId, String accion, String entidadTipo, Integer entidadId,
                          String datosAntes, String datosDespues, String ipOrigen, String userAgent) {
        try {
            SuperadminEventoAuditoria evento = new SuperadminEventoAuditoria(
                    superadminId, accion, entidadTipo, entidadId,
                    datosAntes, datosDespues, ipOrigen, userAgent);
            auditoriaRepository.save(evento);
        } catch (Exception e) {
            // No romper el flujo principal si el log de auditoría falla
        }
    }

    @Transactional(readOnly = true)
    public Page<SuperadminEventoAuditoria> listar(Pageable pageable) {
        return auditoriaRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<SuperadminEventoAuditoria> listarPorEntidad(String entidadTipo, Integer entidadId, Pageable pageable) {
        return auditoriaRepository.findByEntidadTipoAndEntidadId(entidadTipo, entidadId, pageable);
    }
}
