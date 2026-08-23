package com.cafepos.core.restaurante.application;

import com.cafepos.core.restaurante.domain.Mesa;
import com.cafepos.core.restaurante.domain.MesaNoEncontradaException;
import com.cafepos.core.restaurante.domain.MesaRepository;
import com.cafepos.core.restaurante.domain.MesaResumen;
import com.cafepos.core.restaurante.domain.Zona;
import com.cafepos.core.restaurante.domain.ZonaConMesasException;
import com.cafepos.core.restaurante.domain.ZonaNoEncontradaException;
import com.cafepos.core.restaurante.domain.ZonaRepository;
import com.cafepos.core.restaurante.domain.ZonaResumen;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Zona y Mesa comparten un solo service a proposito: comparten el mismo
 * permiso de catalogo (restaurante.zonas_mesas, mesa no tiene entrada
 * propia) y una mesa siempre vive dentro de una zona — separarlos en dos
 * services no ganaria independencia real.
 */
@Service
public class ZonaService {

    private static final String PREFIJO_CODIGO_ZONA = "Z";
    private static final String PREFIJO_CODIGO_MESA = "M";
    private static final int PADDING_CODIGO = 3;

    private final ZonaRepository zonaRepository;
    private final MesaRepository mesaRepository;

    public ZonaService(ZonaRepository zonaRepository, MesaRepository mesaRepository) {
        this.zonaRepository = zonaRepository;
        this.mesaRepository = mesaRepository;
    }

    @Transactional(readOnly = true)
    public List<ZonaResumen> listar() {
        return zonaRepository.listarConNumMesas();
    }

    @Transactional(readOnly = true)
    public Zona buscarPorId(Integer id) {
        return zonaRepository.buscarPorId(id).orElseThrow(ZonaNoEncontradaException::new);
    }

    @Transactional
    public Zona crear(String icono, String nombre, String estado) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Zona zona = new Zona(tenantId, icono, nombre, estado);
        zona = zonaRepository.guardar(zona);
        zona.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO_ZONA, zona.getId(), PADDING_CODIGO));
        return zonaRepository.guardar(zona);
    }

    @Transactional
    public Zona actualizar(Integer id, JsonNullable<String> icono, String nombre, String estado) {
        Zona zona = buscarPorId(id);
        zona.actualizar(icono, nombre, estado);
        return zonaRepository.guardar(zona);
    }

    /** Cuenta mesas ANTES de borrar — mismo patron que CategoriaService.eliminar vs Producto. */
    @Transactional
    public void eliminar(Integer id) {
        Zona zona = buscarPorId(id);
        long numMesas = zonaRepository.contarMesas(id);
        if (numMesas > 0) {
            throw new ZonaConMesasException(numMesas);
        }
        zonaRepository.eliminar(zona);
    }

    @Transactional(readOnly = true)
    public List<MesaResumen> listarMesas(Integer zonaId) {
        buscarPorId(zonaId);
        return mesaRepository.listarDeZona(zonaId);
    }

    @Transactional
    public Mesa crearMesa(Integer zonaId, String numero, Integer capacidad, String estado) {
        Zona zona = buscarPorId(zonaId);
        Mesa mesa = new Mesa(zona.getTenantId(), zona.getId(), numero, capacidad, estado);
        mesa = mesaRepository.guardar(mesa);
        mesa.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO_MESA, mesa.getId(), PADDING_CODIGO));
        return mesaRepository.guardar(mesa);
    }

    @Transactional(readOnly = true)
    public Mesa buscarMesaPorId(Integer id) {
        return mesaRepository.buscarPorId(id).orElseThrow(MesaNoEncontradaException::new);
    }

    @Transactional
    public Mesa actualizarMesa(Integer id, Integer zonaId, String numero, Integer capacidad, String estado) {
        Mesa mesa = buscarMesaPorId(id);
        mesa.actualizar(zonaId, numero, capacidad, estado);
        return mesaRepository.guardar(mesa);
    }

    @Transactional
    public void eliminarMesa(Integer id) {
        mesaRepository.eliminar(buscarMesaPorId(id));
    }
}
