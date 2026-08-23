package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.PedidoService;
import com.cafepos.core.operacion.application.SeleccionCombo;
import com.cafepos.core.shared.openapi.ApiTags;
import com.cafepos.core.shared.seguridad.AuthenticatedUsuario;
import com.cafepos.core.shared.seguridad.PinStepUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
@Tag(name = ApiTags.OPERACION)
public class PedidoController {

    private static final String MODULO_PEDIDO_ABIERTO = "operacion.pedido_abierto";
    private static final String ACCION_ELIMINAR_ITEM = "eliminar_item";
    private static final String RECURSO_TIPO_ITEM = "pedido_item";

    private final PedidoService pedidoService;
    private final PinStepUpService pinStepUpService;

    public PedidoController(PedidoService pedidoService, PinStepUpService pinStepUpService) {
        this.pedidoService = pedidoService;
        this.pinStepUpService = pinStepUpService;
    }

    @PostMapping
    @PreAuthorize("hasPermission('operacion.pedido_abierto', 'crear')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Abre un pedido nuevo al tocar una mesa libre",
            description = "usuario_id se toma del token, nunca del body. 409 si la mesa ya tiene un pedido activo.")
    public PedidoResponse abrir(@Valid @RequestBody PedidoAbrirRequest request, Authentication authentication) {
        AuthenticatedUsuario principal = (AuthenticatedUsuario) authentication.getPrincipal();
        return PedidoResponse.de(pedidoService.abrir(request.mesaId(), request.tipo(), principal.usuarioId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('operacion.pedido_abierto', 'ver')")
    @Operation(summary = "Detalle completo de un pedido, con promociones_sugeridas")
    @ApiResponses({@ApiResponse(responseCode = "404", description = "Pedido no encontrado")})
    public PedidoDetalleResponse obtener(@PathVariable Integer id) {
        return PedidoDetalleResponse.de(pedidoService.obtenerDetalle(id));
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasPermission('operacion.pedido_abierto', 'agregar_producto')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agrega un producto o combo al pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Producto agotado, o seleccion de combo incompleta")
    })
    public ItemAgregadoResponse agregarItem(@PathVariable Integer id, @Valid @RequestBody ItemAgregarRequest request) {
        var selecciones = request.selecciones() == null ? null
                : request.selecciones().stream()
                        .map(s -> new SeleccionCombo(s.comboGrupoId(), s.productoId()))
                        .toList();
        return ItemAgregadoResponse.de(pedidoService.agregarItem(id, request.productoId(), request.comboId(),
                request.cantidad(), request.observacion(), selecciones));
    }

    @PatchMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasPermission('operacion.pedido_abierto', 'editar_item')")
    @Operation(summary = "Edita cantidad u observacion de un item ya agregado")
    public PedidoItemResponse editarItem(@PathVariable Integer id, @PathVariable Integer itemId,
                                          @Valid @RequestBody ItemEditarRequest request) {
        return PedidoItemResponse.de(
                pedidoService.editarItem(id, itemId, request.cantidad(), request.observacion()));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasPermission('operacion.pedido_abierto', 'eliminar_item')")
    @Operation(summary = "Elimina un item del pedido",
            description = "Requiere PIN de step-up (ver PinStepUpService) — header X-Pin-Token con el pin_token "
                    + "emitido por POST /auth/pin/verificar para modulo=operacion.pedido_abierto, "
                    + "accion=eliminar_item, recurso_tipo=pedido_item, recurso_id=el mismo item_id de esta ruta.")
    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Falta el header X-Pin-Token o el pin_token no es "
                    + "valido para este item especifico")
    })
    public ItemEliminadoResponse eliminarItem(@PathVariable Integer id, @PathVariable Integer itemId,
                                               @Parameter(description = "pin_token emitido por POST /auth/pin/verificar")
                                               @RequestHeader(name = "X-Pin-Token", required = false) String pinToken) {
        pinStepUpService.validar(pinToken, MODULO_PEDIDO_ABIERTO, ACCION_ELIMINAR_ITEM, RECURSO_TIPO_ITEM, itemId);
        return ItemEliminadoResponse.de(pedidoService.eliminarItem(id, itemId));
    }

    @PostMapping("/{id}/enviar-comanda")
    @PreAuthorize("hasPermission('operacion.pedido_abierto', 'enviar_comanda')")
    @Operation(summary = "Envia la comanda a cocina/impresora segun configuracion_sistema.modo_comanda")
    public EnviarComandaResponse enviarComanda(@PathVariable Integer id) {
        return EnviarComandaResponse.de(pedidoService.enviarComanda(id));
    }

    @PostMapping("/{id}/mover-mesa")
    @PreAuthorize("hasPermission('operacion.pedido_abierto', 'mover_mesa')")
    @Operation(summary = "Traslada un pedido a otra mesa")
    @ApiResponses({@ApiResponse(responseCode = "409", description = "La mesa destino no esta libre")})
    public MoverMesaResponse moverMesa(@PathVariable Integer id, @Valid @RequestBody MoverMesaRequest request) {
        return MoverMesaResponse.de(pedidoService.moverMesa(id, request.mesaDestinoId()));
    }

    @PostMapping("/{id}/marcar-lista-cobrar")
    @PreAuthorize("hasPermission('operacion.pedido_abierto', 'marcar_lista_cobrar')")
    @Operation(summary = "Marca la mesa del pedido como lista para cobrar")
    public MarcarListaCobrarResponse marcarListaCobrar(@PathVariable Integer id) {
        return MarcarListaCobrarResponse.de(pedidoService.marcarListaCobrar(id));
    }

    @GetMapping("/{id}/prefactura")
    @PreAuthorize("hasPermission('operacion.pedido_abierto', 'prefactura')")
    @Operation(summary = "Genera el resumen para imprimir prefactura",
            description = "Tambien ejecuta marcar-lista-cobrar internamente.")
    public PrefacturaResponse prefactura(@PathVariable Integer id) {
        return PrefacturaResponse.de(pedidoService.prefactura(id));
    }
}
