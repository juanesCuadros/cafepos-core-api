package com.cafepos.admin.auditoria.infrastructure.persistence;

import com.cafepos.admin.auditoria.domain.SuperadminEventoAuditoria;
import com.cafepos.admin.auditoria.domain.SuperadminEventoAuditoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperadminEventoAuditoriaJpaRepository
        extends JpaRepository<SuperadminEventoAuditoria, Long>, SuperadminEventoAuditoriaRepository {

    @Override
    Page<SuperadminEventoAuditoria> findBySuperadminId(Integer superadminId, Pageable pageable);

    @Override
    Page<SuperadminEventoAuditoria> findByEntidadTipoAndEntidadId(String entidadTipo, Integer entidadId, Pageable pageable);
}
