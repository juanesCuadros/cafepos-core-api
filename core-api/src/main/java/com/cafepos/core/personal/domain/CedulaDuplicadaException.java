package com.cafepos.core.personal.domain;

/** 409 — cedula ya existe para el tenant (UNIQUE(tenant_id, cedula), detectado via DataIntegrityViolationException). */
public class CedulaDuplicadaException extends RuntimeException {

    public CedulaDuplicadaException() {
        super("Ya existe un empleado con esa cedula");
    }
}
