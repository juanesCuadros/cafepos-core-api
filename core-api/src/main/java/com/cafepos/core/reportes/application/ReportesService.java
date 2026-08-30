package com.cafepos.core.reportes.application;

import com.cafepos.core.personal.application.PropinaCalculoService;
import com.cafepos.core.personal.application.ResumenPropinas;
import com.cafepos.core.reportes.infrastructure.persistence.ReportesJpaRepository;
import com.cafepos.core.reportes.infrastructure.persistence.ReportesJpaRepository.*;
import com.cafepos.core.reportes.infrastructure.web.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ReportesService {

    private static final String LOCALE_DAYS[] = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    private final ReportesJpaRepository reportesJpaRepository;
    private final PropinaCalculoService propinaCalculoService;

    public ReportesService(ReportesJpaRepository reportesJpaRepository, PropinaCalculoService propinaCalculoService) {
        this.reportesJpaRepository = reportesJpaRepository;
        this.propinaCalculoService = propinaCalculoService;
    }

    @Transactional(readOnly = true)
    public ReporteVentasResponse obtenerReporteVentas(LocalDate fechaInicio, LocalDate fechaFin, Integer metodoPagoId, String tipoPedido) {
        Instant inicio = fechaInicio != null ? fechaInicio.atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;
        Instant fin = fechaFin != null ? fechaFin.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;

        List<VentasDiaProjection> proyecciones = reportesJpaRepository.reporteVentas(inicio, fin, metodoPagoId, tipoPedido);
        
        final long dias = (fechaInicio != null && fechaFin != null) ? ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1 : 0;

        Instant prevInicio = null;
        Instant prevFin = null;
        if (inicio != null && fin != null) {
            prevInicio = fechaInicio.minusDays(dias).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
            prevFin = fechaFin.minusDays(dias).plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
        }

        final List<VentasDiaProjection> proyeccionesAnterior = (prevInicio != null && prevFin != null) 
            ? reportesJpaRepository.reporteVentas(prevInicio, prevFin, metodoPagoId, tipoPedido)
            : null;

        List<ReporteVentasFila> filas = proyecciones.stream().map(p -> {
            LocalDate fechaActual = p.getFecha().toLocalDate();
            String comparativa = "N/A";
            
            if (proyeccionesAnterior != null && fechaInicio != null) {
                long offset = ChronoUnit.DAYS.between(fechaInicio, fechaActual);
                LocalDate fechaObjetivo = fechaInicio.minusDays(dias).plusDays(offset);
                
                VentasDiaProjection anterior = proyeccionesAnterior.stream()
                        .filter(a -> a.getFecha().toLocalDate().equals(fechaObjetivo))
                        .findFirst()
                        .orElse(null);
                        
                BigDecimal totalActual = p.getTotalVentas() != null ? p.getTotalVentas() : BigDecimal.ZERO;
                BigDecimal totalAnterior = (anterior != null && anterior.getTotalVentas() != null) ? anterior.getTotalVentas() : BigDecimal.ZERO;
                
                if (totalAnterior.compareTo(BigDecimal.ZERO) == 0) {
                    comparativa = totalActual.compareTo(BigDecimal.ZERO) > 0 ? "N/A" : "0.0%";
                } else {
                    BigDecimal diff = totalActual.subtract(totalAnterior);
                    BigDecimal pct = diff.divide(totalAnterior, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                    String sign = pct.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
                    comparativa = sign + pct.setScale(1, RoundingMode.HALF_UP).toPlainString() + "%";
                }
            }
            
            return new ReporteVentasFila(fechaActual, p.getTotalVentas(), p.getNumTransacciones(), p.getTicketPromedio(), comparativa);
        }).toList();

        TotalesVentaProjection totalesProj = reportesJpaRepository.reporteVentasTotales(inicio, fin, metodoPagoId, tipoPedido);
        ReporteVentasTotales totales = new ReporteVentasTotales(
            totalesProj != null && totalesProj.getTotalVentas() != null ? totalesProj.getTotalVentas() : BigDecimal.ZERO,
            totalesProj != null ? totalesProj.getNumTransacciones() : 0L
        );

        return new ReporteVentasResponse(filas, totales);
    }

    @Transactional(readOnly = true)
    public ReporteProductosMasVendidosResponse obtenerReporteProductos(LocalDate fechaInicio, LocalDate fechaFin, Integer categoriaId) {
        Instant inicio = fechaInicio != null ? fechaInicio.atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;
        Instant fin = fechaFin != null ? fechaFin.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;

        List<ProductosMasVendidosProjection> proyecciones = reportesJpaRepository.reporteProductosMasVendidos(inicio, fin, categoriaId);
        BigDecimal totalUnidadesGlobal = reportesJpaRepository.totalUnidadesVendidas(inicio, fin);
        
        AtomicInteger pos = new AtomicInteger(1);
        List<ReporteProductosMasVendidosFila> filas = proyecciones.stream().map(p -> {
            String porcentaje = "0.0%";
            if (totalUnidadesGlobal != null && totalUnidadesGlobal.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal pct = p.getUnidadesVendidas().divide(totalUnidadesGlobal, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                porcentaje = pct.setScale(1, RoundingMode.HALF_UP).toPlainString() + "%";
            }
            return new ReporteProductosMasVendidosFila(
                pos.getAndIncrement(), p.getProducto(), p.getCategoria(), p.getUnidadesVendidas(), p.getTotalVentas(), porcentaje
            );
        }).toList();
        
        return new ReporteProductosMasVendidosResponse(filas);
    }

    @Transactional(readOnly = true)
    public ReporteIngredientesMasUsadosResponse obtenerReporteIngredientes(LocalDate fechaInicio, LocalDate fechaFin, Integer categoriaInsumoId) {
        Instant inicio = fechaInicio != null ? fechaInicio.atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;
        Instant fin = fechaFin != null ? fechaFin.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;

        List<IngredientesMasUsadosProjection> proyecciones = reportesJpaRepository.reporteIngredientesMasUsados(inicio, fin, categoriaInsumoId);
        
        AtomicInteger pos = new AtomicInteger(1);
        List<ReporteIngredientesMasUsadosFila> filas = proyecciones.stream().map(p -> 
            new ReporteIngredientesMasUsadosFila(pos.getAndIncrement(), p.getIngrediente(), p.getCategoria(), p.getCantidadUsada(), p.getUnidadMedida(), p.getCostoTotalConsumido())
        ).toList();
        
        return new ReporteIngredientesMasUsadosResponse(filas);
    }

    @Transactional(readOnly = true)
    public ReporteVentasPorMeseroResponse obtenerReporteVentasMesero(LocalDate fechaInicio, LocalDate fechaFin, Integer empleadoId) {
        Instant inicio = fechaInicio != null ? fechaInicio.atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;
        Instant fin = fechaFin != null ? fechaFin.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;

        List<VentasPorMeseroProjection> proyecciones = reportesJpaRepository.reporteVentasPorMesero(inicio, fin, empleadoId);
        
        List<ReporteVentasPorMeseroFila> filas = proyecciones.stream().map(p -> {
            ResumenPropinas propinas = propinaCalculoService.calcular(p.getEmpleadoId(), fechaInicio, fechaFin);
            BigDecimal totalPropinas = propinas.totalPropinas() != null ? propinas.totalPropinas() : BigDecimal.ZERO;
            return new ReporteVentasPorMeseroFila(p.getMesero(), p.getNumPedidos(), p.getTotalVentas(), p.getTicketPromedio(), totalPropinas);
        }).toList();
        
        return new ReporteVentasPorMeseroResponse(filas);
    }

    @Transactional(readOnly = true)
    public ReporteTicketPorDiaResponse obtenerReporteTicketDia(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicio = fechaInicio != null ? fechaInicio.atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;
        Instant fin = fechaFin != null ? fechaFin.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;

        List<TicketPorDiaProjection> proyecciones = reportesJpaRepository.reporteTicketPorDia(inicio, fin);
        
        List<ReporteTicketPorDiaFila> filas = proyecciones.stream().map(p -> 
            new ReporteTicketPorDiaFila(p.getFecha().toLocalDate(), p.getNumTransacciones(), p.getTotalVentas(), p.getTicketPromedio(), p.getTicketMasAlto(), p.getTicketMasBajo())
        ).toList();
        
        return new ReporteTicketPorDiaResponse(filas);
    }

    @Transactional(readOnly = true)
    public ReporteClientesFrecuentesResponse obtenerReporteClientes(LocalDate fechaInicio, LocalDate fechaFin) {
        Instant inicio = fechaInicio != null ? fechaInicio.atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;
        Instant fin = fechaFin != null ? fechaFin.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;

        List<ClientesFrecuentesProjection> proyecciones = reportesJpaRepository.reporteClientesFrecuentes(inicio, fin);
        
        AtomicInteger pos = new AtomicInteger(1);
        List<ReporteClientesFrecuentesFila> filas = proyecciones.stream().map(p -> 
            new ReporteClientesFrecuentesFila(pos.getAndIncrement(), p.getCliente(), p.getNumVisitas(), p.getTotalGastado(), p.getTicketPromedio(), p.getUltimaVisita() != null ? LocalDate.ofInstant(p.getUltimaVisita(), ZoneOffset.UTC) : null)
        ).toList();
        
        return new ReporteClientesFrecuentesResponse(filas);
    }

    @Transactional(readOnly = true)
    public ReporteDemandaResponse obtenerReporteDemanda(LocalDate fechaInicio, LocalDate fechaFin, String vista) {
        Instant inicio = fechaInicio != null ? fechaInicio.atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;
        Instant fin = fechaFin != null ? fechaFin.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant() : null;

        if ("hora".equals(vista)) {
            List<DemandaHoraProjection> proyecciones = reportesJpaRepository.reporteDemandaPorHora(inicio, fin);
            List<ReporteDemandaFila> filas = proyecciones.stream().map(p -> {
                String horaStr = String.format("%02d:00", p.getHoraDia());
                return new ReporteDemandaFila(null, horaStr, p.getNumPedidos(), p.getTotalVentas(), p.getTicketPromedio());
            }).toList();
            return new ReporteDemandaResponse(filas);
        } else {
            List<DemandaDiaProjection> proyecciones = reportesJpaRepository.reporteDemandaPorDia(inicio, fin);
            List<ReporteDemandaFila> filas = proyecciones.stream().map(p -> {
                String diaStr = (p.getDiaSemana() >= 1 && p.getDiaSemana() <= 7) ? LOCALE_DAYS[p.getDiaSemana() - 1] : "Desconocido";
                return new ReporteDemandaFila(diaStr, null, p.getNumPedidos(), p.getTotalVentas(), p.getTicketPromedio());
            }).toList();
            return new ReporteDemandaResponse(filas);
        }
    }
}
