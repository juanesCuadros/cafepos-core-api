package com.cafepos.core.clientes.infrastructure.persistence;

import com.cafepos.core.clientes.domain.Cliente;
import com.cafepos.core.clientes.domain.ClienteBusqueda;
import com.cafepos.core.clientes.domain.ClienteDocumentoDuplicadoException;
import com.cafepos.core.clientes.domain.ClienteRepository;
import com.cafepos.core.clientes.domain.ClienteResumen;
import com.cafepos.core.clientes.domain.ClienteSaldoMovimiento;
import com.cafepos.core.clientes.domain.CompraHistorial;
import com.cafepos.core.clientes.domain.MascaraDocumento;
import com.cafepos.core.clientes.domain.SaldoMovimientoItem;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;
    private final ClienteSaldoMovimientoJpaRepository saldoMovimientoJpaRepository;

    ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository,
                              ClienteSaldoMovimientoJpaRepository saldoMovimientoJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.saldoMovimientoJpaRepository = saldoMovimientoJpaRepository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        try {
            return jpaRepository.saveAndFlush(cliente);
        } catch (DataIntegrityViolationException ex) {
            throw new ClienteDocumentoDuplicadoException();
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ClienteResumen> listar(String q) {
        return jpaRepository.listar(q).stream()
                .map(row -> new ClienteResumen(row.getId(), row.getCodigo(), row.getNombre(),
                        row.getTipoDocumento(), MascaraDocumento.enmascarar(row.getNumeroDocumento()),
                        row.getTelefono(), row.getCorreo(), row.getSaldoFavor()))
                .toList();
    }

    @Override
    public List<ClienteBusqueda> buscarLiviano(String q) {
        return jpaRepository.buscarLiviano(q).stream()
                .map(row -> new ClienteBusqueda(row.getId(), row.getNombre(), row.getTipoDocumento(),
                        MascaraDocumento.enmascarar(row.getNumeroDocumento()), row.getSaldoFavor()))
                .toList();
    }

    @Override
    public boolean tieneVentasAsociadas(Integer clienteId) {
        return jpaRepository.tieneVentasAsociadas(clienteId);
    }

    @Override
    public void eliminar(Cliente cliente) {
        jpaRepository.delete(cliente);
    }

    @Override
    public List<CompraHistorial> historialDe(Integer clienteId) {
        return jpaRepository.historialDe(clienteId).stream()
                .map(row -> new CompraHistorial(row.getVentaId(), row.getFechaHora().atOffset(ZoneOffset.UTC),
                        row.getTotal(), row.getFacturaNumero(), row.getEstado()))
                .toList();
    }

    @Override
    public List<SaldoMovimientoItem> movimientosDe(Integer clienteId) {
        return jpaRepository.movimientosDe(clienteId).stream()
                .map(row -> new SaldoMovimientoItem(row.getId(), row.getTipo(), row.getMonto(),
                        row.getOrigenTipo(), row.getOrigenId(), row.getFecha().atOffset(ZoneOffset.UTC),
                        row.getDescripcion()))
                .toList();
    }

    @Override
    public ClienteSaldoMovimiento guardarMovimientoSaldo(ClienteSaldoMovimiento movimiento) {
        return saldoMovimientoJpaRepository.save(movimiento);
    }
}
