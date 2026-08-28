package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.application.ContabilidadService;
import com.cafepos.core.contabilidad.domain.FormatoNoDisponibleException;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Solo lectura, exclusivo Jefe (ver catalogo permiso). "?formato=pdf|excel"
 * diferido a proposito en las 3 rutas — 501 antes de tocar cualquier
 * dato, ver DECISIONES YA TOMADAS.
 *
 * @RequestParam con nombre explicito en snake_case en TODOS los query
 * params — confirmado real (ver sesion de Gastos) que Spring NO convierte
 * automaticamente "fecha_inicio" (el nombre real que manda el contrato) a
 * un parametro Java "fechaInicio" sin @RequestParam("fecha_inicio")
 * explicito: sin esto, el filtro se ignora en silencio (nunca lanza
 * error, simplemente actua como si no se hubiera mandado). Revisar
 * cualquier controller viejo que use @RequestParam sin nombre explicito
 * para filtros de fecha/id — puede tener el mismo problema.
 */
@RestController
@RequestMapping("/contabilidad")
@Tag(name = ApiTags.CONTABILIDAD)
public class ContabilidadController {

    private final ContabilidadService contabilidadService;

    public ContabilidadController(ContabilidadService contabilidadService) {
        this.contabilidadService = contabilidadService;
    }

    @GetMapping("/balance")
    @PreAuthorize("hasPermission('contabilidad.balance_general', 'ver')")
    @Operation(summary = "Balance general del periodo")
    public BalanceResponse balance(@RequestParam(value = "fecha_inicio", required = false) LocalDate fechaInicio,
                                    @RequestParam(value = "fecha_fin", required = false) LocalDate fechaFin,
                                    @RequestParam(required = false) String vista,
                                    @RequestParam(required = false) String formato) {
        exigirFormatoJson(formato);
        return BalanceResponse.de(contabilidadService.balance(fechaInicio, fechaFin, vista));
    }

    @GetMapping("/flujo-caja")
    @PreAuthorize("hasPermission('contabilidad.flujo_caja', 'ver')")
    @Operation(summary = "Flujo de caja del periodo, con movimientos cronologicos y saldo acumulado")
    public FlujoCajaResponse flujoCaja(@RequestParam(value = "fecha_inicio", required = false) LocalDate fechaInicio,
                                        @RequestParam(value = "fecha_fin", required = false) LocalDate fechaFin,
                                        @RequestParam(required = false) String formato) {
        exigirFormatoJson(formato);
        return FlujoCajaResponse.de(contabilidadService.flujoCaja(fechaInicio, fechaFin));
    }

    @GetMapping("/transacciones")
    @PreAuthorize("hasPermission('contabilidad.transacciones', 'ver')")
    @Operation(summary = "Historial unificado de ventas, compras, gastos y movimientos de caja")
    public TransaccionesResponse transacciones(
            @RequestParam(value = "fecha_inicio", required = false) LocalDate fechaInicio,
            @RequestParam(value = "fecha_fin", required = false) LocalDate fechaFin,
            @RequestParam(required = false) String tipo,
            @RequestParam(value = "metodo_pago_id", required = false) Integer metodoPagoId,
            @RequestParam(required = false) String formato) {
        exigirFormatoJson(formato);
        return TransaccionesResponse.de(contabilidadService.transacciones(fechaInicio, fechaFin, tipo, metodoPagoId));
    }

    private void exigirFormatoJson(String formato) {
        if ("pdf".equals(formato) || "excel".equals(formato)) {
            throw new FormatoNoDisponibleException();
        }
    }
}
