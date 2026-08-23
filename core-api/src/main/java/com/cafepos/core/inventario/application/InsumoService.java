package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.CategoriaInsumoNoEncontradaException;
import com.cafepos.core.inventario.domain.CategoriaInsumoRepository;
import com.cafepos.core.inventario.domain.Insumo;
import com.cafepos.core.inventario.domain.InsumoNoEncontradoException;
import com.cafepos.core.inventario.domain.InsumoRepository;
import com.cafepos.core.inventario.domain.InsumoResumen;
import com.cafepos.core.inventario.domain.ResultadoEliminacionInsumo;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
}
