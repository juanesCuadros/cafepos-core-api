package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.PrefacturaResultado;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record PrefacturaResponse(String numeroOrden, String mesa, List<PrefacturaItemResponse> items,
                                  @Monto BigDecimal subtotal, OffsetDateTime generadoEn) {

    public static PrefacturaResponse de(PrefacturaResultado resultado) {
        return new PrefacturaResponse(resultado.numeroOrden(), resultado.mesaNumero(),
                resultado.items().stream().map(PrefacturaItemResponse::de).toList(), resultado.subtotal(),
                resultado.generadoEn());
    }
}
