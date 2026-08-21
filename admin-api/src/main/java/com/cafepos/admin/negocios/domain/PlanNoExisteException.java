package com.cafepos.admin.negocios.domain;

public class PlanNoExisteException extends RuntimeException {

    public PlanNoExisteException() {
        super("El plan seleccionado no existe");
    }
}
