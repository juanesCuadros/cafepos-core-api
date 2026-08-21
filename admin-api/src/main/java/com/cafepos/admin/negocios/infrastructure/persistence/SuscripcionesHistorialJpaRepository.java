package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.SuscripcionesHistorial;
import com.cafepos.admin.negocios.domain.SuscripcionesHistorialRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuscripcionesHistorialJpaRepository
        extends JpaRepository<SuscripcionesHistorial, Integer>, SuscripcionesHistorialRepository {
}
