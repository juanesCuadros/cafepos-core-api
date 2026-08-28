package com.cafepos.core.contabilidad.application;

import com.cafepos.core.contabilidad.domain.AperturaJornada;
import com.cafepos.core.contabilidad.domain.BalanceGeneral;
import com.cafepos.core.contabilidad.domain.CajaMovimientoContable;
import com.cafepos.core.contabilidad.domain.CompraContable;
import com.cafepos.core.contabilidad.domain.ContabilidadRepository;
import com.cafepos.core.contabilidad.domain.FlujoCaja;
import com.cafepos.core.contabilidad.domain.GastoContable;
import com.cafepos.core.contabilidad.domain.MovimientoCronologico;
import com.cafepos.core.contabilidad.domain.RangoFechas;
import com.cafepos.core.contabilidad.domain.Transaccion;
import com.cafepos.core.contabilidad.domain.VentaContable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ContabilidadService {

    private static final String VISTA_DEFAULT = "mes";

    private final ContabilidadRepository contabilidadRepository;

    public ContabilidadService(ContabilidadRepository contabilidadRepository) {
        this.contabilidadRepository = contabilidadRepository;
    }

    @Transactional(readOnly = true)
    public BalanceGeneral balance(LocalDate fechaInicio, LocalDate fechaFin, String vista) {
        RangoFechas rango = resolverRango(fechaInicio, fechaFin, vista);
        BigDecimal ingresos = contabilidadRepository.totalVentasCobradas(rango.fechaInicio(), rango.fechaFin());
        BigDecimal compras = contabilidadRepository.totalComprasNoAnuladas(rango.fechaInicio(), rango.fechaFin());
        BigDecimal gastos = contabilidadRepository.totalGastos(rango.fechaInicio(), rango.fechaFin());
        BigDecimal egresosCaja = contabilidadRepository.totalCajaMovimiento("egreso", rango.fechaInicio(),
                rango.fechaFin());
        BigDecimal utilidadBruta = ingresos.subtract(compras).subtract(gastos).subtract(egresosCaja);

        return new BalanceGeneral(ingresos, compras, gastos, egresosCaja, utilidadBruta,
                contabilidadRepository.desgloseIngresosPorMetodoPago(rango.fechaInicio(), rango.fechaFin()),
                contabilidadRepository.desgloseComprasPorProveedor(rango.fechaInicio(), rango.fechaFin()),
                contabilidadRepository.desgloseGastosPorCategoria(rango.fechaInicio(), rango.fechaFin()));
    }

    /**
     * saldo_inicial = monto_inicial de la PRIMERA jornada (por
     * fecha_apertura) que abrio dentro del rango, o 0 si ninguna abrio
     * ahi (ver DECISIONES YA TOMADAS del prompt original).
     *
     * Si el rango tiene MAS de una jornada, el monto_inicial de cada
     * jornada POSTERIOR a la primera es dinero real que entro a la caja
     * durante el periodo — se suma a entradas.ingresos_caja para que el
     * saldo_final agregado siempre coincida con el ultimo saldo_acumulado
     * de movimientos_cronologicos (esa lista ya incluye esas aperturas
     * como eventos de entrada, ver construirMovimientosCronologicos). La
     * PRIMERA jornada no se suma aca porque ya esta cubierta por
     * saldo_inicial — sumarla tambien la contaria dos veces.
     */
    @Transactional(readOnly = true)
    public FlujoCaja flujoCaja(LocalDate fechaInicio, LocalDate fechaFin) {
        RangoFechas rango = resolverRango(fechaInicio, fechaFin, null);
        LocalDate fi = rango.fechaInicio();
        LocalDate ff = rango.fechaFin();

        List<AperturaJornada> aperturas = contabilidadRepository.listarAperturasEnRango(fi, ff);
        BigDecimal saldoInicial = aperturas.isEmpty() ? BigDecimal.ZERO : aperturas.get(0).montoInicial();
        BigDecimal ingresosPorAperturasPosteriores = aperturas.stream().skip(1)
                .map(AperturaJornada::montoInicial).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasEfectivo = contabilidadRepository.totalVentasPorMetodoEfectivo(true, fi, ff);
        BigDecimal ventasOtrosMetodos = contabilidadRepository.totalVentasPorMetodoEfectivo(false, fi, ff);
        BigDecimal ingresosCaja = contabilidadRepository.totalCajaMovimiento("ingreso", fi, ff)
                .add(ingresosPorAperturasPosteriores);
        BigDecimal comprasPagadas = contabilidadRepository.totalComprasPagadas(fi, ff);
        BigDecimal gastosOperativos = contabilidadRepository.totalGastos(fi, ff);
        BigDecimal egresosCaja = contabilidadRepository.totalCajaMovimiento("egreso", fi, ff);

        BigDecimal totalEntradas = ventasEfectivo.add(ventasOtrosMetodos).add(ingresosCaja);
        BigDecimal totalSalidas = comprasPagadas.add(gastosOperativos).add(egresosCaja);
        BigDecimal saldoFinal = saldoInicial.add(totalEntradas).subtract(totalSalidas);
        BigDecimal diferencia = saldoFinal.subtract(saldoInicial);

        List<MovimientoCronologico> movimientos = construirMovimientosCronologicos(fi, ff, aperturas);

        return new FlujoCaja(saldoInicial, ventasEfectivo, ventasOtrosMetodos, ingresosCaja, comprasPagadas,
                gastosOperativos, egresosCaja, saldoFinal, diferencia, movimientos);
    }

    /**
     * Un solo evento por linea, ordenado cronologico ASC, saldo_acumulado corrido paso a paso.
     * Arranca en CERO, nunca en saldoInicial — la apertura de la PRIMERA jornada del rango ya
     * viene incluida como uno de los eventos (listarAperturasEnRango trae todas las jornadas
     * abiertas en el rango, no solo la primera), asi que sumarla aca Y ADEMAS arrancar desde
     * saldoInicial la contaria dos veces. Bug real confirmado probando con datos reales: el
     * saldo_acumulado del ultimo movimiento quedaba saldoInicial de mas por encima de saldo_final
     * cuando habia una sola jornada en el rango (coincide con el ejemplo del contrato:
     * primer evento "Apertura de caja" con monto=100000 trae saldo_acumulado=100000, es decir
     * arranca de 0).
     */
    private List<MovimientoCronologico> construirMovimientosCronologicos(LocalDate fi, LocalDate ff,
                                                                          List<AperturaJornada> aperturas) {
        record EventoCrudo(OffsetDateTime fecha, String descripcion, boolean entrada, BigDecimal monto) {
        }

        List<EventoCrudo> eventos = new ArrayList<>();
        for (AperturaJornada a : aperturas) {
            eventos.add(new EventoCrudo(a.fechaApertura(), "Apertura de caja", true, a.montoInicial()));
        }
        for (VentaContable v : contabilidadRepository.listarVentasCobradasEnRango(fi, ff, null)) {
            eventos.add(new EventoCrudo(v.fechaHora(), "Venta " + v.codigo(), true, v.total()));
        }
        for (CajaMovimientoContable m : contabilidadRepository.listarCajaMovimientoEnRango("ingreso", fi, ff)) {
            eventos.add(new EventoCrudo(m.fechaHora(), m.motivo(), true, m.monto()));
        }
        for (CajaMovimientoContable m : contabilidadRepository.listarCajaMovimientoEnRango("egreso", fi, ff)) {
            eventos.add(new EventoCrudo(m.fechaHora(), m.motivo(), false, m.monto()));
        }
        for (CompraContable c : contabilidadRepository.listarComprasPagadasEnRango(fi, ff)) {
            eventos.add(new EventoCrudo(c.fechaHora(), "Compra a " + c.proveedorNombre(), false, c.total()));
        }
        for (GastoContable g : contabilidadRepository.listarGastosEnRango(fi, ff)) {
            eventos.add(new EventoCrudo(g.fechaHora(), g.descripcion(), false, g.monto()));
        }

        eventos.sort(Comparator.comparing(EventoCrudo::fecha));

        List<MovimientoCronologico> resultado = new ArrayList<>();
        BigDecimal saldoCorriendo = BigDecimal.ZERO;
        for (EventoCrudo e : eventos) {
            saldoCorriendo = e.entrada() ? saldoCorriendo.add(e.monto()) : saldoCorriendo.subtract(e.monto());
            String tipo = e.entrada() ? MovimientoCronologico.TIPO_ENTRADA : MovimientoCronologico.TIPO_SALIDA;
            resultado.add(new MovimientoCronologico(e.fecha(), e.descripcion(), tipo, e.monto(), saldoCorriendo));
        }
        return resultado;
    }

    /**
     * metodo_pago_id solo tiene efecto real sobre la fuente "venta" — para
     * las demas fuentes (compra/gasto/caja_movimiento) el concepto de
     * metodo de pago electronico no aplica, asi que el filtro se ignora
     * ahi a proposito si viene junto con un tipo distinto de "venta" (o
     * sin tipo, trayendo las 5 fuentes) — documentado en el prompt
     * original.
     */
    @Transactional(readOnly = true)
    public List<Transaccion> transacciones(LocalDate fechaInicio, LocalDate fechaFin, String tipo,
                                            Integer metodoPagoId) {
        RangoFechas rango = resolverRango(fechaInicio, fechaFin, null);
        LocalDate fi = rango.fechaInicio();
        LocalDate ff = rango.fechaFin();

        List<Transaccion> resultado = new ArrayList<>();
        if (tipo == null || Transaccion.TIPO_VENTA.equals(tipo)) {
            for (VentaContable v : contabilidadRepository.listarVentasCobradasEnRango(fi, ff, metodoPagoId)) {
                // mesa.numero ya incluye la palabra "Mesa" (ej. "Mesa 1"), sin concatenarla de nuevo.
                String descripcion = v.mesaNumero() != null ? "Venta " + v.mesaNumero() : "Venta " + v.codigo();
                resultado.add(new Transaccion(v.fechaHora(), v.codigo(), Transaccion.TIPO_VENTA, descripcion,
                        v.total(), v.metodoPago(), v.usuarioNombre()));
            }
        }
        if (tipo == null || Transaccion.TIPO_COMPRA.equals(tipo)) {
            for (CompraContable c : contabilidadRepository.listarComprasPagadasEnRango(fi, ff)) {
                resultado.add(new Transaccion(c.fechaHora(), c.codigo(), Transaccion.TIPO_COMPRA,
                        "Compra a " + c.proveedorNombre(), c.total().negate(), null, c.usuarioNombre()));
            }
        }
        if (tipo == null || Transaccion.TIPO_GASTO.equals(tipo)) {
            for (GastoContable g : contabilidadRepository.listarGastosEnRango(fi, ff)) {
                resultado.add(new Transaccion(g.fechaHora(), g.codigo(), Transaccion.TIPO_GASTO, g.descripcion(),
                        g.monto().negate(), g.metodoPago(), g.usuarioNombre()));
            }
        }
        if (tipo == null || Transaccion.TIPO_EGRESO_CAJA.equals(tipo)) {
            for (CajaMovimientoContable m : contabilidadRepository.listarCajaMovimientoEnRango("egreso", fi, ff)) {
                resultado.add(new Transaccion(m.fechaHora(), null, Transaccion.TIPO_EGRESO_CAJA, m.motivo(),
                        m.monto().negate(), null, m.usuarioNombre()));
            }
        }
        if (tipo == null || Transaccion.TIPO_INGRESO_CAJA.equals(tipo)) {
            for (CajaMovimientoContable m : contabilidadRepository.listarCajaMovimientoEnRango("ingreso", fi, ff)) {
                resultado.add(new Transaccion(m.fechaHora(), null, Transaccion.TIPO_INGRESO_CAJA, m.motivo(),
                        m.monto(), null, m.usuarioNombre()));
            }
        }

        return resultado.stream().sorted(Comparator.comparing(Transaccion::fechaHora).reversed()).toList();
    }

    /**
     * Si fecha_inicio o fecha_fin vienen explicitos (aunque sea uno solo),
     * mandan siempre y "vista" se ignora por completo. Si ninguno viene,
     * "vista" decide el rango por defecto (dia/semana/mes/año); si "vista"
     * tampoco viene, default a mes calendario actual. Flujo de caja y
     * Transacciones no tienen "vista" en su contrato — llaman esto con
     * vista=null, cayendo siempre al default de mes cuando no dan fechas.
     */
    private RangoFechas resolverRango(LocalDate fechaInicio, LocalDate fechaFin, String vista) {
        if (fechaInicio != null || fechaFin != null) {
            return new RangoFechas(fechaInicio, fechaFin);
        }
        LocalDate hoy = LocalDate.now();
        String vistaEfectiva = vista != null ? vista : VISTA_DEFAULT;
        return switch (vistaEfectiva) {
            case "dia" -> new RangoFechas(hoy, hoy);
            case "semana" -> new RangoFechas(hoy.with(DayOfWeek.MONDAY), hoy.with(DayOfWeek.SUNDAY));
            case "año", "ano" -> new RangoFechas(hoy.withDayOfYear(1), hoy.withDayOfYear(hoy.lengthOfYear()));
            default -> new RangoFechas(hoy.withDayOfMonth(1), hoy.withDayOfMonth(hoy.lengthOfMonth()));
        };
    }
}
