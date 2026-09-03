package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.Usuario;
import com.cafepos.admin.negocios.domain.UsuarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, Integer>, UsuarioRepository {

    @Override
    long countByTenantId(Integer tenantId);
}
