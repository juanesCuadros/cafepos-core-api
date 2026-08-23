package com.cafepos.core.configuracion.application;

import com.cafepos.core.configuracion.domain.ConfiguracionSistema;
import com.cafepos.core.configuracion.domain.ConfiguracionSistemaNoConfiguradaException;
import com.cafepos.core.configuracion.domain.ConfiguracionSistemaRepository;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.operacion
 * lea modo_comanda al enviar una comanda (ver obtenerModoComanda) — solo
 * cruza un String, nunca la entidad ConfiguracionSistema completa.
 */
@org.springframework.modulith.NamedInterface("configuracionSistemaService")
@Service
public class ConfiguracionSistemaService {

    private final ConfiguracionSistemaRepository configuracionSistemaRepository;

    public ConfiguracionSistemaService(ConfiguracionSistemaRepository configuracionSistemaRepository) {
        this.configuracionSistemaRepository = configuracionSistemaRepository;
    }

    @Transactional(readOnly = true)
    public ConfiguracionSistema obtener() {
        return configuracionSistemaRepository.buscarPorTenantActual()
                .orElseThrow(ConfiguracionSistemaNoConfiguradaException::new);
    }

    /** API publica de este modulo para enviar-comanda (com.cafepos.core.operacion). */
    @Transactional(readOnly = true)
    public String obtenerModoComanda() {
        return obtener().getModoComanda();
    }

    @Transactional
    public ConfiguracionSistema actualizar(String modoComanda, JsonNullable<Integer> tiempoLimitePrepMin,
                                            String propinaTipo, JsonNullable<BigDecimal> propinaPorcentaje,
                                            JsonNullable<String> propinaDestino,
                                            JsonNullable<BigDecimal> propinaPctMesero,
                                            JsonNullable<Integer> diasAnticipacionVencim,
                                            JsonNullable<String> estadoConexionDian,
                                            JsonNullable<BigDecimal> ivaPorcentaje,
                                            JsonNullable<BigDecimal> incPorcentaje) {
        ConfiguracionSistema configuracionSistema = obtener();
        configuracionSistema.actualizar(modoComanda, tiempoLimitePrepMin, propinaTipo, propinaPorcentaje,
                propinaDestino, propinaPctMesero, diasAnticipacionVencim, estadoConexionDian, ivaPorcentaje,
                incPorcentaje);
        return configuracionSistemaRepository.guardar(configuracionSistema);
    }
}
