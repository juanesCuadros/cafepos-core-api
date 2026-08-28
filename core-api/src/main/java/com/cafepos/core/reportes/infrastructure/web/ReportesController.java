package com.cafepos.core.reportes.infrastructure.web;

import com.cafepos.core.reportes.application.ReportesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
public class ReportesController {

    private final ReportesService reportesService;

    public ReportesController(ReportesService reportesService) {
        this.reportesService = reportesService;
    }

    private ResponseEntity<Object> handleFormato(String formato, Object response) {
        if ("pdf".equalsIgnoreCase(formato) || "excel".equalsIgnoreCase(formato)) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(Map.of("error", "Exportacion a PDF/Excel no disponible todavia"));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ventas")
    @PreAuthorize("hasPermission('reportes.ventas', 'ver')")
    public ResponseEntity<Object> ventas(
            @RequestParam(required = false) LocalDate fecha_inicio,
            @RequestParam(required = false) LocalDate fecha_fin,
            @RequestParam(required = false) Integer metodo_pago_id,
            @RequestParam(required = false) String tipo_pedido,
            @RequestParam(required = false, defaultValue = "json") String formato) {
        if (!"json".equalsIgnoreCase(formato)) return handleFormato(formato, null);
        return handleFormato(formato, reportesService.obtenerReporteVentas(fecha_inicio, fecha_fin, metodo_pago_id, tipo_pedido));
    }

    @GetMapping("/productos-mas-vendidos")
    @PreAuthorize("hasPermission('reportes.productos_mas_vendidos', 'ver')")
    public ResponseEntity<Object> productosMasVendidos(
            @RequestParam(required = false) LocalDate fecha_inicio,
            @RequestParam(required = false) LocalDate fecha_fin,
            @RequestParam(required = false) Integer categoria_id,
            @RequestParam(required = false, defaultValue = "json") String formato) {
        if (!"json".equalsIgnoreCase(formato)) return handleFormato(formato, null);
        return handleFormato(formato, reportesService.obtenerReporteProductos(fecha_inicio, fecha_fin, categoria_id));
    }

    @GetMapping("/ingredientes-mas-usados")
    @PreAuthorize("hasPermission('reportes.ingredientes_mas_usados', 'ver')")
    public ResponseEntity<Object> ingredientesMasUsados(
            @RequestParam(required = false) LocalDate fecha_inicio,
            @RequestParam(required = false) LocalDate fecha_fin,
            @RequestParam(required = false) Integer categoria_insumo_id,
            @RequestParam(required = false, defaultValue = "json") String formato) {
        if (!"json".equalsIgnoreCase(formato)) return handleFormato(formato, null);
        return handleFormato(formato, reportesService.obtenerReporteIngredientes(fecha_inicio, fecha_fin, categoria_insumo_id));
    }

    @GetMapping("/ventas-por-mesero")
    @PreAuthorize("hasPermission('reportes.ventas_por_mesero', 'ver')")
    public ResponseEntity<Object> ventasPorMesero(
            @RequestParam(required = false) LocalDate fecha_inicio,
            @RequestParam(required = false) LocalDate fecha_fin,
            @RequestParam(required = false) Integer empleado_id,
            @RequestParam(required = false, defaultValue = "json") String formato) {
        if (!"json".equalsIgnoreCase(formato)) return handleFormato(formato, null);
        return handleFormato(formato, reportesService.obtenerReporteVentasMesero(fecha_inicio, fecha_fin, empleado_id));
    }

    @GetMapping("/ticket-por-dia")
    @PreAuthorize("hasPermission('reportes.ticket_por_dia', 'ver')")
    public ResponseEntity<Object> ticketPorDia(
            @RequestParam(required = false) LocalDate fecha_inicio,
            @RequestParam(required = false) LocalDate fecha_fin,
            @RequestParam(required = false, defaultValue = "json") String formato) {
        if (!"json".equalsIgnoreCase(formato)) return handleFormato(formato, null);
        return handleFormato(formato, reportesService.obtenerReporteTicketDia(fecha_inicio, fecha_fin));
    }

    @GetMapping("/clientes-frecuentes")
    @PreAuthorize("hasPermission('reportes.clientes_frecuentes', 'ver')")
    public ResponseEntity<Object> clientesFrecuentes(
            @RequestParam(required = false) LocalDate fecha_inicio,
            @RequestParam(required = false) LocalDate fecha_fin,
            @RequestParam(required = false, defaultValue = "json") String formato) {
        if (!"json".equalsIgnoreCase(formato)) return handleFormato(formato, null);
        return handleFormato(formato, reportesService.obtenerReporteClientes(fecha_inicio, fecha_fin));
    }

    @GetMapping("/demanda")
    @PreAuthorize("hasPermission('reportes.hora_dia_demanda', 'ver')")
    public ResponseEntity<Object> demanda(
            @RequestParam(required = false) LocalDate fecha_inicio,
            @RequestParam(required = false) LocalDate fecha_fin,
            @RequestParam(required = false, defaultValue = "dia") String vista,
            @RequestParam(required = false, defaultValue = "json") String formato) {
        if (!"json".equalsIgnoreCase(formato)) return handleFormato(formato, null);
        return handleFormato(formato, reportesService.obtenerReporteDemanda(fecha_inicio, fecha_fin, vista));
    }
}
