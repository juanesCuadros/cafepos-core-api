package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.AnularFacturaResultado;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record AnularFacturaResponse(Integer notaCreditoId, Integer facturaId, @Monto BigDecimal monto,
                                     String mensaje) {

    public static AnularFacturaResponse de(AnularFacturaResultado resultado) {
        return new AnularFacturaResponse(resultado.notaCreditoId(), resultado.facturaId(), resultado.monto(),
                "Nota credito generada y transmitida a la DIAN");
    }
}
