package com.cafepos.core.clientes.application;

import com.cafepos.core.clientes.domain.Cliente;
import com.cafepos.core.clientes.domain.ClienteBusqueda;
import com.cafepos.core.clientes.domain.ClienteConVentasException;
import com.cafepos.core.clientes.domain.ClienteNoEncontradoException;
import com.cafepos.core.clientes.domain.ClienteNoEliminableException;
import com.cafepos.core.clientes.domain.ClienteRepository;
import com.cafepos.core.clientes.domain.ClienteResumen;
import com.cafepos.core.clientes.domain.CompraHistorial;
import com.cafepos.core.clientes.domain.SaldoMovimientosVista;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ClienteService {

    private static final String PREFIJO_CODIGO = "CLI";

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResumen> listar(String q) {
        return clienteRepository.listar(q);
    }

    @Transactional(readOnly = true)
    public List<ClienteBusqueda> buscarLiviano(String q) {
        return clienteRepository.buscarLiviano(q);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Integer id) {
        return clienteRepository.buscarPorId(id).orElseThrow(ClienteNoEncontradoException::new);
    }

    @Transactional
    public Cliente crear(String tipoDocumento, String numeroDocumento, String nombre, String telefono,
                          String correo, String direccion) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Cliente cliente = new Cliente(tenantId, tipoDocumento, numeroDocumento, nombre, telefono, correo, direccion);
        cliente = clienteRepository.guardar(cliente);
        cliente.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO, cliente.getId()));
        return clienteRepository.guardar(cliente);
    }

    /**
     * Si el body intenta cambiar tipoDocumento o numeroDocumento (cualquiera
     * de los dos no-null, sin importar si el valor nuevo coincide con el
     * actual) y el cliente ya tiene ventas, se rechaza ANTES de tocar
     * cualquier otro campo — todo o nada, el llamador debe reintentar sin
     * esos dos campos si quiere actualizar el resto.
     */
    @Transactional
    public Cliente actualizar(Integer id, String nombre, String tipoDocumento, String numeroDocumento,
                               JsonNullable<String> telefono, JsonNullable<String> correo,
                               JsonNullable<String> direccion) {
        Cliente cliente = buscarPorId(id);
        boolean intentaCambiarDocumento = tipoDocumento != null || numeroDocumento != null;
        if (intentaCambiarDocumento && clienteRepository.tieneVentasAsociadas(id)) {
            throw new ClienteConVentasException();
        }
        cliente.actualizar(nombre, tipoDocumento, numeroDocumento, telefono, correo, direccion);
        return clienteRepository.guardar(cliente);
    }

    /** DELETE fisico simple — clientes no tiene concepto de soft-delete en el contrato. */
    @Transactional
    public void eliminar(Integer id) {
        Cliente cliente = buscarPorId(id);
        boolean tieneSaldoFavor = cliente.getSaldoFavor().compareTo(BigDecimal.ZERO) > 0;
        if (clienteRepository.tieneVentasAsociadas(id) || tieneSaldoFavor) {
            throw new ClienteNoEliminableException();
        }
        clienteRepository.eliminar(cliente);
    }

    @Transactional(readOnly = true)
    public List<CompraHistorial> historialDe(Integer id) {
        buscarPorId(id);
        return clienteRepository.historialDe(id);
    }

    @Transactional(readOnly = true)
    public SaldoMovimientosVista saldoMovimientosDe(Integer id) {
        Cliente cliente = buscarPorId(id);
        return new SaldoMovimientosVista(cliente.getSaldoFavor(), clienteRepository.movimientosDe(id));
    }
}
