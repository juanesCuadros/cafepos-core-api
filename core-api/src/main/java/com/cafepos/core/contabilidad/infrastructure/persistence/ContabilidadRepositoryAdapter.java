package com.cafepos.core.contabilidad.infrastructure.persistence;

import com.cafepos.core.contabilidad.domain.AperturaJornada;
import com.cafepos.core.contabilidad.domain.CajaMovimientoContable;
import com.cafepos.core.contabilidad.domain.CompraContable;
import com.cafepos.core.contabilidad.domain.ContabilidadRepository;
import com.cafepos.core.contabilidad.domain.GastoContable;
import com.cafepos.core.contabilidad.domain.ItemDesglose;
import com.cafepos.core.contabilidad.domain.VentaContable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
class ContabilidadRepositoryAdapter implements ContabilidadRepository {

    private final ContabilidadJpaRepository jpaRepository;

    ContabilidadRepositoryAdapter(ContabilidadJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Columnas TIMESTAMPTZ (venta.fecha_hora, caja_movimiento.fecha_hora,
     * caja_jornada.fecha_apertura) se filtran por instante UTC, nunca por
     * CAST directo de LocalDate contra timestamptz (ver gotcha en
     * ContabilidadJpaRepository) — fechaInicio es el inicio de ese dia en
     * UTC (limite inferior inclusive), fechaFin es el inicio del dia
     * SIGUIENTE en UTC (limite superior exclusivo, coincide con "< hasta"
     * ya usado en esas queries).
     */
    private static OffsetDateTime inicioDeDiaUtc(LocalDate fecha) {
        return fecha == null ? null : fecha.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    private static OffsetDateTime inicioDeDiaSiguienteUtc(LocalDate fecha) {
        return fecha == null ? null : fecha.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    @Override
    public BigDecimal totalVentasCobradas(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.totalVentasCobradas(inicioDeDiaUtc(fechaInicio), inicioDeDiaSiguienteUtc(fechaFin));
    }

    @Override
    public BigDecimal totalComprasNoAnuladas(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.totalComprasNoAnuladas(fechaInicio, fechaFin);
    }

    @Override
    public BigDecimal totalComprasPagadas(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.totalComprasPagadas(fechaInicio, fechaFin);
    }

    @Override
    public BigDecimal totalGastos(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.totalGastos(fechaInicio, fechaFin);
    }

    @Override
    public BigDecimal totalCajaMovimiento(String tipo, LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.totalCajaMovimiento(tipo, inicioDeDiaUtc(fechaInicio), inicioDeDiaSiguienteUtc(fechaFin));
    }

    @Override
    public BigDecimal totalVentasPorMetodoEfectivo(boolean esEfectivo, LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.totalVentasPorMetodoEfectivo(esEfectivo, inicioDeDiaUtc(fechaInicio),
                inicioDeDiaSiguienteUtc(fechaFin));
    }

    @Override
    public List<ItemDesglose> desgloseIngresosPorMetodoPago(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.desgloseIngresosPorMetodoPago(inicioDeDiaUtc(fechaInicio), inicioDeDiaSiguienteUtc(fechaFin)).stream()
                .map(row -> new ItemDesglose(row.getNombre(), row.getTotal()))
                .toList();
    }

    @Override
    public List<ItemDesglose> desgloseComprasPorProveedor(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.desgloseComprasPorProveedor(fechaInicio, fechaFin).stream()
                .map(row -> new ItemDesglose(row.getNombre(), row.getTotal()))
                .toList();
    }

    @Override
    public List<ItemDesglose> desgloseGastosPorCategoria(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.desgloseGastosPorCategoria(fechaInicio, fechaFin).stream()
                .map(row -> new ItemDesglose(row.getNombre(), row.getTotal()))
                .toList();
    }

    @Override
    public List<AperturaJornada> listarAperturasEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.listarAperturasEnRango(inicioDeDiaUtc(fechaInicio), inicioDeDiaSiguienteUtc(fechaFin)).stream()
                .map(row -> new AperturaJornada(row.getFechaApertura().atOffset(ZoneOffset.UTC), row.getMontoInicial()))
                .toList();
    }

    @Override
    public List<VentaContable> listarVentasCobradasEnRango(LocalDate fechaInicio, LocalDate fechaFin,
                                                            Integer metodoPagoId) {
        return jpaRepository.listarVentasCobradasEnRango(inicioDeDiaUtc(fechaInicio), inicioDeDiaSiguienteUtc(fechaFin),
                        metodoPagoId).stream()
                .map(row -> new VentaContable(row.getFechaHora().atOffset(ZoneOffset.UTC), row.getCodigo(),
                        row.getTotal(), row.getMesaNumero(), row.getUsuarioNombre(), row.getMetodoPago()))
                .toList();
    }

    @Override
    public List<CompraContable> listarComprasPagadasEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.listarComprasPagadasEnRango(fechaInicio, fechaFin).stream()
                .map(row -> new CompraContable(row.getFechaHora().atOffset(ZoneOffset.UTC), row.getCodigo(),
                        row.getTotal(), row.getProveedorNombre(), row.getUsuarioNombre()))
                .toList();
    }

    @Override
    public List<GastoContable> listarGastosEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return jpaRepository.listarGastosEnRango(fechaInicio, fechaFin).stream()
                .map(row -> new GastoContable(row.getFechaHora().atOffset(ZoneOffset.UTC), row.getCodigo(),
                        row.getMonto(), row.getDescripcion(), row.getMetodoPago(), row.getUsuarioNombre()))
                .toList();
    }

    @Override
    public List<CajaMovimientoContable> listarCajaMovimientoEnRango(String tipo, LocalDate fechaInicio,
                                                                     LocalDate fechaFin) {
        return jpaRepository.listarCajaMovimientoEnRango(tipo, inicioDeDiaUtc(fechaInicio),
                        inicioDeDiaSiguienteUtc(fechaFin)).stream()
                .map(row -> new CajaMovimientoContable(row.getId(), row.getFechaHora().atOffset(ZoneOffset.UTC),
                        row.getMonto(), row.getMotivo(), row.getUsuarioNombre()))
                .toList();
    }
}
