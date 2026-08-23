package com.cafepos.core.configuracion.application;

import com.cafepos.core.configuracion.domain.CambioPermiso;
import com.cafepos.core.configuracion.domain.MatrizPermisosRepository;
import com.cafepos.core.configuracion.domain.MatrizRolPermisos;
import com.cafepos.core.configuracion.domain.ModuloPermisos;
import com.cafepos.core.configuracion.domain.PermisoMatrizItem;
import com.cafepos.core.configuracion.domain.RolJefeNoEditableException;
import com.cafepos.core.configuracion.domain.RolNoEncontradoException;
import com.cafepos.core.configuracion.domain.UsuarioRepository;
import com.cafepos.core.shared.seguridad.PermisoCacheService;
import com.cafepos.core.shared.seguridad.Rol;
import com.cafepos.core.shared.seguridad.RolRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfiguracionRolService {

    private final RolRepository rolRepository;
    private final MatrizPermisosRepository matrizPermisosRepository;
    private final UsuarioRepository usuarioRepository;
    private final PermisoCacheService permisoCacheService;

    public ConfiguracionRolService(RolRepository rolRepository, MatrizPermisosRepository matrizPermisosRepository,
                                    UsuarioRepository usuarioRepository, PermisoCacheService permisoCacheService) {
        this.rolRepository = rolRepository;
        this.matrizPermisosRepository = matrizPermisosRepository;
        this.usuarioRepository = usuarioRepository;
        this.permisoCacheService = permisoCacheService;
    }

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    /**
     * Rol Jefe (es_editable=false): todo permiso se muestra activo=true
     * aunque no exista fila en rol_permiso — el bypass de codigo en
     * PermisoEvaluator es la fuente de verdad, no los datos.
     */
    @Transactional(readOnly = true)
    public MatrizRolPermisos matrizDe(Integer rolId) {
        Rol rol = rolRepository.findById(rolId).orElseThrow(RolNoEncontradoException::new);
        List<PermisoMatrizItem> crudos = matrizPermisosRepository.obtenerMatrizCruda(rolId);
        Map<String, List<PermisoMatrizItem>> porModuloPadre = new LinkedHashMap<>();
        for (PermisoMatrizItem item : crudos) {
            boolean activoFinal = !rol.isEsEditable() || item.activo();
            PermisoMatrizItem itemFinal = new PermisoMatrizItem(item.permisoId(), item.modulo(), item.accion(),
                    activoFinal);
            String padre = moduloPadre(item.modulo());
            porModuloPadre.computeIfAbsent(padre, k -> new ArrayList<>()).add(itemFinal);
        }
        List<ModuloPermisos> modulos = porModuloPadre.entrySet().stream()
                .map(e -> new ModuloPermisos(nombreDisplay(e.getKey()), e.getValue()))
                .toList();
        return new MatrizRolPermisos(rol.getNombre(), rol.isEsEditable(), modulos);
    }

    /**
     * Jefe nunca es editable, 403 siempre, antes de tocar nada del request.
     * Invalida la cache SIEMPRE al final (aunque cambios este vacio) para
     * mantener el contrato simple — un PATCH con lista vacia es un no-op
     * legitimo, no un error.
     */
    @Transactional
    public int actualizarPermisos(Integer rolId, List<CambioPermiso> cambios) {
        Rol rol = rolRepository.findById(rolId).orElseThrow(RolNoEncontradoException::new);
        if (!rol.isEsEditable()) {
            throw new RolJefeNoEditableException();
        }
        Integer tenantId = TenantContext.getCurrentTenantId();
        for (CambioPermiso cambio : cambios) {
            if (cambio.activo()) {
                matrizPermisosRepository.activar(tenantId, rolId, cambio.permisoId());
            } else {
                matrizPermisosRepository.desactivar(tenantId, rolId, cambio.permisoId());
            }
        }
        permisoCacheService.invalidar(tenantId, rolId);
        return (int) usuarioRepository.contarActivosPorRol(rolId);
    }

    private static String moduloPadre(String modulo) {
        int puntoIdx = modulo.indexOf('.');
        return puntoIdx >= 0 ? modulo.substring(0, puntoIdx) : modulo;
    }

    private static String nombreDisplay(String moduloPadre) {
        String[] palabras = moduloPadre.split("_");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (palabra.isEmpty()) {
                continue;
            }
            if (resultado.length() > 0) {
                resultado.append(' ');
            }
            resultado.append(Character.toUpperCase(palabra.charAt(0))).append(palabra.substring(1));
        }
        return resultado.toString();
    }
}
