package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TenantJpaRepository extends JpaRepository<Tenant, Integer>, TenantRepository {

    @Override
    boolean existsBySlug(String slug);

    @Override
    Optional<Tenant> findBySlug(String slug);

    @Override
    List<Tenant> findByEstadoAndFechaProximaFacturacionBefore(String estado, LocalDate fecha);

    @Override
    long countByEstado(String estado);

    @Override
    @Query("SELECT t FROM Tenant t WHERE " +
           "(:query IS NULL OR LOWER(t.slug) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:estado IS NULL OR t.estado = :estado) AND " +
           "(:planId IS NULL OR t.planId = :planId)")
    Page<Tenant> buscarConFiltros(@Param("query") String query,
                                  @Param("estado") String estado,
                                  @Param("planId") Integer planId,
                                  Pageable pageable);
}
