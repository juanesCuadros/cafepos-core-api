package com.cafepos.core.shared.seguridad;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * REQUIRES_NEW a proposito: el evento de seguridad tiene que quedar
 * registrado aunque la transaccion que lo dispara termine en rollback (ej.
 * RefreshTokenService lanza RefreshTokenInvalidoException apenas detecta el
 * reuso) — si compartiera la transaccion del caller, el rollback se
 * llevaria puesto el registro de auditoria justo en el caso que mas
 * importa dejar rastro.
 */
@Service
public class EventoSeguridadService {

    private final EventoSeguridadRepository eventoSeguridadRepository;

    public EventoSeguridadService(EventoSeguridadRepository eventoSeguridadRepository) {
        this.eventoSeguridadRepository = eventoSeguridadRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(Integer tenantId, Integer usuarioId, String tipoEvento, String detalle) {
        eventoSeguridadRepository.save(new EventoSeguridad(tenantId, usuarioId, tipoEvento, detalle));
    }
}
