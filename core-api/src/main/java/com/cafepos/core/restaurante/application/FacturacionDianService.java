package com.cafepos.core.restaurante.application;

import com.cafepos.core.restaurante.domain.CredencialesFactus;
import com.cafepos.core.restaurante.domain.FacturacionDianEstado;
import com.cafepos.core.restaurante.domain.FacturacionDianRepository;
import com.cafepos.core.restaurante.domain.FacturacionDianResolucion;
import com.cafepos.core.restaurante.domain.NumeroFacturaReservado;
import com.cafepos.core.restaurante.domain.ResolucionVigenteResumen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Solo lectura para su propio modulo/API publica (contrato 10.4, GET
 * /restaurante/facturacion-dian sigue sin POST/PATCH propio) salvo dos
 * casos: reservarSiguienteNumeroFactura() (contador de runtime) y
 * credencialesFactusPara() (lectura descifrada exclusiva para el cliente
 * real de Factus, ver su Javadoc). La escritura de credenciales ya no vive
 * aca — la hace admin-api directo contra la misma tabla (Super Admin,
 * POST /admin/negocios/{tenant_id}/facturacion-dian), core-api solo lee.
 *
 * @NamedInterface: expuesto puntualmente para que otros modulos lo llamen
 * (com.cafepos.core.caja, para reservar numero de factura y para leer
 * credenciales Factus al transmitir) — solo cruzan tipos primitivos,
 * NumeroFacturaReservado y CredencialesFactus (tambien anotados), nunca la
 * entidad FacturacionDianResolucion completa.
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
        Optional<ResolucionVigenteResumen> resolucion = facturacionDianRepository.buscarVigenteResumen();
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
     *
     * Nunca carga la entidad completa: incrementarYReservarNumero() es un
     * UPDATE atomico de una sola sentencia que ni siquiera lee las 4
     * columnas Factus cifradas — un ciphertext corrupto en cualquiera de
     * ellas no puede tumbar esta reserva (y por extension, la venta
     * completa que corre en la misma transaccion). Ver Javadoc de
     * FacturacionDianRepository.incrementarYReservarNumero.
     */
    @Transactional
    public Optional<NumeroFacturaReservado> reservarSiguienteNumeroFactura() {
        Optional<NumeroFacturaReservado> reservado = facturacionDianRepository.incrementarYReservarNumero();
        if (reservado.isEmpty()) {
            return Optional.empty();
        }
        NumeroFacturaReservado r = reservado.get();
        if (r.prefijo() == null || r.prefijo().isBlank()) {
            log.warn("facturacion_dian_resolucion id={} sin prefijo configurado - usando default '{}'",
                    r.resolucionId(), PREFIJO_DEFAULT);
            r = new NumeroFacturaReservado(r.resolucionId(), PREFIJO_DEFAULT, r.numero());
        }
        return Optional.of(r);
    }

    /**
     * API publica de este modulo EXCLUSIVA para el cliente real de Factus
     * (com.cafepos.core.caja.infrastructure.factus) — ningun otro caller
     * deberia necesitar credenciales descifradas. Optional.empty() si el
     * tenant no tiene resolucion vigente, o si la tiene pero sin las 4
     * credenciales Factus completas (resolucion dada de alta manualmente en
     * base sin configurar Factus todavia, ver Javadoc de FacturacionDianResolucion).
     * Sin parametro tenantId a proposito — RLS + TenantContext ya escopan
     * buscarVigenteConCredenciales() al tenant actual, mismo criterio que
     * el resto de metodos de este service.
     */
    @Transactional(readOnly = true)
    public Optional<CredencialesFactus> credencialesFactusPara() {
        return facturacionDianRepository.buscarVigenteConCredenciales()
                .map(FacturacionDianResolucion::credencialesFactus)
                .filter(c -> c.clientId() != null && c.clientSecret() != null && c.username() != null
                        && c.password() != null);
    }
}
