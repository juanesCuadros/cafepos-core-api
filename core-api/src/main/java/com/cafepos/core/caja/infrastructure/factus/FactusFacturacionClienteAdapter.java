package com.cafepos.core.caja.infrastructure.factus;

import com.cafepos.core.caja.domain.ClienteTransmisionFactus;
import com.cafepos.core.caja.domain.FacturaDianTransmisorPort;
import com.cafepos.core.caja.domain.ItemTransmisionFactus;
import com.cafepos.core.caja.domain.PagoTransmisionFactus;
import com.cafepos.core.caja.domain.ResultadoTransmisionFactus;
import com.cafepos.core.caja.domain.SolicitudTransmisionFactus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Implementacion real de FacturaDianTransmisorPort — misma autenticacion
 * (grant_type=password) y mismo shape de body ya probados manualmente en
 * com.cafepos.core.admintemporal.factus.FactusProbarConexionService (que
 * este paquete reemplaza), con el mapeo real de DECISIONES YA TOMADAS
 * encima. Timeout de 15 segundos (conexion+lectura) especifico de este
 * cliente — no el default del proyecto — para que un Factus lento nunca
 * bloquee POST /ventas mas de lo necesario (ver FacturaDianTransmisionService,
 * que ademas corre esto en un hilo aparte tras el commit).
 *
 * NUNCA loguea client_id/client_secret/username/password, ni siquiera en
 * WARN — solo el status HTTP o el nombre de la excepcion.
 */
@Component
class FactusFacturacionClienteAdapter implements FacturaDianTransmisorPort {

    private static final String BASE_URL_SANDBOX = "https://api-sandbox.factus.com.co";
    private static final String BASE_URL_PRODUCCION = "https://api.factus.com.co";
    private static final int TIMEOUT_MILLIS = 15_000;

    private static final String DOCUMENT_FACTURA = "01";
    private static final String OPERATION_TYPE_ESTANDAR = "10";
    private static final String PAYMENT_FORM_CONTADO = "1";
    private static final String TRIBUTE_CODE_DEFAULT = "ZZ";
    private static final String CASH_ROUNDING_FIJO = "0.00";
    private static final String DISCOUNT_RATE_FIJO = "0.00";
    private static final String UNIDAD_MEDIDA_UNICA = "94";
    private static final String CODIGO_ESTANDAR_ADOPCION = "999";

    private static final Logger log = LoggerFactory.getLogger(FactusFacturacionClienteAdapter.class);

    private final RestClient.Builder restClientBuilder;

    FactusFacturacionClienteAdapter(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public ResultadoTransmisionFactus transmitir(SolicitudTransmisionFactus solicitud, String clientId,
                                                   String clientSecret, String username, String password,
                                                   String ambiente) {
        String baseUrl = "produccion".equals(ambiente) ? BASE_URL_PRODUCCION : BASE_URL_SANDBOX;
        RestClient restClient = restClientBuilder.requestFactory(requestFactoryConTimeout()).build();

        String accessToken;
        try {
            accessToken = autenticar(restClient, baseUrl, clientId, clientSecret, username, password);
        } catch (RestClientException ex) {
            log.warn("No se pudo autenticar contra Factus ({}): {}", baseUrl, mensajeSeguro(ex));
            return fallo("No se pudo autenticar contra Factus");
        }

        try {
            FacturaBody body = construirBody(solicitud);
            JsonNode respuesta = restClient.post()
                    .uri(baseUrl + "/v2/bills/validate")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            return aResultado(respuesta);
        } catch (RestClientException ex) {
            log.warn("Fallo la llamada a Factus bills/validate ({}): {}", baseUrl, mensajeSeguro(ex));
            return fallo("Fallo la llamada a Factus bills/validate");
        } catch (RuntimeException ex) {
            log.warn("Respuesta inesperada de Factus bills/validate: {}", ex.getMessage());
            return fallo("Respuesta inesperada de Factus");
        }
    }

    private ResultadoTransmisionFactus aResultado(JsonNode respuesta) {
        JsonNode data = respuesta.get("data");
        String numero = data.get("number").asText();
        String cufe = data.get("cufe").asText();
        boolean validado = data.get("is_validated").asBoolean();
        JsonNode links = data.get("links");
        String qr = links != null && links.hasNonNull("qr") ? links.get("qr").asText() : null;
        return new ResultadoTransmisionFactus(true, numero, cufe, qr, validado, null);
    }

    private String mensajeSeguro(RestClientException ex) {
        return ex instanceof RestClientResponseException rex ? rex.getStatusCode().toString()
                : ex.getClass().getSimpleName();
    }

    private ResultadoTransmisionFactus fallo(String mensaje) {
        return new ResultadoTransmisionFactus(false, null, null, null, false, mensaje);
    }

    private ClientHttpRequestFactory requestFactoryConTimeout() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT_MILLIS);
        factory.setReadTimeout(TIMEOUT_MILLIS);
        return factory;
    }

    private String autenticar(RestClient restClient, String baseUrl, String clientId, String clientSecret,
                               String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", username);
        form.add("password", password);
        FactusTokenResponse token = restClient.post()
                .uri(baseUrl + "/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(FactusTokenResponse.class);
        return token.accessToken();
    }

    private FacturaBody construirBody(SolicitudTransmisionFactus solicitud) {
        ClienteTransmisionFactus c = solicitud.cliente();
        Customer customer = new Customer(c.identificationDocumentCode(), c.identification(),
                c.legalOrganizationCode(), TRIBUTE_CODE_DEFAULT, c.names(), c.company(), c.email());

        List<Item> items = solicitud.items().stream()
                .map(i -> new Item(i.codeReference(), i.nombre(), formatoMonto(i.cantidad()), DISCOUNT_RATE_FIJO,
                        formatoMonto(i.precio()), UNIDAD_MEDIDA_UNICA, CODIGO_ESTANDAR_ADOPCION,
                        List.of(new Tax(i.taxCode(), formatoMonto(i.taxRate())))))
                .toList();

        List<PaymentDetail> pagos = solicitud.pagos().stream()
                .map(p -> new PaymentDetail(PAYMENT_FORM_CONTADO, p.paymentMethodCode(), formatoMonto(p.monto())))
                .toList();

        return new FacturaBody(solicitud.referenceCode(), DOCUMENT_FACTURA, OPERATION_TYPE_ESTANDAR, pagos,
                CASH_ROUNDING_FIJO, customer, items);
    }

    private String formatoMonto(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /** expiresIn sin usar a proposito — no cacheamos el token, cada intento autentica de nuevo (bajo volumen esperado). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FactusTokenResponse(String accessToken, Integer expiresIn) {
    }

    private record FacturaBody(String referenceCode, String document, String operationType,
                                List<PaymentDetail> paymentDetails, String cashRoundingAmount, Customer customer,
                                List<Item> items) {
    }

    private record PaymentDetail(String paymentForm, String paymentMethodCode, String amount) {
    }

    private record Customer(String identificationDocumentCode, String identification, String legalOrganizationCode,
                             String tributeCode, String names, String company, String email) {
    }

    private record Item(String codeReference, String name, String quantity, String discountRate, String price,
                         String unitMeasureCode, String standardCode, List<Tax> taxes) {
    }

    private record Tax(String code, String rate) {
    }
}
