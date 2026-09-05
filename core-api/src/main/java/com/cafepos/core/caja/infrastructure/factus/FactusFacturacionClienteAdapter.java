package com.cafepos.core.caja.infrastructure.factus;

import com.cafepos.core.caja.domain.ClienteTransmisionFactus;
import com.cafepos.core.caja.domain.FacturaDianTransmisorPort;
import com.cafepos.core.caja.domain.ItemTransmisionFactus;
import com.cafepos.core.caja.domain.PagoTransmisionFactus;
import com.cafepos.core.caja.domain.ResultadoEnvioCorreoFactus;
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
    /** Tope del cuerpo de error que se loguea — suficiente para el detalle de validacion de Factus sin inundar el log. */
    private static final int MAX_DETALLE_ERROR = 1_000;

    private static final String DOCUMENT_FACTURA = "01";
    private static final String OPERATION_TYPE_ESTANDAR = "10";
    private static final String PAYMENT_FORM_CONTADO = "1";
    private static final String TRIBUTE_CODE_DEFAULT = "ZZ";
    private static final String CASH_ROUNDING_FIJO = "0.00";
    private static final String UNIDAD_MEDIDA_UNICA = "94";
    private static final String CODIGO_ESTANDAR_ADOPCION = "999";
    /** "Recargo condicionado" - unico concept_type de recargo disponible en la tabla de referencia de Factus, ver tablas-de-referencia. */
    private static final String CONCEPT_TYPE_RECARGO = "03";
    private static final String RAZON_PROPINA = "propina";

    private static final Logger log = LoggerFactory.getLogger(FactusFacturacionClienteAdapter.class);

    private final RestClient.Builder restClientBuilder;

    FactusFacturacionClienteAdapter(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public ResultadoTransmisionFactus transmitir(SolicitudTransmisionFactus solicitud, String clientId,
                                                   String clientSecret, String username, String password,
                                                   String ambiente, Long numberingRangeId) {
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
            FacturaBody body = construirBody(solicitud, numberingRangeId);
            JsonNode respuesta = restClient.post()
                    .uri(baseUrl + "/v2/bills/validate")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            return aResultado(respuesta);
        } catch (RestClientException ex) {
            // A DIFERENCIA del catch de autenticacion, aca SI se loguea el
            // cuerpo de la respuesta: un 422 de bills/validate trae el detalle
            // de que campo rechazo Factus, y sin eso no hay forma de depurar
            // la integracion (confirmado en vivo 02-sep-2026: la primera
            // factura real fallo con 422 por falta de numbering_range_id en
            // el body, y el log solo decia el status, no el motivo real - ver
            // V33, ya se manda el campo). El cuerpo de ESTE endpoint son
            // errores de validacion de
            // la factura, nunca credenciales — esas solo viajan a /oauth/token.
            String detalle = detalleValidacion(ex);
            log.warn("Fallo la llamada a Factus bills/validate ({}): {}", baseUrl, detalle);
            return fallo("Factus rechazo la factura: " + detalle);
        } catch (RuntimeException ex) {
            log.warn("Respuesta inesperada de Factus bills/validate: {}", ex.getMessage());
            return fallo("Respuesta inesperada de Factus");
        }
    }

    @Override
    public ResultadoEnvioCorreoFactus enviarCorreo(String numeroFactura, String email, String clientId,
                                                     String clientSecret, String username, String password,
                                                     String ambiente) {
        String baseUrl = "produccion".equals(ambiente) ? BASE_URL_PRODUCCION : BASE_URL_SANDBOX;
        RestClient restClient = restClientBuilder.requestFactory(requestFactoryConTimeout()).build();

        String accessToken;
        try {
            accessToken = autenticar(restClient, baseUrl, clientId, clientSecret, username, password);
        } catch (RestClientException ex) {
            log.warn("No se pudo autenticar contra Factus para reenviar correo ({}): {}", baseUrl, mensajeSeguro(ex));
            return new ResultadoEnvioCorreoFactus(false, "No se pudo autenticar contra Factus");
        }

        try {
            // toBodilessEntity: la respuesta 200 de este endpoint trae el
            // adjunto (representacion grafica en zip), no un JSON de
            // confirmacion - no hace falta parsearla, solo confirmar el
            // status HTTP.
            restClient.post()
                    .uri(baseUrl + "/v2/bills/{numero}/send-email", numeroFactura)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .body(new EnviarCorreoBody(email))
                    .retrieve()
                    .toBodilessEntity();
            return new ResultadoEnvioCorreoFactus(true, null);
        } catch (RestClientException ex) {
            String detalle = detalleValidacion(ex);
            log.warn("Fallo al reenviar correo de factura {} via Factus ({}): {}", numeroFactura, baseUrl, detalle);
            return new ResultadoEnvioCorreoFactus(false, "Factus no pudo enviar el correo: " + detalle);
        } catch (RuntimeException ex) {
            log.warn("Respuesta inesperada de Factus send-email: {}", ex.getMessage());
            return new ResultadoEnvioCorreoFactus(false, "Respuesta inesperada de Factus");
        }
    }

    /** Status + cuerpo recortado — el detalle real de por que Factus rechazo la factura. */
    private String detalleValidacion(RestClientException ex) {
        if (!(ex instanceof RestClientResponseException rex)) {
            return ex.getClass().getSimpleName();
        }
        String cuerpo = rex.getResponseBodyAsString();
        if (cuerpo.length() > MAX_DETALLE_ERROR) {
            cuerpo = cuerpo.substring(0, MAX_DETALLE_ERROR) + "...(truncado)";
        }
        return rex.getStatusCode() + " " + cuerpo;
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

    private FacturaBody construirBody(SolicitudTransmisionFactus solicitud, Long numberingRangeId) {
        ClienteTransmisionFactus c = solicitud.cliente();
        Customer customer = new Customer(c.identificationDocumentCode(), c.identification(),
                c.legalOrganizationCode(), TRIBUTE_CODE_DEFAULT, c.names(), c.company(), c.email());

        String discountRate = formatoMonto(solicitud.descuentoRatePercent());
        List<Item> items = solicitud.items().stream()
                .map(i -> new Item(i.codeReference(), i.nombre(), formatoMonto(i.cantidad()), discountRate,
                        formatoMonto(i.precio()), UNIDAD_MEDIDA_UNICA, CODIGO_ESTANDAR_ADOPCION,
                        List.of(new Tax(i.taxCode(), formatoMonto(i.taxRate())))))
                .toList();

        List<PaymentDetail> pagos = solicitud.pagos().stream()
                .map(p -> new PaymentDetail(PAYMENT_FORM_CONTADO, p.paymentMethodCode(), formatoMonto(p.monto())))
                .toList();

        return new FacturaBody(numberingRangeId, solicitud.referenceCode(), DOCUMENT_FACTURA, OPERATION_TYPE_ESTANDAR,
                pagos, CASH_ROUNDING_FIJO, customer, items, construirAllowanceCharges(solicitud));
    }

    /**
     * Propina como recargo a nivel de factura — Factus la contempla via
     * allowance_charges (concept_type "03" = "Recargo condicionado", unico
     * codigo de recargo disponible en su tabla de referencia), no como item
     * de la venta ni excluida del todo (ver Javadoc de SolicitudTransmisionFactus).
     * Lista vacia si no hubo propina: Factus no exige el campo.
     */
    private List<AllowanceCharge> construirAllowanceCharges(SolicitudTransmisionFactus solicitud) {
        if (solicitud.propina() == null || solicitud.propina().signum() <= 0) {
            return List.of();
        }
        return List.of(new AllowanceCharge(CONCEPT_TYPE_RECARGO, true, RAZON_PROPINA,
                formatoMonto(solicitud.baseImponiblePropina()), formatoMonto(solicitud.propina())));
    }

    private String formatoMonto(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /** expiresIn sin usar a proposito — no cacheamos el token, cada intento autentica de nuevo (bajo volumen esperado). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FactusTokenResponse(String accessToken, Integer expiresIn) {
    }

    /** numberingRangeId: ver Javadoc de la clase - Factus rechaza con 422 sin este campo (confirmado en vivo 02-sep-2026). */
    private record FacturaBody(Long numberingRangeId, String referenceCode, String document, String operationType,
                                List<PaymentDetail> paymentDetails, String cashRoundingAmount, Customer customer,
                                List<Item> items, List<AllowanceCharge> allowanceCharges) {
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

    /** Ver construirAllowanceCharges — hoy solo se usa para propina, pero el shape es generico (Factus lo permite para cualquier recargo/descuento de factura). */
    private record AllowanceCharge(String conceptType, boolean isSurcharge, String reason, String baseAmount,
                                    String amount) {
    }

    private record EnviarCorreoBody(String email) {
    }
}
