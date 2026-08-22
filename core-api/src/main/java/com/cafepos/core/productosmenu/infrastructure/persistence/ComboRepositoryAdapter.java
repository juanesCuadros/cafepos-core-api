package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.Combo;
import com.cafepos.core.productosmenu.domain.ComboGrupo;
import com.cafepos.core.productosmenu.domain.ComboGrupoDetalle;
import com.cafepos.core.productosmenu.domain.ComboGrupoProducto;
import com.cafepos.core.productosmenu.domain.ComboGrupoProductoYaExisteException;
import com.cafepos.core.productosmenu.domain.ComboRepository;
import com.cafepos.core.productosmenu.domain.ComboResumen;
import com.cafepos.core.productosmenu.domain.ProductoRef;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
class ComboRepositoryAdapter implements ComboRepository {

    private final ComboJpaRepository comboJpaRepository;
    private final ComboGrupoJpaRepository comboGrupoJpaRepository;
    private final ComboGrupoProductoJpaRepository comboGrupoProductoJpaRepository;

    ComboRepositoryAdapter(ComboJpaRepository comboJpaRepository, ComboGrupoJpaRepository comboGrupoJpaRepository,
                           ComboGrupoProductoJpaRepository comboGrupoProductoJpaRepository) {
        this.comboJpaRepository = comboJpaRepository;
        this.comboGrupoJpaRepository = comboGrupoJpaRepository;
        this.comboGrupoProductoJpaRepository = comboGrupoProductoJpaRepository;
    }

    @Override
    public Combo guardar(Combo combo) {
        return comboJpaRepository.save(combo);
    }

    @Override
    public Optional<Combo> buscarPorId(Integer id) {
        return comboJpaRepository.findById(id);
    }

    @Override
    public List<ComboResumen> listar() {
        return comboJpaRepository.listar().stream()
                .map(row -> new ComboResumen(row.getId(), row.getCodigo(), row.getNombre(), row.getPrecio(),
                        row.getEstado()))
                .toList();
    }

    @Override
    public void eliminar(Combo combo) {
        comboJpaRepository.delete(combo);
    }

    @Override
    public List<ComboGrupoDetalle> gruposDe(Integer comboId) {
        Map<Integer, String> nombrePorGrupo = new LinkedHashMap<>();
        Map<Integer, List<ProductoRef>> productosPorGrupo = new LinkedHashMap<>();
        for (ComboGrupoProductoRow row : comboGrupoJpaRepository.gruposDe(comboId)) {
            nombrePorGrupo.putIfAbsent(row.getGrupoId(), row.getGrupoNombre());
            List<ProductoRef> productos = productosPorGrupo.computeIfAbsent(row.getGrupoId(), id -> new ArrayList<>());
            if (row.getProductoId() != null) {
                productos.add(new ProductoRef(row.getProductoId(), row.getProductoNombre()));
            }
        }
        return nombrePorGrupo.entrySet().stream()
                .map(entry -> new ComboGrupoDetalle(entry.getKey(), entry.getValue(),
                        productosPorGrupo.get(entry.getKey())))
                .toList();
    }

    @Override
    public ComboGrupo guardarGrupo(ComboGrupo grupo) {
        return comboGrupoJpaRepository.save(grupo);
    }

    @Override
    public Optional<ComboGrupo> buscarGrupo(Integer comboId, Integer grupoId) {
        return comboGrupoJpaRepository.findByIdAndComboId(grupoId, comboId);
    }

    @Override
    public void eliminarGrupo(ComboGrupo grupo) {
        comboGrupoJpaRepository.delete(grupo);
    }

    @Override
    public void agregarProducto(ComboGrupoProducto asociacion) {
        try {
            comboGrupoProductoJpaRepository.saveAndFlush(asociacion);
        } catch (DataIntegrityViolationException ex) {
            throw new ComboGrupoProductoYaExisteException();
        }
    }

    @Override
    public Optional<ComboGrupoProducto> buscarAsociacion(Integer comboGrupoId, Integer productoId) {
        return comboGrupoProductoJpaRepository.findByComboGrupoIdAndProductoId(comboGrupoId, productoId);
    }

    @Override
    public void quitarProducto(ComboGrupoProducto asociacion) {
        comboGrupoProductoJpaRepository.delete(asociacion);
    }
}
