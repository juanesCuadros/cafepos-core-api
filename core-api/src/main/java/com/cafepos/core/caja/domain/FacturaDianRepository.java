package com.cafepos.core.caja.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de FacturaDian (propia de este modulo) — implementado en infrastructure.persistence. */
public interface FacturaDianRepository {

    FacturaDian guardar(FacturaDian factura);

    Optional<FacturaDian> buscarPorVentaId(Integer ventaId);

    Optional<FacturaDian> buscarPorId(Integer id);

    /** Cualquiera de los filtros puede venir null (sin filtrar por ese campo) — ver FacturacionService. */
    List<FacturaListadoItem> listar(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String estadoDian,
                                     String cliente, String numeroFactura);
}
