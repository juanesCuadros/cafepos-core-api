package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.configuracion.domain.RolTiempoSesion;
import com.cafepos.core.configuracion.domain.TiempoSesionRepository;
import com.cafepos.core.shared.seguridad.Rol;
import com.cafepos.core.shared.seguridad.RolRepository;
import com.cafepos.core.shared.seguridad.TenantRolConfig;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class TiempoSesionRepositoryAdapter implements TiempoSesionRepository {

    private final TenantRolConfigJpaRepository jpaRepository;
    private final RolRepository rolRepository;

    TiempoSesionRepositoryAdapter(TenantRolConfigJpaRepository jpaRepository, RolRepository rolRepository) {
        this.jpaRepository = jpaRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public List<RolTiempoSesion> listar() {
        return jpaRepository.listar().stream()
                .map(row -> new RolTiempoSesion(row.getRolId(), row.getRol(), row.getMinutosInactividad()))
                .toList();
    }

    @Override
    public Optional<RolTiempoSesion> actualizar(Integer rolId, int minutosInactividad) {
        Optional<TenantRolConfig> configuracion = jpaRepository.findByRolId(rolId);
        if (configuracion.isEmpty()) {
            return Optional.empty();
        }
        TenantRolConfig entidad = configuracion.get();
        entidad.actualizarMinutos(minutosInactividad);
        jpaRepository.save(entidad);
        String nombreRol = rolRepository.findById(rolId).map(Rol::getNombre).orElse(null);
        return Optional.of(new RolTiempoSesion(rolId, nombreRol, minutosInactividad));
    }
}
