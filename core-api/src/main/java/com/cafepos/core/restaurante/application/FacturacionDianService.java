package com.cafepos.core.restaurante.application;

import com.cafepos.core.restaurante.domain.FacturacionDianEstado;
import com.cafepos.core.restaurante.domain.FacturacionDianRepository;
import com.cafepos.core.restaurante.domain.FacturacionDianResolucion;
import com.cafepos.core.restaurante.domain.NumeroFacturaReservado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Solo lectura para su propio modulo (ver contrato 10.4, sin crear/actualizar
 * de prefijo/rango/etc).
 *
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.caja
 * reserve el siguiente numero de factura al cobrar con cliente identificado
 * (ver reservarSiguienteNumeroFactura) — solo cruza NumeroFacturaReservado
 * (tambien anotado), nunca la entidad FacturacionDianResolucion completa.
 */
@org.springframework.modulith.NamedInterface("facturacionDianService")
@Service
public class FacturacionDianService {

    /** Fallback SOLO si la resolucion vigente tiene prefijo null/vacio (columna nullable en el schema) — caso raro, se loguea. */
    private static final String PREFIJO_DEFAULT = "FE";

    private static final Logger log = LoggerFactory.getLogger(FacturacionDianService.class);

    private final FacturacionDianRepository facturacionDianRepository;

    public FacturacionDianService(FacturacionDianRepository facturacionDianRepository) {
        this.facturacionDianRepository = facturacionDianRepository;
    }

    @Transactional(readOnly = true)
    public FacturacionDianEstado obtener() {
        Optional<FacturacionDianResolucion> resolucion = facturacionDianRepository.buscarVigente();
        if (resolucion.isEmpty()) {
            return FacturacionDianEstado.noConfigurada();
        }
        String estadoConexion = facturacionDianRepository.buscarEstadoConexion().orElse("inactiva");
        return FacturacionDianEstado.de(resolucion.get(), estadoConexion);
    }

    /**
     * API publica de este modulo para com.cafepos.core.caja: reserva
     * (incrementa y persiste) el siguiente numero de secuencia de la
     * resolucion DIAN vigente del tenant, con su prefijo REAL (distintos
     * tenants pueden tener prefijos distintos configurados por la DIAN —
     * nunca hardcodear "FE" en el modulo que consume esto). Optional.empty()
     * si el tenant no tiene ninguna resolucion configurada — caja no debe
     * fallar la venta por esto, solo omite crear la factura (cliente queda
     * asociado igual).
     *
     * NO valida rango_inicio/rango_fin/estado='agotada' todavia — fuera de
     * alcance de este prompt (simplificacion aceptada, documentada).
     */
    @Transactional
    public Optional<NumeroFacturaReservado> reservarSiguienteNumeroFactura() {
        Optional<FacturacionDianResolucion> resolucion = facturacionDianRepository.buscarVigente();
        if (resolucion.isEmpty()) {
            return Optional.empty();
        }
        FacturacionDianResolucion r = resolucion.get();
        r.incrementarNumeracion();
        r = facturacionDianRepository.guardar(r);

        String prefijo = r.getPrefijo();
        if (prefijo == null || prefijo.isBlank()) {
            log.warn("facturacion_dian_resolucion id={} sin prefijo configurado - usando default '{}'",
                    r.getId(), PREFIJO_DEFAULT);
            prefijo = PREFIJO_DEFAULT;
        }
        return Optional.of(new NumeroFacturaReservado(r.getId(), prefijo, r.getNumeracionActual().intValue()));
    }
}
