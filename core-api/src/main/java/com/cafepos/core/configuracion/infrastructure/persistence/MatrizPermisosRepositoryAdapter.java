package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.configuracion.domain.MatrizPermisosRepository;
import com.cafepos.core.configuracion.domain.PermisoMatrizItem;
import com.cafepos.core.shared.seguridad.RolPermiso;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class MatrizPermisosRepositoryAdapter implements MatrizPermisosRepository {

    private final RolPermisoJpaRepository jpaRepository;

    MatrizPermisosRepositoryAdapter(RolPermisoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<PermisoMatrizItem> obtenerMatrizCruda(Integer rolId) {
        return jpaRepository.obtenerMatrizCruda(rolId).stream()
                .map(row -> new PermisoMatrizItem(row.getPermisoId(), row.getModulo(), row.getAccion(),
                        row.getActivo()))
                .toList();
    }

    @Override
    public void activar(Integer tenantId, Integer rolId, Integer permisoId) {
        Optional<RolPermiso> existente = jpaRepository.findByTenantIdAndRolIdAndPermisoId(tenantId, rolId, permisoId);
        if (existente.isEmpty()) {
            jpaRepository.save(new RolPermiso(tenantId, rolId, permisoId));
        }
    }

    @Override
    public void desactivar(Integer tenantId, Integer rolId, Integer permisoId) {
        jpaRepository.findByTenantIdAndRolIdAndPermisoId(tenantId, rolId, permisoId)
                .ifPresent(jpaRepository::delete);
    }
}
