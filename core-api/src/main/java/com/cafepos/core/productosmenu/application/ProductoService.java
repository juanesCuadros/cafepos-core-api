package com.cafepos.core.productosmenu.application;

import com.cafepos.core.productosmenu.domain.AreaCocinaNoEncontradaException;
import com.cafepos.core.productosmenu.domain.CategoriaNoEncontradaException;
import com.cafepos.core.productosmenu.domain.CategoriaRepository;
import com.cafepos.core.productosmenu.domain.Producto;
import com.cafepos.core.productosmenu.domain.ProductoNoEncontradoException;
import com.cafepos.core.productosmenu.domain.ProductoParaPedido;
import com.cafepos.core.productosmenu.domain.ProductoPublico;
import com.cafepos.core.productosmenu.domain.ProductoRepository;
import com.cafepos.core.productosmenu.domain.ProductoResumen;
import com.cafepos.core.productosmenu.domain.ResultadoEliminacionProducto;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @NamedInterface: expuesto puntualmente para "llamada directa síncrona"
 * desde otros módulos (ver restaurante.MenuPublicoService) — el resto de
 * application (CategoriaService, PromocionService, ComboService) y todo
 * domain/infrastructure de este módulo siguen cerrados.
 */
@org.springframework.modulith.NamedInterface("productoService")
@Service
public class ProductoService {

    private static final String PREFIJO_CODIGO = "PROD";

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResumen> listar(Integer categoriaId, String estado, String q) {
        return productoRepository.listar(categoriaId, estado, q);
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Integer id) {
        return productoRepository.buscarPorId(id).orElseThrow(ProductoNoEncontradoException::new);
    }

    /** API publica de este modulo para el menu publico de restaurante (com.cafepos.core.restaurante). */
    @Transactional(readOnly = true)
    public List<ProductoPublico> listarVisiblesParaMenuPublico() {
        return productoRepository.listarVisiblesParaMenuPublico();
    }

    /** API publica de este modulo para agregar productos a un pedido (com.cafepos.core.operacion). */
    @Transactional(readOnly = true)
    public Optional<ProductoParaPedido> buscarParaPedido(Integer id) {
        return productoRepository.buscarPorId(id).map(p -> new ProductoParaPedido(
                p.getId(), p.getNombre(), p.getPrecioVenta(), p.getEstado(), p.getAreaCocinaId()));
    }

    @Transactional
    public Producto crear(Integer categoriaId, Integer areaCocinaId, String nombre, String descripcion,
                           String imagen, BigDecimal precioVenta, BigDecimal costoEstimado, String tasaImpuesto,
                           Boolean manejaReceta, Boolean manejaInventario, String unidadMedida,
                           BigDecimal stockMinimo, String estado, String visibilidad) {
        validarCategoria(categoriaId);
        validarAreaCocina(areaCocinaId);

        Integer tenantId = TenantContext.getCurrentTenantId();
        Producto producto = new Producto(tenantId, categoriaId, areaCocinaId, nombre, descripcion, imagen,
                precioVenta, costoEstimado, tasaImpuesto, manejaReceta, manejaInventario, unidadMedida,
                stockMinimo, estado, visibilidad);
        producto = productoRepository.guardar(producto);

        producto.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO, producto.getId()));
        return productoRepository.guardar(producto);
    }

    @Transactional
    public Producto actualizar(Integer id, Integer categoriaId, JsonNullable<Integer> areaCocinaId, String nombre,
                                JsonNullable<String> descripcion, JsonNullable<String> imagen, BigDecimal precioVenta,
                                JsonNullable<BigDecimal> costoEstimado, JsonNullable<String> tasaImpuesto,
                                Boolean manejaReceta, Boolean manejaInventario, JsonNullable<String> unidadMedida,
                                JsonNullable<BigDecimal> stockMinimo, String estado, String visibilidad) {
        Producto producto = productoRepository.buscarPorId(id).orElseThrow(ProductoNoEncontradoException::new);
        if (categoriaId != null) {
            validarCategoria(categoriaId);
        }
        if (areaCocinaId.isPresent()) {
            validarAreaCocina(areaCocinaId.get());
        }
        producto.actualizar(categoriaId, areaCocinaId, nombre, descripcion, imagen, precioVenta, costoEstimado,
                tasaImpuesto, manejaReceta, manejaInventario, unidadMedida, stockMinimo, estado, visibilidad);
        return productoRepository.guardar(producto);
    }

    /**
     * Nunca se hace DELETE fisico de un producto con historial de ventas
     * (RN del contrato) - si tiene al menos una venta asociada se convierte
     * automaticamente en soft-delete (estado inactivo) en vez de borrarse.
     */
    @Transactional
    public ResultadoEliminacionProducto eliminar(Integer id) {
        Producto producto = productoRepository.buscarPorId(id).orElseThrow(ProductoNoEncontradoException::new);
        if (productoRepository.tieneVentasAsociadas(id)) {
            producto.marcarInactivo();
            productoRepository.guardar(producto);
            return ResultadoEliminacionProducto.MARCADO_INACTIVO;
        }
        productoRepository.eliminar(producto);
        return ResultadoEliminacionProducto.ELIMINADO_FISICO;
    }

    private void validarCategoria(Integer categoriaId) {
        categoriaRepository.buscarPorId(categoriaId).orElseThrow(CategoriaNoEncontradaException::new);
    }

    private void validarAreaCocina(Integer areaCocinaId) {
        if (areaCocinaId != null && !productoRepository.existeAreaCocina(areaCocinaId)) {
            throw new AreaCocinaNoEncontradaException();
        }
    }
}
