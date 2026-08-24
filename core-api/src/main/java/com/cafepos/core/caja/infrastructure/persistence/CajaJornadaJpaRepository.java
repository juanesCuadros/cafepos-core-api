package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.CajaJornada;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

interface CajaJornadaJpaRepository extends TenantAwareRepository<CajaJornada, Integer> {

    Optional<CajaJornada> findByEstado(String estado);

    /**
     * Nativa con CAST explicito a proposito: JPQL con ":param IS NULL OR ..."
     * genera un ? por cada ocurrencia del parametro con nombre, y Postgres
     * no puede inferir el tipo de un ? usado solo en "? IS NULL" sin
     * contexto — error real en runtime ("could not determine data type of
     * parameter $1"), no se ve en compilacion. El CAST se lo da explicito.
     */
    @Query(value = "SELECT * FROM caja_jornada WHERE "
            + "(CAST(:fechaInicio AS timestamptz) IS NULL OR fecha_apertura >= CAST(:fechaInicio AS timestamptz)) AND "
            + "(CAST(:fechaFin AS timestamptz) IS NULL OR fecha_apertura < CAST(:fechaFin AS timestamptz)) "
            + "ORDER BY fecha_apertura DESC", nativeQuery = true)
    List<CajaJornada> listarEnRango(@Param("fechaInicio") OffsetDateTime fechaInicio,
                                     @Param("fechaFin") OffsetDateTime fechaFin);
}
