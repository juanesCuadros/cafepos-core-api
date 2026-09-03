package com.cafepos.admin.negocios.domain;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    long countByTenantId(Integer tenantId);
}
