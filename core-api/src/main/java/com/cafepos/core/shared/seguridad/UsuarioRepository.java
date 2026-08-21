package com.cafepos.core.shared.seguridad;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByTenantIdAndCorreo(Integer tenantId, String correo);
}
