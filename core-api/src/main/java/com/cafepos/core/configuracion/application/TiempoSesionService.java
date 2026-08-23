package com.cafepos.core.configuracion.application;

import com.cafepos.core.configuracion.domain.RolNoEncontradoException;
import com.cafepos.core.configuracion.domain.RolTiempoSesion;
import com.cafepos.core.configuracion.domain.TiempoSesionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TiempoSesionService {

    private final TiempoSesionRepository tiempoSesionRepository;

    public TiempoSesionService(TiempoSesionRepository tiempoSesionRepository) {
        this.tiempoSesionRepository = tiempoSesionRepository;
    }

    @Transactional(readOnly = true)
    public List<RolTiempoSesion> listar() {
        return tiempoSesionRepository.listar();
    }

    @Transactional
    public RolTiempoSesion actualizar(Integer rolId, int minutosInactividad) {
        return tiempoSesionRepository.actualizar(rolId, minutosInactividad).orElseThrow(RolNoEncontradoException::new);
    }
}
