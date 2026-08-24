package com.cafepos.core.restaurante.application;

import com.cafepos.core.restaurante.domain.MetodoPago;
import com.cafepos.core.restaurante.domain.MetodoPagoEfectivoNoDesactivableException;
import com.cafepos.core.restaurante.domain.MetodoPagoEfectivoNoEliminableException;
import com.cafepos.core.restaurante.domain.MetodoPagoNoEncontradoException;
import com.cafepos.core.restaurante.domain.MetodoPagoRepository;
import com.cafepos.core.restaurante.domain.MetodoPagoResumen;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.caja
 * valide metodo_pago_id al cobrar (ver buscarResumenPorId) — solo cruza
 * MetodoPagoResumen (tambien anotado), nunca la entidad MetodoPago completa.
 */
@org.springframework.modulith.NamedInterface("metodoPagoService")
@Service
public class MetodoPagoService {

    private static final String ESTADO_INACTIVO = "inactivo";

    private final MetodoPagoRepository metodoPagoRepository;

    public MetodoPagoService(MetodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @Transactional(readOnly = true)
    public List<MetodoPagoResumen> listar() {
        return metodoPagoRepository.listar();
    }

    /** API publica de este modulo para validar metodo_pago_id al cobrar (com.cafepos.core.caja). */
    @Transactional(readOnly = true)
    public Optional<MetodoPagoResumen> buscarResumenPorId(Integer id) {
        return metodoPagoRepository.buscarPorId(id)
                .map(m -> new MetodoPagoResumen(m.getId(), m.getNombre(), m.getIcono(), m.isEsEfectivo(),
                        m.getEstado()));
    }

    @Transactional
    public MetodoPago crear(String nombre, String icono, String estado) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        MetodoPago metodoPago = new MetodoPago(tenantId, nombre, icono, estado);
        return metodoPagoRepository.guardar(metodoPago);
    }

    @Transactional
    public MetodoPago actualizar(Integer id, JsonNullable<String> icono, String nombre, String estado) {
        MetodoPago metodoPago = buscarPorId(id);
        if (metodoPago.isEsEfectivo() && ESTADO_INACTIVO.equals(estado)) {
            throw new MetodoPagoEfectivoNoDesactivableException();
        }
        metodoPago.actualizar(icono, nombre, estado);
        return metodoPagoRepository.guardar(metodoPago);
    }

    @Transactional
    public void eliminar(Integer id) {
        MetodoPago metodoPago = buscarPorId(id);
        if (metodoPago.isEsEfectivo()) {
            throw new MetodoPagoEfectivoNoEliminableException();
        }
        metodoPagoRepository.eliminar(metodoPago);
    }

    private MetodoPago buscarPorId(Integer id) {
        return metodoPagoRepository.buscarPorId(id).orElseThrow(MetodoPagoNoEncontradoException::new);
    }
}
