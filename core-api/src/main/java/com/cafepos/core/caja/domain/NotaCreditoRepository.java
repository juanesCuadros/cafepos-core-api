package com.cafepos.core.caja.domain;

/** Puerto de persistencia de NotaCredito — implementado en infrastructure.persistence. */
public interface NotaCreditoRepository {

    NotaCredito guardar(NotaCredito notaCredito);
}
