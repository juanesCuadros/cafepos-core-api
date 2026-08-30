package com.cafepos.core.compras.application;

import com.cafepos.core.compras.domain.Compra;
import com.cafepos.core.compras.domain.CompraAnuladaBloqueadaException;
import com.cafepos.core.compras.domain.CompraDetalle;
import com.cafepos.core.compras.domain.CompraDetalleItemVista;
import com.cafepos.core.compras.domain.CompraDetalleRepository;
import com.cafepos.core.compras.domain.CompraListadoItem;
import com.cafepos.core.compras.domain.CompraNoEncontradaException;
import com.cafepos.core.compras.domain.CompraNoMarcablePagadaException;
import com.cafepos.core.compras.domain.CompraRepository;
import com.cafepos.core.compras.domain.InsumoInvalidoException;
import com.cafepos.core.compras.domain.Proveedor;
import com.cafepos.core.compras.domain.ProveedorNoEncontradoException;
import com.cafepos.core.compras.domain.ProveedorRepository;
import com.cafepos.core.compras.domain.StockInsuficienteParaAnularException;
import com.cafepos.core.inventario.application.InsumoService;
import com.cafepos.core.inventario.application.LoteInsumoService;
import com.cafepos.core.inventario.application.MovimientoInventarioService;
import com.cafepos.core.inventario.domain.ReversionInsumoResultado;
import com.cafepos.core.shared.auditoria.Auditable;
import com.cafepos.core.shared.auditoria.AuditoriaContext;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.seguridad.UsuarioRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Llama directamente (sincrono, misma transaccion) a
 * com.cafepos.core.inventario.application.{InsumoService,LoteInsumoService,
 * MovimientoInventarioService} — todos NamedInterface, ver package-info.java
 * de este modulo.
 */
@Service
public class CompraService {

    private static final String PREFIJO_CODIGO_COMPRA = "COMP";
    private static final String REFERENCIA_TIPO_COMPRA = "compra";
    private static final String REFERENCIA_TIPO_COMPRA_ANULADA = "compra_anulada";

    private static final Logger log = LoggerFactory.getLogger(CompraService.class);

    private final CompraRepository compraRepository;
    private final CompraDetalleRepository compraDetalleRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final InsumoService insumoService;
    private final LoteInsumoService loteInsumoService;
    private final MovimientoInventarioService movimientoInventarioService;

    public CompraService(CompraRepository compraRepository, CompraDetalleRepository compraDetalleRepository,
                          ProveedorRepository proveedorRepository, UsuarioRepository usuarioRepository,
                          InsumoService insumoService, LoteInsumoService loteInsumoService,
                          MovimientoInventarioService movimientoInventarioService) {
        this.compraRepository = compraRepository;
        this.compraDetalleRepository = compraDetalleRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.insumoService = insumoService;
        this.loteInsumoService = loteInsumoService;
        this.movimientoInventarioService = movimientoInventarioService;
    }

