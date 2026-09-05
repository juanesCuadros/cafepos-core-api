package com.cafepos.admin.negocios.application;

import com.cafepos.admin.negocios.domain.FacturacionDianResolucion;
import com.cafepos.admin.negocios.domain.FacturacionDianResolucionRepository;
import com.cafepos.admin.negocios.domain.TenantNoEncontradoException;
import com.cafepos.admin.negocios.domain.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Configuracion PERMANENTE de credenciales Factus por tenant, exclusiva de
 * Super Admin — reemplaza el endpoint temporal que existia en core-api
 * (admintemporal.factus.guardarCredencialesReales, eliminado). Solo
 * ESCRIBE: nunca vuelve a leer client_id/client_secret/username/password en
 * texto plano, quien los descifra para llamar a Factus de verdad es
 * core-api (misma llave de cifrado en los dos proyectos, ver
 * shared.criptografia.FactusCredencialesCryptoService y su Javadoc de
 * sincronizacion).
 */
@Service
public class FacturacionDianAdminService {

    private final FacturacionDianResolucionRepository facturacionDianResolucionRepository;
    private final TenantRepository tenantRepository;

    public FacturacionDianAdminService(FacturacionDianResolucionRepository facturacionDianResolucionRepository,
                                        TenantRepository tenantRepository) {
        this.facturacionDianResolucionRepository = facturacionDianResolucionRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public void configurarCredenciales(Integer tenantId, String ambienteFactus, String clientId, String clientSecret,
                                        String username, String password, Long rangoInicio, Long rangoFin,
                                        String prefijo, LocalDate fechaExpedicion, LocalDate fechaVencimiento,
                                        Long numberingRangeId) {
        tenantRepository.findById(tenantId).orElseThrow(TenantNoEncontradoException::new);

        FacturacionDianResolucion resolucion = facturacionDianResolucionRepository.buscarVigentePorTenant(tenantId)
                .orElseGet(() -> FacturacionDianResolucion.crear(tenantId));
        resolucion.configurarCredencialesFactus(clientId, clientSecret, username, password, rangoInicio, rangoFin,
                mapearAmbienteDb(ambienteFactus), "vigente", prefijo, fechaExpedicion, fechaVencimiento,
                numberingRangeId);
        facturacionDianResolucionRepository.guardar(resolucion);
    }

    /**
     * Mismo mapeo ya resuelto en core-api (FacturacionDianService) —
     * duplicado aca a proposito: la columna real solo acepta 'pruebas'/
     * 'produccion' (CHECK constraint, ver V1__schema_v4.sql en core-api),
     * pero el vocabulario que usa la API de Factus (y este endpoint) es
     * 'sandbox'/'produccion'.
     */
    private static String mapearAmbienteDb(String ambienteFactus) {
        return "sandbox".equals(ambienteFactus) ? "pruebas" : ambienteFactus;
    }
}
