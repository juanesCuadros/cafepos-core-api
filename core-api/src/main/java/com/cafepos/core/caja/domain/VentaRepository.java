package com.cafepos.core.caja.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Venta — implementado en infrastructure.persistence. */
public interface VentaRepository {

    Venta guardar(Venta venta);

    Optional<Venta> buscarPorId(Integer id);

    /** Cualquiera de los filtros puede venir null (sin filtrar por ese campo) — ver HistorialVentasService. */
    List<Venta> listar(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, Integer metodoPagoId, String estado,
                        Integer cajeroId);

    /** SUM(total) de ventas 'cobrado' de esa jornada — ver CajaJornadaService (total_ventas_actual). */
    BigDecimal sumaTotalCobradoDeJornada(Integer jornadaId);
}
