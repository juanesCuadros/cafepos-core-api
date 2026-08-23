package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.configuracion.domain.UsuarioCorreoDuplicadoException;
import com.cafepos.core.configuracion.domain.UsuarioDetalle;
import com.cafepos.core.configuracion.domain.UsuarioRepository;
import com.cafepos.core.configuracion.domain.UsuarioResumen;
import com.cafepos.core.shared.seguridad.Usuario;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class UsuarioRepositoryAdapter implements UsuarioRepository {

    /** Nombre auto-generado por Postgres para UNIQUE(tenant_id, correo) — ver V1__schema_v4.sql. */
    private static final String CONSTRAINT_CORREO_DUPLICADO = "usuario_tenant_id_correo_key";

    private final UsuarioJpaRepository jpaRepository;

    UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        try {
            return jpaRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException ex) {
            String mensaje = ex.getMostSpecificCause().getMessage();
            if (mensaje != null && mensaje.contains(CONSTRAINT_CORREO_DUPLICADO)) {
                throw new UsuarioCorreoDuplicadoException();
            }
            throw ex;
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void eliminar(Usuario usuario) {
        jpaRepository.delete(usuario);
    }

    @Override
    public List<UsuarioResumen> listar(Integer rolId, String estado) {
        return jpaRepository.listar(rolId, estado).stream()
                .map(row -> new UsuarioResumen(row.getId(), row.getNombre(), row.getCorreo(), row.getRol(),
                        row.getEmpleadoAsociado(), row.getEstado()))
                .toList();
    }

    @Override
    public Optional<UsuarioDetalle> detalleDe(Integer id) {
        return jpaRepository.detalleDe(id)
                .map(row -> new UsuarioDetalle(row.getId(), row.getNombre(), row.getCorreo(), row.getRolId(),
                        row.getRol(), row.getEmpleadoId(), row.getEmpleadoAsociado(), row.getEstado()));
    }

    @Override
    public long contarActivosPorRol(Integer rolId) {
        return jpaRepository.contarActivosPorRol(rolId);
    }
}
