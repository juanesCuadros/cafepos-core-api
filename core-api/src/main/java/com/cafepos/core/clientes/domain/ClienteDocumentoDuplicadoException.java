package com.cafepos.core.clientes.domain;

/**
 * POST/PATCH /clientes rechazado — (tenant_id, tipo_documento, numero_documento)
 * ya existe (UNIQUE de la tabla cliente). Se lanza al capturar
 * DataIntegrityViolationException en el adapter, no con un SELECT previo
 * (mismo patron que ComboGrupoProductoYaExisteException en productosmenu).
 */
public class ClienteDocumentoDuplicadoException extends RuntimeException {

    public ClienteDocumentoDuplicadoException() {
        super("Ya existe un cliente con este documento");
    }
}
