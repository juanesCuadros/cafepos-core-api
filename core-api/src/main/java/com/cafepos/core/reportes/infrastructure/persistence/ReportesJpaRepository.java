package com.cafepos.core.reportes.infrastructure.persistence;

import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReportesJpaRepository extends TenantAwareRepository<ReporteVentaEntity, Integer> {

    // 12.1 Ventas
    @Query(nativeQuery = true, value = """
        SELECT 
            DATE(v.fecha_hora AT TIME ZONE 'UTC') as fecha,
            SUM(v.total) as totalVentas,
            COUNT(v.id) as numTransacciones,
            SUM(v.total) / NULLIF(COUNT(v.id), 0) as ticketPromedio
        FROM venta v
        LEFT JOIN pedido p ON v.pedido_id = p.id
        WHERE v.estado = 'cobrado'
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR v.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR v.fecha_hora < CAST(:fechaFin AS timestamp))
          AND (CAST(:tipoPedido AS text) IS NULL OR p.tipo = CAST(:tipoPedido AS text))
          AND (CAST(:metodoPagoId AS integer) IS NULL OR EXISTS (
              SELECT 1 FROM venta_pago vp WHERE vp.venta_id = v.id AND vp.metodo_pago_id = CAST(:metodoPagoId AS integer)
          ))
        GROUP BY DATE(v.fecha_hora AT TIME ZONE 'UTC')
        ORDER BY fecha ASC
        """)
    List<VentasDiaProjection> reporteVentas(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin,
            @Param("metodoPagoId") Integer metodoPagoId,
            @Param("tipoPedido") String tipoPedido);

    interface VentasDiaProjection {
        java.sql.Date getFecha();
        java.math.BigDecimal getTotalVentas();
        Long getNumTransacciones();
        java.math.BigDecimal getTicketPromedio();
    }

    @Query(nativeQuery = true, value = """
        SELECT 
            SUM(v.total) as totalVentas,
            COUNT(v.id) as numTransacciones
        FROM venta v
        LEFT JOIN pedido p ON v.pedido_id = p.id
        WHERE v.estado = 'cobrado'
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR v.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR v.fecha_hora < CAST(:fechaFin AS timestamp))
          AND (CAST(:tipoPedido AS text) IS NULL OR p.tipo = CAST(:tipoPedido AS text))
          AND (CAST(:metodoPagoId AS integer) IS NULL OR EXISTS (
              SELECT 1 FROM venta_pago vp WHERE vp.venta_id = v.id AND vp.metodo_pago_id = CAST(:metodoPagoId AS integer)
          ))
        """)
    TotalesVentaProjection reporteVentasTotales(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin,
            @Param("metodoPagoId") Integer metodoPagoId,
            @Param("tipoPedido") String tipoPedido);

    interface TotalesVentaProjection {
        java.math.BigDecimal getTotalVentas();
        Long getNumTransacciones();
    }

    // 12.2 Productos mas vendidos
    @Query(nativeQuery = true, value = """
        SELECT 
            pi.producto_id as productoId,
            pr.nombre as producto,
            c.nombre as categoria,
            SUM(pi.cantidad) as unidadesVendidas,
            SUM(pi.cantidad * pi.precio_unitario) as totalVentas
        FROM venta v
        JOIN pedido p ON v.pedido_id = p.id
        JOIN pedido_item pi ON p.id = pi.pedido_id
        JOIN producto pr ON pi.producto_id = pr.id
        LEFT JOIN categoria c ON pr.categoria_id = c.id
        WHERE v.estado = 'cobrado'
          AND pi.producto_id IS NOT NULL
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR v.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR v.fecha_hora < CAST(:fechaFin AS timestamp))
          AND (CAST(:categoriaId AS integer) IS NULL OR pr.categoria_id = CAST(:categoriaId AS integer))
        GROUP BY pi.producto_id, pr.nombre, c.nombre
        ORDER BY unidadesVendidas DESC
        """)
    List<ProductosMasVendidosProjection> reporteProductosMasVendidos(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin,
            @Param("categoriaId") Integer categoriaId);

    interface ProductosMasVendidosProjection {
        Integer getProductoId();
        String getProducto();
        String getCategoria();
        java.math.BigDecimal getUnidadesVendidas();
        java.math.BigDecimal getTotalVentas();
    }

    @Query(nativeQuery = true, value = """
        SELECT COALESCE(SUM(pi.cantidad), 0)
        FROM venta v
        JOIN pedido_item pi ON v.pedido_id = pi.pedido_id
        WHERE v.estado = 'cobrado'
          AND pi.producto_id IS NOT NULL
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR v.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR v.fecha_hora < CAST(:fechaFin AS timestamp))
        """)
    java.math.BigDecimal totalUnidadesVendidas(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin);

    // 12.3 Ingredientes mas usados
    @Query(nativeQuery = true, value = """
        SELECT 
            mi.insumo_id as insumoId,
            i.nombre as ingrediente,
            ci.nombre as categoria,
            SUM(mi.cantidad) as cantidadUsada,
            i.unidad_medida as unidadMedida,
            SUM(mi.cantidad * i.costo_actual) as costoTotalConsumido
        FROM movimiento_inventario mi
        JOIN insumo i ON mi.insumo_id = i.id
        LEFT JOIN categoria_insumo ci ON i.categoria_insumo_id = ci.id
        WHERE mi.tipo = 'salida'
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR mi.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR mi.fecha_hora < CAST(:fechaFin AS timestamp))
          AND (CAST(:categoriaInsumoId AS integer) IS NULL OR i.categoria_insumo_id = CAST(:categoriaInsumoId AS integer))
        GROUP BY mi.insumo_id, i.nombre, ci.nombre, i.unidad_medida
        ORDER BY cantidadUsada DESC
        """)
    List<IngredientesMasUsadosProjection> reporteIngredientesMasUsados(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin,
            @Param("categoriaInsumoId") Integer categoriaInsumoId);

    interface IngredientesMasUsadosProjection {
        Integer getInsumoId();
        String getIngrediente();
        String getCategoria();
        java.math.BigDecimal getCantidadUsada();
        String getUnidadMedida();
        java.math.BigDecimal getCostoTotalConsumido();
    }

    // 12.4 Ventas por mesero
    @Query(nativeQuery = true, value = """
        SELECT 
            u.empleado_id as empleadoId,
            e.nombre as mesero,
            COUNT(v.id) as numPedidos,
            SUM(v.total) as totalVentas,
            SUM(v.total) / NULLIF(COUNT(v.id), 0) as ticketPromedio
        FROM venta v
        JOIN pedido p ON v.pedido_id = p.id
        JOIN usuario u ON p.usuario_id = u.id
        JOIN empleado e ON u.empleado_id = e.id
        WHERE v.estado = 'cobrado'
          AND u.empleado_id IS NOT NULL
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR v.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR v.fecha_hora < CAST(:fechaFin AS timestamp))
          AND (CAST(:empleadoId AS integer) IS NULL OR u.empleado_id = CAST(:empleadoId AS integer))
        GROUP BY u.empleado_id, e.nombre
        ORDER BY totalVentas DESC
        """)
    List<VentasPorMeseroProjection> reporteVentasPorMesero(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin,
            @Param("empleadoId") Integer empleadoId);

    interface VentasPorMeseroProjection {
        Integer getEmpleadoId();
        String getMesero();
        Long getNumPedidos();
        java.math.BigDecimal getTotalVentas();
        java.math.BigDecimal getTicketPromedio();
    }

    // 12.5 Ticket por dia
    @Query(nativeQuery = true, value = """
        SELECT 
            DATE(v.fecha_hora AT TIME ZONE 'UTC') as fecha,
            COUNT(v.id) as numTransacciones,
            SUM(v.total) as totalVentas,
            SUM(v.total) / NULLIF(COUNT(v.id), 0) as ticketPromedio,
            MAX(v.total) as ticketMasAlto,
            MIN(v.total) as ticketMasBajo
        FROM venta v
        WHERE v.estado = 'cobrado'
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR v.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR v.fecha_hora < CAST(:fechaFin AS timestamp))
        GROUP BY DATE(v.fecha_hora AT TIME ZONE 'UTC')
        ORDER BY fecha ASC
        """)
    List<TicketPorDiaProjection> reporteTicketPorDia(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin);

    interface TicketPorDiaProjection {
        java.sql.Date getFecha();
        Long getNumTransacciones();
        java.math.BigDecimal getTotalVentas();
        java.math.BigDecimal getTicketPromedio();
        java.math.BigDecimal getTicketMasAlto();
        java.math.BigDecimal getTicketMasBajo();
    }

    // 12.6 Clientes frecuentes
    @Query(nativeQuery = true, value = """
        SELECT 
            v.cliente_id as clienteId,
            c.nombre as cliente,
            COUNT(v.id) as numVisitas,
            SUM(v.total) as totalGastado,
            SUM(v.total) / NULLIF(COUNT(v.id), 0) as ticketPromedio,
            MAX(v.fecha_hora) as ultimaVisita
        FROM venta v
        JOIN cliente c ON v.cliente_id = c.id
        WHERE v.estado = 'cobrado'
          AND v.cliente_id IS NOT NULL
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR v.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR v.fecha_hora < CAST(:fechaFin AS timestamp))
        GROUP BY v.cliente_id, c.nombre
        ORDER BY numVisitas DESC
        """)
    List<ClientesFrecuentesProjection> reporteClientesFrecuentes(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin);

    interface ClientesFrecuentesProjection {
        Integer getClienteId();
        String getCliente();
        Long getNumVisitas();
        java.math.BigDecimal getTotalGastado();
        java.math.BigDecimal getTicketPromedio();
        Instant getUltimaVisita();
    }

    // 12.7 Demanda (vista = dia)
    // to_char(..., 'TMDay') devuelve el día en el locale por defecto de Postgres, pero si el locale no es español, podría fallar.
    // Una opción más segura e internacionalizada es usar 'ID' (1 = Lunes, 7 = Domingo) y mapearlo manualmente o usar un CASE.
    @Query(nativeQuery = true, value = """
        SELECT 
            CAST(EXTRACT(ISODOW FROM (v.fecha_hora AT TIME ZONE 'UTC')) AS integer) as diaSemana,
            COUNT(v.id) as numPedidos,
            SUM(v.total) as totalVentas,
            SUM(v.total) / NULLIF(COUNT(v.id), 0) as ticketPromedio
        FROM venta v
        WHERE v.estado = 'cobrado'
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR v.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR v.fecha_hora < CAST(:fechaFin AS timestamp))
        GROUP BY EXTRACT(ISODOW FROM (v.fecha_hora AT TIME ZONE 'UTC'))
        ORDER BY diaSemana ASC
        """)
    List<DemandaDiaProjection> reporteDemandaPorDia(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin);

    interface DemandaDiaProjection {
        Integer getDiaSemana(); // 1=Lunes, 7=Domingo
        Long getNumPedidos();
        java.math.BigDecimal getTotalVentas();
        java.math.BigDecimal getTicketPromedio();
    }

    // 12.7 Demanda (vista = hora)
    @Query(nativeQuery = true, value = """
        SELECT 
            CAST(EXTRACT(HOUR FROM (v.fecha_hora AT TIME ZONE 'UTC')) AS integer) as horaDia,
            COUNT(v.id) as numPedidos,
            SUM(v.total) as totalVentas,
            SUM(v.total) / NULLIF(COUNT(v.id), 0) as ticketPromedio
        FROM venta v
        WHERE v.estado = 'cobrado'
          AND (CAST(:fechaInicio AS timestamp) IS NULL OR v.fecha_hora >= CAST(:fechaInicio AS timestamp))
          AND (CAST(:fechaFin AS timestamp) IS NULL OR v.fecha_hora < CAST(:fechaFin AS timestamp))
        GROUP BY EXTRACT(HOUR FROM (v.fecha_hora AT TIME ZONE 'UTC'))
        ORDER BY horaDia ASC
        """)
    List<DemandaHoraProjection> reporteDemandaPorHora(
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin);

    interface DemandaHoraProjection {
        Integer getHoraDia(); // 0 a 23
        Long getNumPedidos();
        java.math.BigDecimal getTotalVentas();
        java.math.BigDecimal getTicketPromedio();
    }
}
