package com.cafepos.admin.auditoria.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SuperadminEventoAuditoriaRepository {

    SuperadminEventoAuditoria save(SuperadminEventoAuditoria evento);

    Page<SuperadminEventoAuditoria> findAll(Pageable pageable);

    Page<SuperadminEventoAuditoria> findBySuperadminId(Integer superadminId, Pageable pageable);

    Page<SuperadminEventoAuditoria> findByEntidadTipoAndEntidadId(String entidadTipo, Integer entidadId, Pageable pageable);

    Optional<SuperadminEventoAuditoria> findById(Long id);
}
