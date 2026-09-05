package com.cafepos.core.restaurante.domain;

import com.cafepos.core.shared.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba real de punta a punta contra Postgres dev (requiere docker compose
 * up -d) — confirma que @Convert(FactusCredencialAttributeConverter) cifra
 * al escribir y descifra al leer a traves del repositorio real (no del
 * converter aislado — eso ya lo cubre FactusCredencialesCryptoServiceTest).
 *
 * Usa la fila ya sembrada de facturacion_dian_resolucion del tenant
 * "cafeteria-demo" (tenant_id=9, id=1 en la base dev) — sus
 * client_id_factus/client_secret_factus ya venian en texto plano como
 * fixture de dev, asi que sobreescribirlos aca con valores de prueba y
 * releerlos demuestra la migracion real de texto plano a cifrado.
 *
 * Este test esta en el paquete domain a proposito: clientIdFactus/
 * clientSecretFactus tienen @Getter(AccessLevel.NONE) en la entidad (ver
 * su Javadoc), asi que ReflectionTestUtils es la unica forma de leer/
 * escribir esos dos campos, incluso desde un test.
 *
 * Nunca hay HTTP de por medio, asi que TenantContext se fija a mano antes
 * de cada llamada al repositorio — mismo patron que PermisoEvaluatorIT.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("dev")
class FacturacionDianResolucionEncriptacionIT {

    private static final Integer TENANT_CAFETERIA_DEMO = 9;
    private static final String CLIENT_ID_PRUEBA = "factus-client-id-prueba-claude";
    private static final String CLIENT_SECRET_PRUEBA = "factus-client-secret-prueba-claude-99999";

    @Autowired
    private FacturacionDianRepository facturacionDianRepository;

    @AfterEach
    void limpiarTenantContext() {
        TenantContext.clear();
    }

    @Test
    void guardarYReleerViaRepositorio_devuelveElValorOriginalSinCifrar() {
        TenantContext.setCurrentTenantId(TENANT_CAFETERIA_DEMO);

        FacturacionDianResolucion resolucion = facturacionDianRepository.buscarVigenteConCredenciales().orElseThrow();
        ReflectionTestUtils.setField(resolucion, "clientIdFactus", CLIENT_ID_PRUEBA);
        ReflectionTestUtils.setField(resolucion, "clientSecretFactus", CLIENT_SECRET_PRUEBA);
        Integer idFila = facturacionDianRepository.guardar(resolucion).getId();

        // Cada llamada al repositorio abre su propia transaccion/EntityManager
        // (open-in-view: false, sin @Transactional en el test) — esta relectura
        // pega contra la base de nuevo, no devuelve el mismo objeto en memoria.
        FacturacionDianResolucion releida = facturacionDianRepository.buscarVigenteConCredenciales().orElseThrow();

        assertThat(releida.getId()).isEqualTo(idFila);
        assertThat(ReflectionTestUtils.getField(releida, "clientIdFactus")).isEqualTo(CLIENT_ID_PRUEBA);
        assertThat(ReflectionTestUtils.getField(releida, "clientSecretFactus")).isEqualTo(CLIENT_SECRET_PRUEBA);

        System.out.println("FACTUS_ENCRIPTACION_IT_FILA_ID=" + idFila);
    }
}
