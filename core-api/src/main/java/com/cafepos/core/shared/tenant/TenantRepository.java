package com.cafepos.core.shared.tenant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Integer> {

    Optional<Tenant> findBySlug(String slug);
}
