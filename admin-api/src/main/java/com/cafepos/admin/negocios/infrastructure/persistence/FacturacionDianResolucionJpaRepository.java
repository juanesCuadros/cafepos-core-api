package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.FacturacionDianResolucion;
import com.cafepos.admin.negocios.domain.FacturacionDianResolucionRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacturacionDianResolucionJpaRepository
        extends JpaRepository<FacturacionDianResolucion, Integer>, FacturacionDianResolucionRepository {

    Optional<FacturacionDianResolucion> findTopByTenantIdOrderByIdDesc(Integer tenantId);

    @Override
    default Optional<FacturacionDianResolucion> buscarVigentePorTenant(Integer tenantId) {
        return findTopByTenantIdOrderByIdDesc(tenantId);
    }

    @Override
    default FacturacionDianResolucion guardar(FacturacionDianResolucion resolucion) {
        return save(resolucion);
    }
}
