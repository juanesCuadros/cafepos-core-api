package com.cafepos.core.inventario.application;

import com.cafepos.core.inventario.domain.LoteVencimiento;
import com.cafepos.core.inventario.domain.VencimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Solo lectura — lote_insumo hoy esta vacia en la practica (se llena
 * desde Compras, que no existe todavia); el endpoint funciona igual y
 * simplemente devuelve una lista vacia hasta que ese modulo exista.
 */
@Service
public class VencimientoService {

    private final VencimientoRepository vencimientoRepository;

    public VencimientoService(VencimientoRepository vencimientoRepository) {
        this.vencimientoRepository = vencimientoRepository;
    }

    @Transactional(readOnly = true)
    public List<LoteVencimiento> listar(String estado, Integer categoriaInsumoId) {
        return vencimientoRepository.listar(estado, categoriaInsumoId);
    }
}
