package com.cafepos.core.compras.application;

import com.cafepos.core.compras.domain.Proveedor;
import com.cafepos.core.compras.domain.ProveedorConComprasException;
import com.cafepos.core.compras.domain.ProveedorNoEncontradoException;
import com.cafepos.core.compras.domain.ProveedorRepository;
import com.cafepos.core.compras.domain.ProveedorResumen;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProveedorService {

    private static final String PREFIJO_CODIGO = "PROV";

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional(readOnly = true)
    public List<ProveedorResumen> listar(String estado, String q) {
        return proveedorRepository.listar(estado, q);
    }

    @Transactional(readOnly = true)
    public Proveedor buscarPorId(Integer id) {
        return proveedorRepository.buscarPorId(id).orElseThrow(ProveedorNoEncontradoException::new);
    }

    @Transactional
    public Proveedor crear(String nombre, String nit, String contacto, String telefono, String correo,
                            String direccion, String estado) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Proveedor proveedor = new Proveedor(tenantId, nombre, nit, contacto, telefono, correo, direccion, estado);
        proveedor = proveedorRepository.guardar(proveedor);
        proveedor.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO, proveedor.getId()));
        return proveedorRepository.guardar(proveedor);
    }

    @Transactional
    public Proveedor actualizar(Integer id, String nombre, String nit, JsonNullable<String> contacto,
                                 JsonNullable<String> telefono, JsonNullable<String> correo,
                                 JsonNullable<String> direccion, String estado) {
        Proveedor proveedor = buscarPorId(id);
        proveedor.actualizar(nombre, nit, contacto, telefono, correo, direccion, estado);
        return proveedorRepository.guardar(proveedor);
    }

    @Transactional
    public void eliminar(Integer id) {
        Proveedor proveedor = buscarPorId(id);
        if (proveedorRepository.tieneComprasAsociadas(id)) {
            throw new ProveedorConComprasException();
        }
        proveedorRepository.eliminar(proveedor);
    }
}
