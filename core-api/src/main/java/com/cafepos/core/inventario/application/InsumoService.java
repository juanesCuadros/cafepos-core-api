package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.CategoriaInsumoNoEncontradaException;
import com.cafepos.core.inventario.domain.CategoriaInsumoRepository;
import com.cafepos.core.inventario.domain.Insumo;
import com.cafepos.core.inventario.domain.InsumoNoEncontradoException;
import com.cafepos.core.inventario.domain.InsumoRef;
import com.cafepos.core.inventario.domain.InsumoRepository;
import com.cafepos.core.inventario.domain.InsumoResumen;
import com.cafepos.core.inventario.domain.ResultadoEliminacionInsumo;
import com.cafepos.core.inventario.domain.ReversionInsumoResultado;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.compras
 * registre entradas/reversiones de stock y costo al registrar/anular una
 * compra (ver registrarEntradaPorCompra/revertirPorAnulacionCompra/
 * buscarRefPorId) — solo cruzan InsumoRef/ReversionInsumoResultado
 * (tambien anotados), nunca la entidad Insumo completa.
 */
@org.springframework.modulith.NamedInterface("insumoService")
@Service
public class InsumoService {

    private static final String PREFIJO_CODIGO = "INS";

    private final InsumoRepository insumoRepository;
    private final CategoriaInsumoRepository categoriaInsumoRepository;

    public InsumoService(InsumoRepository insumoRepository, CategoriaInsumoRepository categoriaInsumoRepository) {
        this.insumoRepository = insumoRepository;
        this.categoriaInsumoRepository = categoriaInsumoRepository;
    }

    @Transactional(readOnly = true)
    public List<InsumoResumen> listar(Integer categoriaInsumoId, String estado, String estadoStock, String q) {
        return insumoRepository.listar(categoriaInsumoId, estado, estadoStock, q);
    }

    @Transactional(readOnly = true)
    public Insumo buscarPorId(Integer id) {
        return insumoRepository.buscarPorId(id).orElseThrow(InsumoNoEncontradoException::new);
    }

    @Transactional
    public Insumo crear(String nombre, Integer categoriaInsumoId, String unidadMedida, BigDecimal stockMinimo,
                         BigDecimal stockMaximo, LocalDate fechaVencimRef, String estado) {
        validarCategoriaInsumo(categoriaInsumoId);
        Integer tenantId = TenantContext.getCurrentTenantId();
        Insumo insumo = new Insumo(tenantId, categoriaInsumoId, nombre, unidadMedida, stockMinimo, stockMaximo,
                fechaVencimRef, estado);
        insumo = insumoRepository.guardar(insumo);
        insumo.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO, insumo.getId()));
        return insumoRepository.guardar(insumo);
    }

    @Transactional
    public Insumo actualizar(Integer id, String nombre, Integer categoriaInsumoId, String unidadMedida,
                              BigDecimal stockMinimo, JsonNullable<BigDecimal> stockMaximo,
                              JsonNullable<LocalDate> fechaVencimRef, String estado) {
        if (categoriaInsumoId != null) {
            validarCategoriaInsumo(categoriaInsumoId);
        }
        Insumo insumo = buscarPorId(id);
        insumo.actualizar(nombre, categoriaInsumoId, unidadMedida, stockMinimo, stockMaximo, fechaVencimRef, estado);
        return insumoRepository.guardar(insumo);
    }

    /** Mismo patron que ProductoService.eliminar: soft-delete si tiene movimientos asociados, sino borrado fisico. */
    @Transactional
    public ResultadoEliminacionInsumo eliminar(Integer id) {
        Insumo insumo = buscarPorId(id);
        if (insumoRepository.tieneMovimientosAsociados(id)) {
            insumo.marcarInactivo();
            insumoRepository.guardar(insumo);
            return ResultadoEliminacionInsumo.MARCADO_INACTIVO;
        }
        insumoRepository.eliminar(insumo);
        return ResultadoEliminacionInsumo.ELIMINADO_FISICO;
    }

    private void validarCategoriaInsumo(Integer categoriaInsumoId) {
        categoriaInsumoRepository.buscarPorId(categoriaInsumoId).orElseThrow(CategoriaInsumoNoEncontradaException::new);
    }

    /** API publica de este modulo para validar insumo_id en el detalle de una compra (com.cafepos.core.compras). */
    @Transactional(readOnly = true)
    public Optional<InsumoRef> buscarRefPorId(Integer id) {
        return insumoRepository.buscarPorId(id)
                .map(i -> new InsumoRef(i.getId(), i.getCodigo(), i.getNombre(), i.getUnidadMedida()));
    }

    /**
     * API publica de este modulo para com.cafepos.core.compras: suma stock y
     * sobreescribe costo_actual con el costo_unitario de la linea de compra
     * (nunca promedio ponderado, ver DECISIONES YA TOMADAS de la conversacion
     * Compras). Asume insumoId ya validado por el caller (buscarRefPorId).
     */
    @Transactional
    public void registrarEntradaPorCompra(Integer insumoId, BigDecimal cantidad, BigDecimal costoUnitario) {
        Insumo insumo = buscarPorId(insumoId);
        insumo.actualizarStock(insumo.getStockActual().add(cantidad));
        insumo.actualizarCostoActual(costoUnitario);
        insumoRepository.guardar(insumo);
    }

    /**
     * API publica de este modulo para com.cafepos.core.compras: revierte
     * stock y costo al anular una compra. exitoso=false (nada se muta) si la
     * cantidad a revertir dejaria stock_actual negativo — el caller decide
     * el 400 con su propio tipo de excepcion (ver ReversionInsumoResultado).
     * costoRevertido ya viene resuelto por el caller (ultimo costo_unitario
     * real de otra compra no anulada de ese insumo, o 0 si no existe
     * ninguna otra — ver CompraService.resolverCostoReversion).
     */
    @Transactional
    public ReversionInsumoResultado revertirPorAnulacionCompra(Integer insumoId, BigDecimal cantidad,
                                                                 BigDecimal costoRevertido) {
        Insumo insumo = buscarPorId(insumoId);
        BigDecimal stockNuevo = insumo.getStockActual().subtract(cantidad);
        if (stockNuevo.compareTo(BigDecimal.ZERO) < 0) {
            return new ReversionInsumoResultado(false, insumo.getStockActual());
        }
        insumo.actualizarStock(stockNuevo);
        insumo.actualizarCostoActual(costoRevertido);
        insumoRepository.guardar(insumo);
        return new ReversionInsumoResultado(true, stockNuevo);
    }
}
