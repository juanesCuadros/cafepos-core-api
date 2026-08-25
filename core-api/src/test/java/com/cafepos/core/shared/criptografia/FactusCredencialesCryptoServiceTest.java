package com.cafepos.core.shared.criptografia;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unitario puro (sin Spring, sin DB) — construye FactusCredencialesCryptoService
 * a mano con llaves de prueba generadas en memoria, igual formato que
 * produce openssl rand -base64 32.
 */
class FactusCredencialesCryptoServiceTest {

    @Test
    void cifraYDescifra_devuelveElValorOriginal() {
        FactusCredencialesCryptoService servicio = new FactusCredencialesCryptoService(claveDePrueba());

        String cifrado = servicio.encrypt("client-secret-de-prueba-123");

        assertThat(cifrado).isNotEqualTo("client-secret-de-prueba-123");
        assertThat(servicio.decrypt(cifrado)).isEqualTo("client-secret-de-prueba-123");
    }

    @Test
    void mismoValorCifradoDosVeces_daResultadosDistintos_porIvAleatorio() {
        FactusCredencialesCryptoService servicio = new FactusCredencialesCryptoService(claveDePrueba());

        String cifrado1 = servicio.encrypt("mismo-valor");
        String cifrado2 = servicio.encrypt("mismo-valor");

        assertThat(cifrado1).isNotEqualTo(cifrado2);
        assertThat(servicio.decrypt(cifrado1)).isEqualTo("mismo-valor");
        assertThat(servicio.decrypt(cifrado2)).isEqualTo("mismo-valor");
    }

    @Test
    void descifrarConOtraLlave_fallaSinFiltrarLaLlaveNiElValorEnElMensaje() {
        String valorOriginal = "client-secret-super-secreto-no-filtrar";
        FactusCredencialesCryptoService servicioClaveA = new FactusCredencialesCryptoService(claveDePrueba());
        FactusCredencialesCryptoService servicioClaveB = new FactusCredencialesCryptoService(claveDePrueba());

        String cifradoConA = servicioClaveA.encrypt(valorOriginal);

        assertThatThrownBy(() -> servicioClaveB.decrypt(cifradoConA))
                .isInstanceOf(CifradoException.class)
                .satisfies(ex -> {
                    assertThat(ex.getMessage()).doesNotContain(valorOriginal);
                    assertThat(ex.getMessage()).doesNotContain(cifradoConA);
                    if (ex.getCause() != null) {
                        String causaMensaje = String.valueOf(ex.getCause().getMessage());
                        assertThat(causaMensaje).doesNotContain(valorOriginal);
                        assertThat(causaMensaje).doesNotContain(cifradoConA);
                    }
                });
    }

    @Test
    void nullPasaDeLargoSinCifrar() {
        FactusCredencialesCryptoService servicio = new FactusCredencialesCryptoService(claveDePrueba());

        assertThat(servicio.encrypt(null)).isNull();
        assertThat(servicio.decrypt(null)).isNull();
    }

    @Test
    void llaveQueNoDecodificaA32Bytes_fallaAlConstruir() {
        String llave16Bytes = Base64.getEncoder().encodeToString(new byte[16]);

        assertThatThrownBy(() -> new FactusCredencialesCryptoService(llave16Bytes))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 bytes");
    }

    /**
     * Calculo de tamano del blob almacenado (ver Javadoc de FactusCredencialesCryptoService):
     * overhead fijo = 12 (IV) + 16 (tag) = 28 bytes antes de Base64. Confirma con codigo real
     * (no supuesto) que 161 caracteres de texto plano es el maximo que entra en VARCHAR(255),
     * y que 162 ya se pasa.
     */
    @Test
    void textoPlanoDe161Caracteres_caeDentroDeVarchar255_162Caracteres_seDesborda() {
        FactusCredencialesCryptoService servicio = new FactusCredencialesCryptoService(claveDePrueba());

        String texto161 = "a".repeat(161);
        String texto162 = "a".repeat(162);

        String cifrado161 = servicio.encrypt(texto161);
        String cifrado162 = servicio.encrypt(texto162);

        assertThat(cifrado161.length()).isLessThanOrEqualTo(255);
        assertThat(cifrado162.length()).isGreaterThan(255);
        assertThat(servicio.decrypt(cifrado161)).isEqualTo(texto161);
    }

    private static String claveDePrueba() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
