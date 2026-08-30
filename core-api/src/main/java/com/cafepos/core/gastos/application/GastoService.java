package com.cafepos.core.gastos.application;

import com.cafepos.core.gastos.domain.CategoriaGasto;
import com.cafepos.core.gastos.domain.CategoriaGastoInactivaException;
import com.cafepos.core.gastos.domain.CategoriaGastoNoEncontradaException;
import com.cafepos.core.gastos.domain.CategoriaGastoRepository;
import com.cafepos.core.gastos.domain.Gasto;
import com.cafepos.core.gastos.domain.GastoNoEncontradoException;
import com.cafepos.core.gastos.domain.GastoRepository;
import com.cafepos.core.gastos.domain.GastoResumen;
import com.cafepos.core.shared.auditoria.Auditable;
import com.cafepos.core.shared.auditoria.AuditoriaContext;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.seguridad.UsuarioRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class GastoService {

    private static final String PREFIJO_CODIGO = "GAS";

    private final GastoRepository gastoRepository;
    private final CategoriaGastoRepository categoriaGastoRepository;
    private final UsuarioRepository usuarioRepository;

    public GastoService(GastoRepository gastoRepository, CategoriaGastoRepository categoriaGastoRepository,
                         UsuarioRepository usuarioRepository) {
        this.gastoRepository = gastoRepository;
        this.categoriaGastoRepository = categoriaGastoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<GastoResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer categoriaGastoId,
                                      String metodoPago) {
        return gastoRepository.listar(fechaInicio, fechaFin, categoriaGastoId, metodoPago);
    }

    @Transactional(readOnly = true)
    public GastoVista detalle(Integer id) {
        Gasto gasto = buscarPorId(id);
        String categoriaNombre = categoriaGastoRepository.buscarPorId(gasto.getCategoriaGastoId())
                .map(CategoriaGasto::getNombre).orElse(null);
        String usuarioNombre = usuarioRepository.findById(gasto.getUsuarioId()).map(Usuario::getNombre).orElse(null);
        return new GastoVista(gasto, categoriaNombre, usuarioNombre);
    }

    /** categoria_gasto_id debe existir (404) y estar activa (400) — dos validaciones distintas, ver DECISIONES YA TOMADAS. */
    @Transactional
    public Gasto crear(Integer categoriaGastoId, String descripcion, BigDecimal monto, String metodoPago,
                        LocalDate fecha, String comprobanteImagen, String observaciones, Integer usuarioId) {
        validarCategoriaActiva(categoriaGastoId);
        Integer tenantId = TenantContext.getCurrentTenantId();
        Gasto gasto = new Gasto(tenantId, categoriaGastoId, usuarioId, descripcion, monto, metodoPago, fecha,
                comprobanteImagen, observaciones);
        gasto = gastoRepository.guardar(gasto);
        gasto.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO, gasto.getId()));
        return gastoRepository.guardar(gasto);
    }

    @Transactional
    public Gasto actualizar(Integer id, Integer categoriaGastoId, String descripcion, BigDecimal monto,
                             String metodoPago, LocalDate fecha, JsonNullable<String> comprobanteImagen,
                             JsonNullable<String> observaciones) {
        Gasto gasto = buscarPorId(id);
        if (categoriaGastoId != null) {
            validarCategoriaActiva(categoriaGastoId);
        }
        gasto.actualizar(categoriaGastoId, descripcion, monto, metodoPago, fecha, comprobanteImagen, observaciones);
        return gastoRepository.guardar(gasto);
    }

    @Transactional
    @Auditable(entidadTipo = "gasto", accion = "eliminar", entidadIdExpression = "#id")
    public void eliminar(Integer id) {
        Gasto gasto = buscarPorId(id);
        AuditoriaContext.registrarAntes(gasto);
        gastoRepository.eliminar(gasto);
    }

    private void validarCategoriaActiva(Integer categoriaGastoId) {
        CategoriaGasto categoria = categoriaGastoRepository.buscarPorId(categoriaGastoId)
                .orElseThrow(CategoriaGastoNoEncontradaException::new);
        if (!categoria.estaActiva()) {
            throw new CategoriaGastoInactivaException();
        }
    }

    private Gasto buscarPorId(Integer id) {
        return gastoRepository.buscarPorId(id).orElseThrow(GastoNoEncontradoException::new);
    }
}
