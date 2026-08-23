package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.CategoriaInsumo;
import com.cafepos.core.inventario.domain.CategoriaInsumoRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Catalogo chico — solo GET/POST, ver CategoriaInsumoController. */
@Service
public class CategoriaInsumoService {

    private final CategoriaInsumoRepository categoriaInsumoRepository;

    public CategoriaInsumoService(CategoriaInsumoRepository categoriaInsumoRepository) {
        this.categoriaInsumoRepository = categoriaInsumoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaInsumo> listar() {
        return categoriaInsumoRepository.listar();
    }

    @Transactional
    public CategoriaInsumo crear(String nombre) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        return categoriaInsumoRepository.guardar(new CategoriaInsumo(tenantId, nombre));
    }
}
