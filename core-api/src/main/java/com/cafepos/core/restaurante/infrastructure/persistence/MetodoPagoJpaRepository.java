package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.MetodoPago;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface MetodoPagoJpaRepository extends TenantAwareRepository<MetodoPago, Integer> {

    /** es_efectivo primero para que Efectivo siempre aparezca al tope, igual que el ejemplo del contrato. */
    @Query(value = "SELECT id AS id, nombre AS nombre, icono AS icono, es_efectivo AS es_efectivo, "
            + "estado AS estado FROM metodo_pago ORDER BY es_efectivo DESC, nombre", nativeQuery = true)
    List<MetodoPagoResumenRow> listar();
}
