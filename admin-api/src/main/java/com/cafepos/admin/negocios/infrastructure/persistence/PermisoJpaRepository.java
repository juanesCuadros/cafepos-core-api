package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.Permiso;
import com.cafepos.admin.negocios.domain.PermisoRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermisoJpaRepository extends JpaRepository<Permiso, Integer>, PermisoRepository {
}
