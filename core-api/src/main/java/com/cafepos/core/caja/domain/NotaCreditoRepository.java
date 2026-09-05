package com.cafepos.core.caja.domain;

/** Puerto de persistencia de NotaCredito — implementado en infrastructure.persistence. */
public interface NotaCreditoRepository {

    NotaCredito guardar(NotaCredito notaCredito);

    /** true si esta factura ya tiene AL MENOS una nota_credito generada (por devolución o por anulación directa). */
    boolean existePorFacturaId(Integer facturaId);
}
