package com.cafepos.core.caja.domain;

import java.math.BigDecimal;
import java.util.List;

/** Puerto de persistencia de VentaPago — implementado en infrastructure.persistence. */
public interface VentaPagoRepository {

    VentaPago guardar(VentaPago pago);

    List<VentaPago> listarDeVenta(Integer ventaId);

    /** Nombres de metodo_pago de una venta (puede repetir si hubiera 2 pagos del mismo metodo). */
    List<String> nombresMetodoPagoDeVenta(Integer ventaId);

    /**
     * SUM(monto) de pagos en efectivo (metodo_pago.es_efectivo=true) de
     * ventas 'cobrado' de esa jornada — arqueo, ver CajaJornadaService
     * (monto_final_sistema = SOLO efectivo).
     */
    BigDecimal sumaEfectivoDeJornada(Integer jornadaId);

    /** TODOS los metodos (no solo efectivo), agrupados — ver CajaJornadaService (resumen_por_metodo_pago). */
    List<ResumenMetodoPago> resumenPorMetodoDeJornada(Integer jornadaId);
}
