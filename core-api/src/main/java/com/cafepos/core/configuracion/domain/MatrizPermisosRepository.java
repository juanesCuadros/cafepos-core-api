package com.cafepos.core.configuracion.domain;

import java.util.List;

/**
 * Puerto de persistencia de la matriz de permisos por rol — implementado en
 * infrastructure.persistence sobre las tablas compartidas permiso/rol_permiso.
 */
public interface MatrizPermisosRepository {

    /** Los 151 permisos del catalogo LEFT JOIN rol_permiso para (tenant actual, rolId) — activo crudo, sin el bypass de Jefe. */
    List<PermisoMatrizItem> obtenerMatrizCruda(Integer rolId);

    /** Crea la fila en rol_permiso si no existe (activo nace true por construccion) — no-op si ya existe. */
    void activar(Integer tenantId, Integer rolId, Integer permisoId);

    /** Borra la fila de rol_permiso si existe — no-op si no existe. */
    void desactivar(Integer tenantId, Integer rolId, Integer permisoId);
}
