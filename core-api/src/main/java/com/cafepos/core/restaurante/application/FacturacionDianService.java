package com.cafepos.core.restaurante.application;

import com.cafepos.core.restaurante.domain.FacturacionDianEstado;
import com.cafepos.core.restaurante.domain.FacturacionDianRepository;
import com.cafepos.core.restaurante.domain.FacturacionDianResolucion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Solo lectura a proposito — no existe crear/actualizar, ver contrato 10.4. */
@Service
public class FacturacionDianService {

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
}
