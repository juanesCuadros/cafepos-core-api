package com.cafepos.core.shared.criptografia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES-256-GCM (cifrado autenticado — protege confidencialidad e integridad
 * a la vez, a diferencia de CBC) de client_id_factus/client_secret_factus.
 * javax.crypto de la JDK estandar alcanza, sin dependencia nueva.
 *
 * Formato almacenado (un solo String Base64): IV (12 bytes, aleatorio por
 * cada valor — GCM nunca debe reusar IV con la misma llave) + texto cifrado
 * + tag de autenticacion GCM (16 bytes, la propia Cipher lo pega al final
 * del texto cifrado en modo GCM, no hay que manejarlo aparte).
 *
 * Calculo real de que VARCHAR(255) alcanza (ver migracion V1, columnas
 * client_id_factus/client_secret_factus): overhead fijo = 12 (IV) + 16
 * (tag) = 28 bytes antes de Base64. Base64 estandar con padding produce
 * 4*ceil(N/3) caracteres. Despejando 4*ceil((texto_plano+28)/3) <= 255:
 * el texto plano cabe hasta 161 bytes (161 caracteres ASCII) — a esa
 * longitud el Base64 resultante mide exactamente 252 caracteres, 3 de
 * margen contra el limite de 255. Un client_secret tipico de Factus (unas
 * pocas decenas de caracteres) queda muy por debajo de eso; 161+ caracteres
 * SI desbordaria la columna — ver FactusCredencialesCryptoServiceTest para
 * la verificacion con valores reales.
 */
@Component
public class FactusCredencialesCryptoService {

    private static final String TRANSFORMACION = "AES/GCM/NoPadding";
    private static final int TAMANO_IV_BYTES = 12;
    private static final int TAMANO_TAG_BITS = 128;
    private static final int TAMANO_LLAVE_BYTES = 32; // AES-256

    private final SecretKeySpec llave;
    private final SecureRandom random = new SecureRandom();

    public FactusCredencialesCryptoService(@Value("${cafepos.factus.encryption-key}") String llaveBase64) {
        byte[] llaveBytes;
        try {
            llaveBytes = Base64.getDecoder().decode(llaveBase64);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "cafepos.factus.encryption-key no es Base64 valido — generar con openssl rand -base64 32");
        }
        if (llaveBytes.length != TAMANO_LLAVE_BYTES) {
            throw new IllegalStateException("cafepos.factus.encryption-key debe decodificar a "
                    + TAMANO_LLAVE_BYTES + " bytes (AES-256), decodifico a " + llaveBytes.length
                    + " — generar con openssl rand -base64 32");
        }
        this.llave = new SecretKeySpec(llaveBytes, "AES");
    }

    /** null pasa de largo sin cifrar — client_id_factus/client_secret_factus son nullable mientras no se configuran. */
    public String encrypt(String textoPlano) {
        if (textoPlano == null) {
            return null;
        }
        try {
            byte[] iv = new byte[TAMANO_IV_BYTES];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMACION);
            cipher.init(Cipher.ENCRYPT_MODE, llave, new GCMParameterSpec(TAMANO_TAG_BITS, iv));
            byte[] cifrado = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));

            byte[] combinado = new byte[iv.length + cifrado.length];
            System.arraycopy(iv, 0, combinado, 0, iv.length);
            System.arraycopy(cifrado, 0, combinado, iv.length, cifrado.length);
            return Base64.getEncoder().encodeToString(combinado);
        } catch (GeneralSecurityException ex) {
            // ex de javax.crypto nunca trae la llave ni el texto plano en su mensaje, es seguro encadenarla
            throw new CifradoException("No se pudo cifrar el valor", ex);
        }
    }

    /** null pasa de largo sin descifrar — mismo motivo que encrypt. */
    public String decrypt(String valorAlmacenado) {
        if (valorAlmacenado == null) {
            return null;
        }
        byte[] combinado;
        try {
            combinado = Base64.getDecoder().decode(valorAlmacenado);
        } catch (IllegalArgumentException ex) {
            throw new CifradoException("El valor almacenado no es Base64 valido — dato corrupto");
        }
        if (combinado.length <= TAMANO_IV_BYTES) {
            throw new CifradoException("El valor almacenado es mas corto de lo esperado — dato corrupto");
        }

        byte[] iv = Arrays.copyOfRange(combinado, 0, TAMANO_IV_BYTES);
        byte[] cifrado = Arrays.copyOfRange(combinado, TAMANO_IV_BYTES, combinado.length);
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMACION);
            cipher.init(Cipher.DECRYPT_MODE, llave, new GCMParameterSpec(TAMANO_TAG_BITS, iv));
            byte[] textoPlano = cipher.doFinal(cifrado);
            return new String(textoPlano, StandardCharsets.UTF_8);
        } catch (AEADBadTagException ex) {
            // se dispara con la llave incorrecta (o dato manipulado) — GCM valida el tag antes de devolver nada
            throw new CifradoException(
                    "No se pudo descifrar el valor — la llave de cifrado cambio o el dato esta corrupto");
        } catch (GeneralSecurityException ex) {
            throw new CifradoException("No se pudo descifrar el valor — la llave de cifrado cambio o el dato esta corrupto", ex);
        }
    }
}
