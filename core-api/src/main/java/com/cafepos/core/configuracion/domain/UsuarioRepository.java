package com.cafepos.core.configuracion.domain;

import com.cafepos.core.shared.seguridad.Usuario;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Usuario (entidad compartida en shared.seguridad) — implementado en infrastructure.persistence. */
public interface UsuarioRepository {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Integer id);

    void eliminar(Usuario usuario);

    List<UsuarioResumen> listar(Integer rolId, String estado);

    Optional<UsuarioDetalle> detalleDe(Integer id);

    long contarActivosPorRol(Integer rolId);
}
