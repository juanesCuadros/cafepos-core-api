package com.cafepos.core.clientes.domain;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Cliente — implementado en infrastructure.persistence. */
public interface ClienteRepository {

    /** Lanza ClienteDocumentoDuplicadoException si (tenant_id, tipo_documento, numero_documento) ya existe. */
    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(Integer id);

    List<ClienteResumen> listar(String q);

    List<ClienteBusqueda> buscarLiviano(String q);

    /** join venta — usado por PATCH (bloqueo de cambio de documento) y DELETE. */
    boolean tieneVentasAsociadas(Integer clienteId);

    void eliminar(Cliente cliente);

    List<CompraHistorial> historialDe(Integer clienteId);

    List<SaldoMovimientoItem> movimientosDe(Integer clienteId);

    ClienteSaldoMovimiento guardarMovimientoSaldo(ClienteSaldoMovimiento movimiento);
}
