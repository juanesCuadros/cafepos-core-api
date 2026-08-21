package com.cafepos.admin.negocios.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TenantRepository {

    boolean existsBySlug(String slug);

    Optional<Tenant> findById(Integer id);

    List<Tenant> findByEstadoAndFechaProximaFacturacionBefore(String estado, LocalDate fecha);

    Tenant save(Tenant tenant);
}
