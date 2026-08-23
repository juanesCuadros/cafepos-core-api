package com.cafepos.core.configuracion.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de tenant_rol_config — implementado en infrastructure.persistence. */
public interface TiempoSesionRepository {

    List<RolTiempoSesion> listar();

    /** empty si rolId no corresponde a una fila de tenant_rol_config para el tenant actual. */
    Optional<RolTiempoSesion> actualizar(Integer rolId, int minutosInactividad);
}
