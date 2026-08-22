package com.cafepos.core.productosmenu.application;

import com.cafepos.core.productosmenu.domain.Categoria;
import com.cafepos.core.productosmenu.domain.CategoriaConProductosException;
import com.cafepos.core.productosmenu.domain.CategoriaNoEncontradaException;
import com.cafepos.core.productosmenu.domain.CategoriaRepository;
import com.cafepos.core.productosmenu.domain.CategoriaResumen;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResumen> listar() {
        return categoriaRepository.listarConNumProductos();
    }

    @Transactional
    public Categoria crear(String icono, String nombre, String descripcion, Integer orden, String estado) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Categoria categoria = new Categoria(tenantId, icono, nombre, descripcion, orden, estado);
        return categoriaRepository.guardar(categoria);
    }

    @Transactional
    public Categoria actualizar(Integer id, String icono, String nombre, JsonNullable<String> descripcion,
                                 Integer orden, String estado) {
        Categoria categoria = categoriaRepository.buscarPorId(id)
                .orElseThrow(CategoriaNoEncontradaException::new);
        categoria.actualizar(icono, nombre, descripcion, orden, estado);
        return categoriaRepository.guardar(categoria);
    }

    /**
     * Cuenta productos ANTES de borrar (RN del contrato): si hay al menos uno,
     * se rechaza con el conteo real en vez de intentar el DELETE y dejar que
     * la FK de producto.categoria_id lo rechace con un error crudo de Postgres.
     */
    @Transactional
    public void eliminar(Integer id) {
        Categoria categoria = categoriaRepository.buscarPorId(id)
                .orElseThrow(CategoriaNoEncontradaException::new);
        long numProductos = categoriaRepository.contarProductos(id);
        if (numProductos > 0) {
            throw new CategoriaConProductosException(numProductos);
        }
        categoriaRepository.eliminar(categoria);
    }
}
