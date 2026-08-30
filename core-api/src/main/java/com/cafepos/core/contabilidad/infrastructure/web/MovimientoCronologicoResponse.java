package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.MovimientoCronologico;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovimientoCronologicoResponse(OffsetDateTime fecha, String descripcion, String tipo,
                                             @Monto BigDecimal monto, @Monto BigDecimal saldoAcumulado) {

    public static MovimientoCronologicoResponse de(MovimientoCronologico m) {
        return new MovimientoCronologicoResponse(m.fecha(), m.descripcion(), m.tipo(), m.monto(), m.saldoAcumulado());
    }
}
