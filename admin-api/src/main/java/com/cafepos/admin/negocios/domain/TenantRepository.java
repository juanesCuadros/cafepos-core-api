package com.cafepos.admin.negocios.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TenantRepository {

    boolean existsBySlug(String slug);

    Optional<Tenant> findById(Integer id);

    Optional<Tenant> findBySlug(String slug);

    List<Tenant> findByEstadoAndFechaProximaFacturacionBefore(String estado, LocalDate fecha);

    List<Tenant> findAll();

    Page<Tenant> findAll(Pageable pageable);

    Page<Tenant> buscarConFiltros(String query, String estado, Integer planId, Pageable pageable);

    long count();

    long countByEstado(String estado);

    Tenant save(Tenant tenant);
}
