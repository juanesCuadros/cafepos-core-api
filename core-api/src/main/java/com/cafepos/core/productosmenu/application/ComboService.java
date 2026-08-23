package com.cafepos.core.productosmenu.application;

import com.cafepos.core.productosmenu.domain.Combo;
import com.cafepos.core.productosmenu.domain.ComboGrupo;
import com.cafepos.core.productosmenu.domain.ComboGrupoCrear;
import com.cafepos.core.productosmenu.domain.ComboGrupoDetalle;
import com.cafepos.core.productosmenu.domain.ComboGrupoNoEncontradoException;
import com.cafepos.core.productosmenu.domain.ComboGrupoParaPedido;
import com.cafepos.core.productosmenu.domain.ComboGrupoProducto;
import com.cafepos.core.productosmenu.domain.ComboGrupoProductoNoEncontradoException;
import com.cafepos.core.productosmenu.domain.ComboNoEncontradoException;
import com.cafepos.core.productosmenu.domain.ComboParaPedido;
import com.cafepos.core.productosmenu.domain.ComboRepository;
import com.cafepos.core.productosmenu.domain.ComboResumen;
import com.cafepos.core.productosmenu.domain.ProductoNoEncontradoException;
import com.cafepos.core.productosmenu.domain.ProductoRef;
import com.cafepos.core.productosmenu.domain.ProductoRepository;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.operacion
 * agregue combos a un pedido (ver buscarParaPedido) — solo cruzan
 * ComboParaPedido/ComboGrupoParaPedido (tambien anotados), nunca las
 * entidades Combo/ComboGrupo completas.
 */
@org.springframework.modulith.NamedInterface("comboService")
@Service
public class ComboService {

    private static final String PREFIJO_CODIGO = "COMBO";

    private final ComboRepository comboRepository;
    private final ProductoRepository productoRepository;

    public ComboService(ComboRepository comboRepository, ProductoRepository productoRepository) {
        this.comboRepository = comboRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ComboResumen> listar() {
        return comboRepository.listar();
    }

    @Transactional(readOnly = true)
    public Combo buscarPorId(Integer id) {
        return comboRepository.buscarPorId(id).orElseThrow(ComboNoEncontradoException::new);
    }

    @Transactional(readOnly = true)
    public List<ComboGrupoDetalle> gruposDe(Integer comboId) {
        return comboRepository.gruposDe(comboId);
    }

    /** API publica de este modulo para agregar combos a un pedido (com.cafepos.core.operacion). */
    @Transactional(readOnly = true)
    public Optional<ComboParaPedido> buscarParaPedido(Integer comboId) {
        return comboRepository.buscarPorId(comboId).map(combo -> {
            List<ComboGrupoParaPedido> grupos = comboRepository.gruposDe(comboId).stream()
                    .map(g -> new ComboGrupoParaPedido(g.id(), g.nombre(),
                            g.productos().stream().map(ProductoRef::id).toList()))
                    .toList();
            return new ComboParaPedido(combo.getId(), combo.getNombre(), combo.getPrecio(), combo.getEstado(),
                    grupos);
        });
    }

    /**
     * Crea el combo con sus grupos y productos en una sola transaccion
     * (todo o nada). Valida TODOS los productos_ids antes de crear nada
     * (mismo criterio "fail fast" que PromocionService.crear) — un
     * producto_id invalido en cualquier grupo aborta la creacion completa.
     */
    @Transactional
    public Combo crear(String nombre, String descripcion, String imagen, BigDecimal precio, String estado,
                        List<ComboGrupoCrear> grupos) {
        if (grupos != null) {
            for (ComboGrupoCrear grupoCrear : grupos) {
                if (grupoCrear.productosIds() != null) {
                    grupoCrear.productosIds().forEach(this::validarProducto);
                }
            }
        }

        Integer tenantId = TenantContext.getCurrentTenantId();
        Combo combo = new Combo(tenantId, nombre, descripcion, imagen, precio, estado);
        combo = comboRepository.guardar(combo);
        combo.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO, combo.getId()));
        combo = comboRepository.guardar(combo);

        if (grupos != null) {
            for (ComboGrupoCrear grupoCrear : grupos) {
                ComboGrupo grupo = comboRepository.guardarGrupo(
                        new ComboGrupo(tenantId, combo.getId(), grupoCrear.nombre()));
                if (grupoCrear.productosIds() != null) {
                    for (Integer productoId : grupoCrear.productosIds()) {
                        comboRepository.agregarProducto(new ComboGrupoProducto(tenantId, grupo.getId(), productoId));
                    }
                }
            }
        }
        return combo;
    }

    @Transactional
    public Combo actualizar(Integer id, String nombre, JsonNullable<String> descripcion, JsonNullable<String> imagen,
                             BigDecimal precio, String estado) {
        Combo combo = comboRepository.buscarPorId(id).orElseThrow(ComboNoEncontradoException::new);
        combo.actualizar(nombre, descripcion, imagen, precio, estado);
        return comboRepository.guardar(combo);
    }

    /** DELETE fisico simple — combos no tiene la regla de "ventas asociadas" que si tiene Producto. */
    @Transactional
    public void eliminar(Integer id) {
        Combo combo = comboRepository.buscarPorId(id).orElseThrow(ComboNoEncontradoException::new);
        comboRepository.eliminar(combo);
    }

    @Transactional
    public void crearGrupo(Integer comboId, String nombre) {
        Combo combo = comboRepository.buscarPorId(comboId).orElseThrow(ComboNoEncontradoException::new);
        comboRepository.guardarGrupo(new ComboGrupo(combo.getTenantId(), combo.getId(), nombre));
    }

    @Transactional
    public void renombrarGrupo(Integer comboId, Integer grupoId, String nombre) {
        ComboGrupo grupo = buscarGrupo(comboId, grupoId);
        grupo.renombrar(nombre);
        comboRepository.guardarGrupo(grupo);
    }

    @Transactional
    public void eliminarGrupo(Integer comboId, Integer grupoId) {
        comboRepository.eliminarGrupo(buscarGrupo(comboId, grupoId));
    }

    @Transactional
    public void agregarProducto(Integer comboId, Integer grupoId, Integer productoId) {
        ComboGrupo grupo = buscarGrupo(comboId, grupoId);
        validarProducto(productoId);
        comboRepository.agregarProducto(new ComboGrupoProducto(grupo.getTenantId(), grupo.getId(), productoId));
    }

    @Transactional
    public void quitarProducto(Integer comboId, Integer grupoId, Integer productoId) {
        ComboGrupo grupo = buscarGrupo(comboId, grupoId);
        ComboGrupoProducto asociacion = comboRepository.buscarAsociacion(grupo.getId(), productoId)
                .orElseThrow(ComboGrupoProductoNoEncontradoException::new);
        comboRepository.quitarProducto(asociacion);
    }

    /** Busca por (comboId, grupoId) a la vez — cubre "combo no existe", "grupo no existe" y "grupo de otro combo" con un 404 unico. */
    private ComboGrupo buscarGrupo(Integer comboId, Integer grupoId) {
        return comboRepository.buscarGrupo(comboId, grupoId).orElseThrow(ComboGrupoNoEncontradoException::new);
    }

    private void validarProducto(Integer productoId) {
        productoRepository.buscarPorId(productoId).orElseThrow(ProductoNoEncontradoException::new);
    }
}