    @Transactional(readOnly = true)
    public List<CompraListadoItem> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer proveedorId,
                                           String formaPago, String estado) {
        return compraRepository.listar(fechaInicio, fechaFin, proveedorId, formaPago, estado);
    }

    @Transactional(readOnly = true)
    public CompraVista detalle(Integer id) {
        Compra compra = buscarPorId(id);
        String proveedorNombre = proveedorRepository.buscarPorId(compra.getProveedorId())
                .map(Proveedor::getNombre).orElse(null);
        String usuarioNombre = usuarioRepository.findById(compra.getUsuarioId())
                .map(Usuario::getNombre).orElse(null);
        List<CompraDetalleItemVista> items = compraDetalleRepository.listarVistaPorCompraId(id);
        return new CompraVista(compra, proveedorNombre, usuarioNombre, items);
    }

    /**
     * proveedor_id debe existir (404). Cada insumo_id del detalle debe
     * existir (400 — viaja anidado en el body, no es un recurso de la URL,
     * ver DECISIONES YA TOMADAS). total = suma de subtotales
     * (cantidad*costo_unitario por linea). estado se deriva de forma_pago
     * (ver Compra). Por cada linea: suma stock + sobreescribe costo_actual,
     * crea el lote, y registra el movimiento de entrada.
     */
    @Transactional
    public Compra registrar(Integer proveedorId, String numeroFacturaProv, LocalDate fecha, String formaPago,
                             String observaciones, List<DetalleCompraInput> detalles, Integer usuarioId) {
        proveedorRepository.buscarPorId(proveedorId).orElseThrow(ProveedorNoEncontradoException::new);
        for (DetalleCompraInput d : detalles) {
            if (insumoService.buscarRefPorId(d.insumoId()).isEmpty()) {
                throw new InsumoInvalidoException();
            }
        }

        BigDecimal total = detalles.stream()
                .map(d -> d.cantidad().multiply(d.costoUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        Integer tenantId = TenantContext.getCurrentTenantId();
        Compra compra = new Compra(tenantId, proveedorId, usuarioId, numeroFacturaProv, fecha, formaPago,
                observaciones, total);
        compra = compraRepository.guardar(compra);
        compra.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO_COMPRA, compra.getId()));
        compra = compraRepository.guardar(compra);

        for (DetalleCompraInput d : detalles) {
            BigDecimal subtotal = d.cantidad().multiply(d.costoUnitario()).setScale(2, RoundingMode.HALF_UP);
            CompraDetalle detalle = new CompraDetalle(tenantId, compra.getId(), d.insumoId(), d.cantidad(),
                    d.costoUnitario(), d.numeroLote(), d.fechaVencimiento(), subtotal);
            detalle = compraDetalleRepository.guardar(detalle);

            insumoService.registrarEntradaPorCompra(d.insumoId(), d.cantidad(), d.costoUnitario());
            loteInsumoService.crear(d.insumoId(), detalle.getId(), d.numeroLote(), d.fechaVencimiento(),
                    d.cantidad());
            movimientoInventarioService.registrarEntrada(d.insumoId(), usuarioId, d.cantidad(),
                    REFERENCIA_TIPO_COMPRA, compra.getId());
        }

        return compra;
    }

    /**
     * PIN ya validado por el controller ANTES de llamar aca (ver
     * CompraController). 403 de negocio SOLO si forma_pago=credito Y
     * estado=pagada (Compra.bloqueadaParaAnular — nunca una condicion
     * generica de "si esta pagada"). Por cada linea: revierte stock (400 si
     * dejaria negativo) y costo_actual, agota el lote, y registra el
     * movimiento de salida.
     */
    @Transactional
    @Auditable(entidadTipo = "compra", accion = "anular", entidadIdExpression = "#id")
    public AnularCompraResultado anular(Integer id, Integer usuarioId, String motivo) {
        Compra compra = buscarPorId(id);
        AuditoriaContext.registrarAntes(compra);
        if (compra.bloqueadaParaAnular()) {
            throw new CompraAnuladaBloqueadaException();
        }

        List<CompraDetalle> detalles = compraDetalleRepository.listarPorCompraId(id);
        int movimientosGenerados = 0;
        for (CompraDetalle d : detalles) {
            BigDecimal costoRevertido = resolverCostoReversion(d.getInsumoId(), id);
            ReversionInsumoResultado resultado = insumoService.revertirPorAnulacionCompra(d.getInsumoId(),
                    d.getCantidad(), costoRevertido);
            if (!resultado.exitoso()) {
                throw new StockInsuficienteParaAnularException();
            }
            loteInsumoService.agotarPorCompraDetalleId(d.getId());
            movimientoInventarioService.registrarSalida(d.getInsumoId(), usuarioId, d.getCantidad(),
                    "Reversion por anulacion de compra " + compra.getCodigo(), REFERENCIA_TIPO_COMPRA_ANULADA,
                    compra.getId());
            movimientosGenerados++;
        }

        compra.anular(motivo);
        compra = compraRepository.guardar(compra);

        return new AnularCompraResultado(compra.getId(), compra.getEstado(), movimientosGenerados);
    }

    @Transactional
    public Compra marcarPagada(Integer id) {
        Compra compra = buscarPorId(id);
        if (!Compra.FORMA_PAGO_CREDITO.equals(compra.getFormaPago())
                || !Compra.ESTADO_PENDIENTE.equals(compra.getEstado())) {
            throw new CompraNoMarcablePagadaException();
        }
        compra.marcarPagada();
        return compraRepository.guardar(compra);
    }

    private Compra buscarPorId(Integer id) {
        return compraRepository.buscarPorId(id).orElseThrow(CompraNoEncontradaException::new);
    }

    /**
     * No es una reversion matematicamente exacta — es la mejor aproximacion
     * disponible sin un historial de costos real (ver DECISIONES YA
     * TOMADAS). Si no existe ninguna otra compra_detalle de ese insumo en
     * una compra no anulada, costo_actual queda en 0 con este WARN.
     */
    private BigDecimal resolverCostoReversion(Integer insumoId, Integer compraIdExcluir) {
        Optional<BigDecimal> costo = compraDetalleRepository.buscarCostoUnitarioMasRecientePorInsumo(insumoId,
                compraIdExcluir);
        if (costo.isPresent()) {
            return costo.get();
        }
        log.warn("Anulando compra {} - insumo {} sin otra compra no anulada previa, costo_actual revertido a 0 "
                + "(aproximacion, no es una reversion exacta)", compraIdExcluir, insumoId);
        return BigDecimal.ZERO;
    }
}
