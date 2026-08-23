package com.cafepos.core.shared.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Fuerza escala 2 en la serializacion, sin importar la escala interna del
 * BigDecimal (que puede venir inflada por aritmetica en cadena — ej.
 * precioUnitario.multiply(cantidad) en operacion.PedidoItem.subtotal(),
 * donde cantidad es DECIMAL(12,3), da scale 5). Nunca aplicar esto a un
 * campo de CANTIDAD (stock, cantidad de pedido_item, etc.) — esos
 * necesitan su propia precision, no 2 decimales fijos. Ver anotacion Monto.
 */
public class MontoSerializer extends JsonSerializer<BigDecimal> {

    private static final int ESCALA_MONEDA = 2;

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP));
    }
}
