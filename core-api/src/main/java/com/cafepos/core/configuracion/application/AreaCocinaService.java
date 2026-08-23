package com.cafepos.core.configuracion.application;

import com.cafepos.core.configuracion.domain.AreaCocina;
import com.cafepos.core.configuracion.domain.AreaCocinaNoEncontradaException;
import com.cafepos.core.configuracion.domain.AreaCocinaRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AreaCocinaService {

    private final AreaCocinaRepository areaCocinaRepository;

    public AreaCocinaService(AreaCocinaRepository areaCocinaRepository) {
        this.areaCocinaRepository = areaCocinaRepository;
    }

    @Transactional(readOnly = true)
    public List<AreaCocina> listar() {
        return areaCocinaRepository.listar();
    }

    @Transactional(readOnly = true)
    public AreaCocina buscarPorId(Integer id) {
        return areaCocinaRepository.buscarPorId(id).orElseThrow(AreaCocinaNoEncontradaException::new);
    }

    @Transactional
    public AreaCocina crear(String nombre, String estado) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        return areaCocinaRepository.guardar(new AreaCocina(tenantId, nombre, estado));
    }

    @Transactional
    public AreaCocina actualizar(Integer id, String nombre, String estado) {
        AreaCocina areaCocina = buscarPorId(id);
        areaCocina.actualizar(nombre, estado);
        return areaCocinaRepository.guardar(areaCocina);
    }

    @Transactional
    public void eliminar(Integer id) {
        areaCocinaRepository.eliminar(buscarPorId(id));
    }
}
