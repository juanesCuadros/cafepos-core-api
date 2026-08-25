package com.cafepos.core.shared.impuestos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IVA/INC — logica compartida entre com.cafepos.core.caja.application.VentaService
 * (calcula impuestos al cobrar) y com.cafepos.core.caja.application.FacturaDianTransmisionService
 * (arma taxes[] para Factus, que ademas necesita saber si el porcentaje
 * vino de un "IVA" explicito o del default INC del tenant — ver esIva).
 * Vive en shared porque ambos son casos de uso del mismo modulo caja, pero
 * la logica en si no depende de ningun tipo de dominio de caja.
 */
public final class ResolverTasaImpuesto {

    private static final String TASA_EXENTO = "Exento";
    private static final Pattern PATRON_TASA_PORCENTAJE = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*%");

    private static final Logger log = LoggerFactory.getLogger(ResolverTasaImpuesto.class);

    private ResolverTasaImpuesto() {
    }

    /**
     * "Exento" -> 0%. null -> default del tenant. Texto no parseable ->
     * default del tenant + WARN (nunca falla la venta por esto).
     */
    public static BigDecimal tasa(String tasaImpuestoTexto, BigDecimal defaultIncPorcentaje) {
        if (tasaImpuestoTexto == null) {
            return defaultIncPorcentaje;
        }
        if (TASA_EXENTO.equalsIgnoreCase(tasaImpuestoTexto.trim())) {
            return BigDecimal.ZERO;
        }
        Matcher matcher = PATRON_TASA_PORCENTAJE.matcher(tasaImpuestoTexto);
        if (matcher.find()) {
            return new BigDecimal(matcher.group(1).replace(',', '.'));
        }
        log.warn("producto.tasa_impuesto con formato inesperado: '{}' - usando el default del tenant ({}%)",
                tasaImpuestoTexto, defaultIncPorcentaje);
        return defaultIncPorcentaje;
    }

    /**
     * true si el producto trae un tasa_impuesto EXPLICITO que menciona IVA
     * (code Factus "01") — false para default del tenant/Exento/INC/texto
     * no parseable, todos ellos van con code Factus "04" (INC), ver
     * DECISIONES YA TOMADAS de la conversacion Factus real.
     */
    public static boolean esIva(String tasaImpuestoTexto) {
        return tasaImpuestoTexto != null && tasaImpuestoTexto.toUpperCase(Locale.ROOT).contains("IVA");
    }
}
