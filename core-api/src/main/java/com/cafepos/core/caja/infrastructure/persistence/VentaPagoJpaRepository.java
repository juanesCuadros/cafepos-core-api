package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.VentaPago;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Las queries nativas cruzan a las tablas venta y metodo_pago (esta ultima
 * de com.cafepos.core.restaurante) directo por SQL — Modulith no restringe
 * el acceso a tablas via SQL nativo, solo el acceso a clases Java de otro
 * modulo (mismo patron ya usado en restaurante.FacturacionDianJpaRepository
 * leyendo configuracion_sistema).
 */
interface VentaPagoJpaRepository extends TenantAwareRepository<VentaPago, Integer> {

    List<VentaPago> findByVentaId(Integer ventaId);

    @Query(value = "SELECT mp.nombre FROM venta_pago vp "
            + "JOIN metodo_pago mp ON mp.id = vp.metodo_pago_id "
            + "WHERE vp.venta_id = :ventaId", nativeQuery = true)
    List<String> nombresMetodoPagoDeVenta(@Param("ventaId") Integer ventaId);

    @Query(value = "SELECT COALESCE(SUM(vp.monto), 0) FROM venta_pago vp "
            + "JOIN venta v ON v.id = vp.venta_id "
            + "JOIN metodo_pago mp ON mp.id = vp.metodo_pago_id "
            + "WHERE v.jornada_id = :jornadaId AND v.estado = 'cobrado' AND mp.es_efectivo = true",
            nativeQuery = true)
    BigDecimal sumaEfectivoDeJornada(@Param("jornadaId") Integer jornadaId);

    @Query(value = "SELECT mp.nombre AS nombre, SUM(vp.monto) AS total FROM venta_pago vp "
            + "JOIN venta v ON v.id = vp.venta_id "
            + "JOIN metodo_pago mp ON mp.id = vp.metodo_pago_id "
            + "WHERE v.jornada_id = :jornadaId AND v.estado = 'cobrado' "
            + "GROUP BY mp.nombre ORDER BY mp.nombre", nativeQuery = true)
    List<ResumenMetodoPagoRow> resumenPorMetodoDeJornada(@Param("jornadaId") Integer jornadaId);
}
