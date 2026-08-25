package com.cafepos.core.clientes.application;

import com.cafepos.core.clientes.domain.Cliente;
import com.cafepos.core.clientes.domain.ClienteBusqueda;
import com.cafepos.core.clientes.domain.ClienteConVentasException;
import com.cafepos.core.clientes.domain.ClienteNoEncontradoException;
import com.cafepos.core.clientes.domain.ClienteNoEliminableException;
import com.cafepos.core.clientes.domain.ClienteParaFactura;
import com.cafepos.core.clientes.domain.ClienteParaFacturaDian;
import com.cafepos.core.clientes.domain.ClienteRef;
import com.cafepos.core.clientes.domain.ClienteRepository;
import com.cafepos.core.clientes.domain.ClienteResumen;
import com.cafepos.core.clientes.domain.ClienteSaldoMovimiento;
import com.cafepos.core.clientes.domain.CompraHistorial;
import com.cafepos.core.clientes.domain.SaldoMovimientosVista;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.caja
 * valide cliente_id y arme el "cliente" de la respuesta al cobrar (ver
 * buscarParaVenta) — solo cruza ClienteRef (tambien anotado), nunca la
 * entidad Cliente completa.
 */
@org.springframework.modulith.NamedInterface("clienteService")
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

    /** API publica de este modulo para validar cliente_id al cobrar (com.cafepos.core.caja). */
    @Transactional(readOnly = true)
    public Optional<ClienteRef> buscarParaVenta(Integer id) {
        return clienteRepository.buscarPorId(id).map(c -> new ClienteRef(c.getId(), c.getNombre()));
    }

    /** API publica de este modulo para el detalle/reenvio de facturas DIAN (com.cafepos.core.caja). */
    @Transactional(readOnly = true)
    public Optional<ClienteParaFactura> buscarParaFactura(Integer id) {
        return clienteRepository.buscarPorId(id)
                .map(c -> new ClienteParaFactura(c.getId(), c.getNombre(), c.getNumeroDocumentoEnmascarado(),
                        c.getCorreo()));
    }

    /**
     * API publica de este modulo EXCLUSIVA para transmitir una factura DIAN
     * real a Factus (com.cafepos.core.caja.application.FacturaDianTransmisionService)
     * — numero_documento SIN mascara, Factus lo exige tal cual (ver
     * ClienteParaFacturaDian). Ningun otro caller deberia necesitar el
     * documento sin enmascarar.
     */
    @Transactional(readOnly = true)
    public Optional<ClienteParaFacturaDian> buscarParaFacturaDian(Integer id) {
        return clienteRepository.buscarPorId(id)
                .map(c -> new ClienteParaFacturaDian(c.getId(), c.getTipoDocumento(), c.getNumeroDocumento(),
                        c.getNombre(), c.getCorreo()));
    }

    /**
     * API publica de este modulo para acreditar saldo a favor en una
     * devolucion donde el item ya se habia preparado (com.cafepos.core.caja,
     * ver RN-023/024 en api_03_caja.md) — incrementa cliente.saldo_favor Y
     * deja el rastro en cliente_saldo_movimiento en la misma transaccion
     * (el caller, VentaService/DevolucionService, decide si hace rollback
     * de todo si algo mas falla despues).
     */
    @Transactional
    public void acreditarSaldoFavorPorDevolucion(Integer clienteId, BigDecimal monto, Integer devolucionId,
                                                  Integer usuarioAutorizaId) {
        Cliente cliente = buscarPorId(clienteId);
        cliente.acreditarSaldoFavor(monto);
        clienteRepository.guardar(cliente);

        Integer tenantId = TenantContext.getCurrentTenantId();
        clienteRepository.guardarMovimientoSaldo(new ClienteSaldoMovimiento(tenantId, clienteId, usuarioAutorizaId,
                ClienteSaldoMovimiento.TIPO_CREDITO, monto, "devolucion", devolucionId,
                "Devolución de ítem ya preparado"));
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
