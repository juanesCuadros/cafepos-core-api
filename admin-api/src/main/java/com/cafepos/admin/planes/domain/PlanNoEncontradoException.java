package com.cafepos.admin.planes.domain;

/** El plan no existe para operar sobre el (editar, cambiar estado) — distinto de negocios.PlanNoExisteException. */
public class PlanNoEncontradoException extends RuntimeException {

    public PlanNoEncontradoException() {
        super("El plan no existe");
    }
}
