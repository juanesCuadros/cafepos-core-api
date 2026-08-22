package com.cafepos.core.shared.seguridad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;

/**
 * Hace cumplir en tiempo real la matriz de permisos por rol —
 * usado desde @PreAuthorize con hasPermission('modulo.subvista', 'accion')
 * en cualquier controller de cualquier modulo de negocio.
 *
 * tenant_id y rol_id salen del AuthenticatedUsuario ya puesto en el
 * SecurityContext por JwtAuthenticationFilter — nunca de TenantContext por
 * separado, para no depender de un side-channel distinto del principal que
 * ya trae esos datos.
 *
 * Orden de chequeo:
 *   1. Si el rol del usuario tiene es_editable=false (solo Jefe, ver
 *      V1__schema_v4.sql), se concede SIEMPRE — es una garantia de codigo,
 *      no depende de que rol_permiso este poblado para ese rol. No se
 *      consulta ni el catalogo ni la cache en este caso.
 *   2. Si modulo+accion no existe en absoluto en el catalogo "permiso"
 *      (no una fila con activo=false, sino que la fila del catalogo global
 *      ni existe), se loguea un WARN — probable typo en el string de un
 *      @PreAuthorize — y se deniega.
 *   3. Resto de los roles: fail closed contra la cache de rol_permiso
 *      (PermisoCacheService) — si la combinacion no esta en el set de
 *      activos, se deniega sin excepcion.
 *
 * Solo implementa la variante de 3 argumentos (targetDomainObject =
 * "modulo.subvista", permission = "accion"); la variante de 4 argumentos
 * (targetId/targetType) no se usa en este proyecto.
 */
@Component
public class PermisoEvaluator implements PermissionEvaluator {

    private static final Logger log = LoggerFactory.getLogger(PermisoEvaluator.class);

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PermisoCacheService permisoCacheService;

    public PermisoEvaluator(RolRepository rolRepository,
                             PermisoRepository permisoRepository,
                             PermisoCacheService permisoCacheService) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.permisoCacheService = permisoCacheService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (!(authentication.getPrincipal() instanceof AuthenticatedUsuario usuario)) {
            return false;
        }
        String modulo = String.valueOf(targetDomainObject);
        String accion = String.valueOf(permission);

        Rol rol = rolRepository.findById(usuario.rolId()).orElse(null);
        if (rol != null && !rol.isEsEditable()) {
            return true;
        }

        if (!permisoRepository.existsByModuloAndAccion(modulo, accion)) {
            log.warn("Permiso solicitado no existe en el catalogo: modulo='{}' accion='{}' "
                    + "- revisar el string de @PreAuthorize/hasPermission que lo pidio", modulo, accion);
            return false;
        }

        Set<String> activos = permisoCacheService.permisosActivos(usuario.tenantId(), usuario.rolId());
        return activos.contains(modulo + ":" + accion);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
                                  Object permission) {
        throw new UnsupportedOperationException(
                "PermisoEvaluator solo implementa la variante de 3 argumentos (modulo, accion)");
    }
}
