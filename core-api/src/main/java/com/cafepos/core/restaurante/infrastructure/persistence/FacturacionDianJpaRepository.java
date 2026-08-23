package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.FacturacionDianResolucion;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface FacturacionDianJpaRepository extends TenantAwareRepository<FacturacionDianResolucion, Integer> {

    Optional<FacturacionDianResolucion> findTopByOrderByIdDesc();

    /**
     * estado_conexion_dian vive en configuracion_sistema (Modulo 11, todavia
     * no existe como modulo Java propio) — lectura acotada a esta unica
     * columna, no se mapea configuracion_sistema como entidad completa aca.
     * RLS ya scopea a la fila del tenant actual (UNIQUE(tenant_id)).
     */
    @Query(value = "SELECT estado_conexion_dian FROM configuracion_sistema LIMIT 1", nativeQuery = true)
    Optional<String> buscarEstadoConexionDian();
}
