package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.MetodoPago;
import com.cafepos.admin.negocios.domain.MetodoPagoRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetodoPagoJpaRepository extends JpaRepository<MetodoPago, Integer>, MetodoPagoRepository {
}
