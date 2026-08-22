package com.cafepos.core.shared.seguridad;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Cache Caffeine de la matriz de permisos activos por tenant+rol — clave
 * "tenantId:rolId", valor el set de "modulo:accion" con activo=true en
 * rol_permiso. TTL de 15 minutos como respaldo (no todo invalidar()
 * explicito desde Configuracion > Roles y Permisos, que todavia no existe).
 *
 * No aplica al rol Jefe (es_editable=false): PermisoEvaluator nunca llega a
 * consultar esta cache para ese caso, ver su Javadoc.
 */
@Service
public class PermisoCacheService {

    private final RolPermisoRepository rolPermisoRepository;
    private final Cache<String, Set<String>> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(15))
            .maximumSize(10_000)
            .build();

    public PermisoCacheService(RolPermisoRepository rolPermisoRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
    }

    public Set<String> permisosActivos(Integer tenantId, Integer rolId) {
        return cache.get(clave(tenantId, rolId),
                k -> new HashSet<>(rolPermisoRepository.findClavesActivas(tenantId, rolId)));
    }

    public void invalidar(Integer tenantId, Integer rolId) {
        cache.invalidate(clave(tenantId, rolId));
    }

    private String clave(Integer tenantId, Integer rolId) {
        return tenantId + ":" + rolId;
    }
}
