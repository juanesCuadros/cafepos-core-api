package com.cafepos.core.operacion.domain;

import java.util.Optional;

/** Puerto de persistencia de Turno — implementado en infrastructure.persistence. */
public interface TurnoRepository {

    Turno guardar(Turno turno);

    /** Turno con hora_fin IS NULL de ese usuario, si existe — ver TurnoService. */
    Optional<Turno> buscarActivoPorUsuario(Integer usuarioId);
}
