package com.cafepos.core.configuracion.application;

import com.cafepos.core.configuracion.domain.ConfiguracionSistema;
import com.cafepos.core.configuracion.domain.ConfiguracionSistemaNoConfiguradaException;
import com.cafepos.core.configuracion.domain.ConfiguracionSistemaRepository;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
