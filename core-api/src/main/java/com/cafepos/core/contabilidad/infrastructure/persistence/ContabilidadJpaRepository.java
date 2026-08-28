package com.cafepos.core.contabilidad.infrastructure.persistence;

import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Parametrizado sobre shared.seguridad.Usuario solo porque
 * TenantAwareRepository exige un tipo de entidad — las queries reales
 * leen venta/compra/gasto/caja_movimiento/caja_jornada y sus catalogos
 * directo por nombre de tabla, mismo patron ya usado en
 * personal.PropinaJpaRepository / inventario.VencimientoJpaRepository.
 *
 * GOTCHA REAL confirmado (ver CLAUDE.md): las columnas TIMESTAMPTZ
 * (venta.fecha_hora, caja_movimiento.fecha_hora, caja_jornada.fecha_apertura)
 * NUNCA se filtran con "CAST(:fechaComoLocalDate AS date)" comparado
 * directo contra la columna — el driver JDBC de Postgres ajusta el
 * timezone de la sesion al timezone POR DEFECTO DE LA JVM (no UTC), asi
 * que ese CAST implicito de date a timestamptz queda corrido por el
 * offset de esa zona (confirmado real: JVM en America/Bogota corrio los
 * resultados 5 horas). Por eso estos metodos reciben OffsetDateTime ya
 * resueltos en UTC desde Java (ContabilidadService, mismo patron que
 * HistorialVentasService.listar) — nunca LocalDate crudo. compra.fecha
 * y gasto.fecha SI son DATE de verdad (no TIMESTAMPTZ), ahi el CAST
 * contra LocalDate es seguro y sigue el patron normal del proyecto.
 */
