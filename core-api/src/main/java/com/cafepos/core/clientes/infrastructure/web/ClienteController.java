package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.application.ClienteService;
import com.cafepos.core.clientes.domain.Cliente;
import com.cafepos.core.shared.openapi.ApiTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Vista unica (sin sub-vistas, ver contrato) — un solo controller para todo el modulo. */
@RestController
@RequestMapping("/clientes")
@Tag(name = ApiTags.CLIENTES)
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('clientes', 'ver')")
    @Operation(summary = "Lista los clientes del tenant actual — q busca por nombre o numero de documento")
    public ClientesResponse listar(@RequestParam(required = false) String q) {
        return ClientesResponse.de(clienteService.listar(q));
    }

    /**
     * Version liviana para el modal de seleccion de cliente en el POS —
     * ver Javadoc de ClienteBusqueda: api_03_caja.md (donde se documenta
     * originalmente este endpoint) todavia no esta en el repo, el shape
     * de respuesta es una inferencia a verificar cuando ese contrato
     * exista. Mapeado ANTES que /{id} a proposito de la lectura, aunque
     * Spring ya resuelve la ruta literal "/buscar" por sobre el patron
     * "/{id}" sin ambiguedad real.
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasPermission('clientes', 'ver')")
    @Operation(summary = "Busqueda liviana de clientes para el modal de venta del POS (INFERIDO, ver Javadoc)")
    public ClientesBusquedaResponse buscar(@RequestParam(required = false) String q) {
        return ClientesBusquedaResponse.de(clienteService.buscarLiviano(q));
    }

    @PostMapping
    @PreAuthorize("hasPermission('clientes', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un cliente — mismo endpoint para el formulario completo y el registro rapido del POS")
    public ClienteCreadoResponse crear(@Valid @RequestBody ClienteCrearRequest request) {
        Cliente cliente = clienteService.crear(request.tipoDocumento(), request.numeroDocumento(), request.nombre(),
                request.telefono(), request.correo(), request.direccion());
        return ClienteCreadoResponse.de(cliente);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('clientes', 'ver')")
    @Operation(summary = "Detalle completo de un cliente, con el numero de documento sin enmascarar")
    public ClienteDetalleResponse obtener(@PathVariable Integer id) {
        return ClienteDetalleResponse.de(clienteService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission('clientes', 'editar')")
    @Operation(summary = "Actualiza un cliente — no permite cambiar el documento si ya tiene ventas registradas")
    public ClienteCreadoResponse actualizar(@PathVariable Integer id, @Valid @RequestBody ClienteActualizarRequest request) {
        Cliente cliente = clienteService.actualizar(id, request.nombre(), request.tipoDocumento(),
                request.numeroDocumento(), request.telefono(), request.correo(), request.direccion());
        return ClienteCreadoResponse.de(cliente);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('clientes', 'eliminar')")
    @Operation(summary = "Elimina un cliente sin ventas ni saldo a favor pendiente")
    public ClienteEliminadoResponse eliminar(@PathVariable Integer id) {
        clienteService.eliminar(id);
        return ClienteEliminadoResponse.ELIMINADO;
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize("hasPermission('clientes', 'ver')")
    @Operation(summary = "Historial de compras del cliente")
    public HistorialComprasResponse historial(@PathVariable Integer id) {
        return HistorialComprasResponse.de(clienteService.historialDe(id));
    }

    @GetMapping("/{id}/saldo-movimientos")
    @PreAuthorize("hasPermission('clientes', 'ver')")
    @Operation(summary = "Historial de movimientos del saldo a favor del cliente")
    public SaldoMovimientosResponse saldoMovimientos(@PathVariable Integer id) {
        return SaldoMovimientosResponse.de(clienteService.saldoMovimientosDe(id));
    }
}
