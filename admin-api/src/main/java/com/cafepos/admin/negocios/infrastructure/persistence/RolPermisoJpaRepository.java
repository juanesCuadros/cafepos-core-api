package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.RolPermiso;
import com.cafepos.admin.negocios.domain.RolPermisoRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolPermisoJpaRepository extends JpaRepository<RolPermiso, Integer>, RolPermisoRepository {
}
