package com.cafepos.core.gastos.application;

import com.cafepos.core.gastos.domain.CategoriaGasto;
import com.cafepos.core.gastos.domain.CategoriaGastoRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Catalogo chico — solo GET/POST, ver CategoriaGastoController. */
@Service
public class CategoriaGastoService {

    private final CategoriaGastoRepository categoriaGastoRepository;

    public CategoriaGastoService(CategoriaGastoRepository categoriaGastoRepository) {
        this.categoriaGastoRepository = categoriaGastoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaGasto> listar() {
        return categoriaGastoRepository.listar();
    }

    @Transactional
    public CategoriaGasto crear(String nombre) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        return categoriaGastoRepository.guardar(new CategoriaGasto(tenantId, nombre));
    }
}