interface ContabilidadJpaRepository extends TenantAwareRepository<Usuario, Integer> {

    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM venta "
            + "WHERE estado = 'cobrado' "
            + "AND (CAST(:desde AS timestamptz) IS NULL OR fecha_hora >= CAST(:desde AS timestamptz)) "
            + "AND (CAST(:hasta AS timestamptz) IS NULL OR fecha_hora < CAST(:hasta AS timestamptz))",
            nativeQuery = true)
    BigDecimal totalVentasCobradas(@Param("desde") OffsetDateTime desde, @Param("hasta") OffsetDateTime hasta);

    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM compra "
            + "WHERE estado != 'anulada' "
            + "AND (CAST(:fechaInicio AS date) IS NULL OR fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR fecha <= CAST(:fechaFin AS date))", nativeQuery = true)
    BigDecimal totalComprasNoAnuladas(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM compra "
            + "WHERE estado = 'pagada' "
            + "AND (CAST(:fechaInicio AS date) IS NULL OR fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR fecha <= CAST(:fechaFin AS date))", nativeQuery = true)
    BigDecimal totalComprasPagadas(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query(value = "SELECT COALESCE(SUM(monto), 0) FROM gasto "
            + "WHERE (CAST(:fechaInicio AS date) IS NULL OR fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR fecha <= CAST(:fechaFin AS date))", nativeQuery = true)
    BigDecimal totalGastos(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query(value = "SELECT COALESCE(SUM(monto), 0) FROM caja_movimiento "
            + "WHERE tipo = CAST(:tipo AS varchar) "
            + "AND (CAST(:desde AS timestamptz) IS NULL OR fecha_hora >= CAST(:desde AS timestamptz)) "
            + "AND (CAST(:hasta AS timestamptz) IS NULL OR fecha_hora < CAST(:hasta AS timestamptz))",
            nativeQuery = true)
    BigDecimal totalCajaMovimiento(@Param("tipo") String tipo, @Param("desde") OffsetDateTime desde,
                                    @Param("hasta") OffsetDateTime hasta);

    @Query(value = "SELECT COALESCE(SUM(vp.monto), 0) FROM venta_pago vp "
            + "JOIN venta v ON v.id = vp.venta_id "
            + "JOIN metodo_pago mp ON mp.id = vp.metodo_pago_id "
            + "WHERE v.estado = 'cobrado' AND mp.es_efectivo = CAST(:esEfectivo AS boolean) "
            + "AND (CAST(:desde AS timestamptz) IS NULL OR v.fecha_hora >= CAST(:desde AS timestamptz)) "
            + "AND (CAST(:hasta AS timestamptz) IS NULL OR v.fecha_hora < CAST(:hasta AS timestamptz))",
            nativeQuery = true)
    BigDecimal totalVentasPorMetodoEfectivo(@Param("esEfectivo") Boolean esEfectivo, @Param("desde") OffsetDateTime desde,
                                             @Param("hasta") OffsetDateTime hasta);

    @Query(value = "SELECT mp.nombre AS nombre, SUM(vp.monto) AS total FROM venta_pago vp "
            + "JOIN venta v ON v.id = vp.venta_id "
            + "JOIN metodo_pago mp ON mp.id = vp.metodo_pago_id "
            + "WHERE v.estado = 'cobrado' "
            + "AND (CAST(:desde AS timestamptz) IS NULL OR v.fecha_hora >= CAST(:desde AS timestamptz)) "
            + "AND (CAST(:hasta AS timestamptz) IS NULL OR v.fecha_hora < CAST(:hasta AS timestamptz)) "
            + "GROUP BY mp.nombre ORDER BY mp.nombre", nativeQuery = true)
    List<ItemDesgloseRow> desgloseIngresosPorMetodoPago(@Param("desde") OffsetDateTime desde,
                                                         @Param("hasta") OffsetDateTime hasta);

    @Query(value = "SELECT pr.nombre AS nombre, SUM(c.total) AS total FROM compra c "
            + "JOIN proveedor pr ON pr.id = c.proveedor_id "
            + "WHERE c.estado != 'anulada' "
            + "AND (CAST(:fechaInicio AS date) IS NULL OR c.fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR c.fecha <= CAST(:fechaFin AS date)) "
            + "GROUP BY pr.nombre ORDER BY pr.nombre", nativeQuery = true)
    List<ItemDesgloseRow> desgloseComprasPorProveedor(@Param("fechaInicio") LocalDate fechaInicio,
                                                       @Param("fechaFin") LocalDate fechaFin);

    @Query(value = "SELECT cg.nombre AS nombre, SUM(g.monto) AS total FROM gasto g "
            + "JOIN categoria_gasto cg ON cg.id = g.categoria_gasto_id "
            + "WHERE (CAST(:fechaInicio AS date) IS NULL OR g.fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR g.fecha <= CAST(:fechaFin AS date)) "
            + "GROUP BY cg.nombre ORDER BY cg.nombre", nativeQuery = true)
    List<ItemDesgloseRow> desgloseGastosPorCategoria(@Param("fechaInicio") LocalDate fechaInicio,
                                                      @Param("fechaFin") LocalDate fechaFin);

    @Query(value = "SELECT fecha_apertura AS fecha_apertura, monto_inicial AS monto_inicial FROM caja_jornada "
            + "WHERE (CAST(:desde AS timestamptz) IS NULL OR fecha_apertura >= CAST(:desde AS timestamptz)) "
            + "AND (CAST(:hasta AS timestamptz) IS NULL OR fecha_apertura < CAST(:hasta AS timestamptz)) "
            + "ORDER BY fecha_apertura ASC", nativeQuery = true)
    List<AperturaJornadaRow> listarAperturasEnRango(@Param("desde") OffsetDateTime desde,
                                                     @Param("hasta") OffsetDateTime hasta);

    /** metodo_pago viene con string_agg DISTINCT — venta con pago mixto muestra ambos nombres separados por coma. */
    @Query(value = "SELECT v.fecha_hora AS fecha_hora, v.codigo AS codigo, v.total AS total, m.numero AS mesa_numero, "
            + "u.nombre AS usuario_nombre, string_agg(DISTINCT mp.nombre, ', ' ORDER BY mp.nombre) AS metodo_pago "
            + "FROM venta v "
            + "JOIN usuario u ON u.id = v.cajero_id "
            + "LEFT JOIN pedido p ON p.id = v.pedido_id "
            + "LEFT JOIN mesa m ON m.id = p.mesa_id "
            + "LEFT JOIN venta_pago vp ON vp.venta_id = v.id "
            + "LEFT JOIN metodo_pago mp ON mp.id = vp.metodo_pago_id "
            + "WHERE v.estado = 'cobrado' "
            + "AND (CAST(:desde AS timestamptz) IS NULL OR v.fecha_hora >= CAST(:desde AS timestamptz)) "
            + "AND (CAST(:hasta AS timestamptz) IS NULL OR v.fecha_hora < CAST(:hasta AS timestamptz)) "
            + "AND (CAST(:metodoPagoId AS int) IS NULL OR EXISTS (SELECT 1 FROM venta_pago vp2 "
            + "WHERE vp2.venta_id = v.id AND vp2.metodo_pago_id = CAST(:metodoPagoId AS int))) "
            + "GROUP BY v.id, v.fecha_hora, v.codigo, v.total, m.numero, u.nombre "
            + "ORDER BY v.fecha_hora", nativeQuery = true)
    List<VentaContableRow> listarVentasCobradasEnRango(@Param("desde") OffsetDateTime desde,
                                                        @Param("hasta") OffsetDateTime hasta,
                                                        @Param("metodoPagoId") Integer metodoPagoId);

    @Query(value = "SELECT c.created_at AS fecha_hora, c.codigo AS codigo, c.total AS total, "
            + "pr.nombre AS proveedor_nombre, u.nombre AS usuario_nombre "
            + "FROM compra c "
            + "JOIN proveedor pr ON pr.id = c.proveedor_id "
            + "JOIN usuario u ON u.id = c.usuario_id "
            + "WHERE c.estado = 'pagada' "
            + "AND (CAST(:fechaInicio AS date) IS NULL OR c.fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR c.fecha <= CAST(:fechaFin AS date)) "
            + "ORDER BY c.created_at", nativeQuery = true)
    List<CompraContableRow> listarComprasPagadasEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                                         @Param("fechaFin") LocalDate fechaFin);

    @Query(value = "SELECT g.created_at AS fecha_hora, g.codigo AS codigo, g.monto AS monto, "
            + "g.descripcion AS descripcion, g.metodo_pago AS metodo_pago, u.nombre AS usuario_nombre "
            + "FROM gasto g "
            + "JOIN usuario u ON u.id = g.usuario_id "
            + "WHERE (CAST(:fechaInicio AS date) IS NULL OR g.fecha >= CAST(:fechaInicio AS date)) "
            + "AND (CAST(:fechaFin AS date) IS NULL OR g.fecha <= CAST(:fechaFin AS date)) "
            + "ORDER BY g.created_at", nativeQuery = true)
    List<GastoContableRow> listarGastosEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                                @Param("fechaFin") LocalDate fechaFin);

    @Query(value = "SELECT cm.fecha_hora AS fecha_hora, cm.monto AS monto, cm.motivo AS motivo, "
            + "u.nombre AS usuario_nombre "
            + "FROM caja_movimiento cm "
            + "JOIN usuario u ON u.id = cm.usuario_id "
            + "WHERE cm.tipo = CAST(:tipo AS varchar) "
            + "AND (CAST(:desde AS timestamptz) IS NULL OR cm.fecha_hora >= CAST(:desde AS timestamptz)) "
            + "AND (CAST(:hasta AS timestamptz) IS NULL OR cm.fecha_hora < CAST(:hasta AS timestamptz)) "
            + "ORDER BY cm.fecha_hora", nativeQuery = true)
    List<CajaMovimientoContableRow> listarCajaMovimientoEnRango(@Param("tipo") String tipo,
                                                                 @Param("desde") OffsetDateTime desde,
                                                                 @Param("hasta") OffsetDateTime hasta);
}
