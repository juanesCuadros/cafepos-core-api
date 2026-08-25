package com.cafepos.core.personal.infrastructure.persistence;

import com.cafepos.core.personal.domain.Empleado;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Parametrizado sobre Empleado solo porque TenantAwareRepository exige un
 * tipo de entidad — la query real lee venta/pedido/configuracion_sistema
 * directo por nombre (no son entidades de este modulo), mismo patron ya
 * usado en inventario.VencimientoJpaRepository.
 */
interface PropinaJpaRepository extends TenantAwareRepository<Empleado, Integer> {

    @Query(value = "SELECT propina_destino AS propina_destino, propina_pct_mesero AS propina_pct_mesero "
            + "FROM configuracion_sistema LIMIT 1", nativeQuery = true)
    ConfiguracionPropinaRow obtenerConfiguracionPropina();

    /** Solo venta.estado='cobrado' — una anulada nunca genero propina real. */
    @Query(value = "SELECT v.codigo AS codigo, v.fecha_hora AS fecha, v.propina AS propina "
            + "FROM venta v JOIN pedido p ON p.id = v.pedido_id "
            + "WHERE v.estado = 'cobrado' AND p.usuario_id = :usuarioId "
            + "AND (CAST(:desde AS timestamptz) IS NULL OR v.fecha_hora >= CAST(:desde AS timestamptz)) "
            + "AND (CAST(:hasta AS timestamptz) IS NULL OR v.fecha_hora < CAST(:hasta AS timestamptz)) "
            + "ORDER BY v.fecha_hora DESC", nativeQuery = true)
    List<VentaConPropinaRow> listarVentasConPropina(@Param("usuarioId") Integer usuarioId,
                                                      @Param("desde") OffsetDateTime desde,
                                                      @Param("hasta") OffsetDateTime hasta);
}
