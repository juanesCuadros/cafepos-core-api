package com.cafepos.core.personal.application;

import com.cafepos.core.personal.domain.Empleado;
import com.cafepos.core.personal.domain.EmpleadoNoEncontradoException;
import com.cafepos.core.personal.domain.EmpleadoRepository;
import com.cafepos.core.personal.domain.EmpleadoResumen;
import com.cafepos.core.shared.codigo.GeneradorCodigo;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class EmpleadoService {

    private static final String PREFIJO_CODIGO = "EMP";

    private final EmpleadoRepository empleadoRepository;
    private final PropinaCalculoService propinaCalculoService;

    public EmpleadoService(EmpleadoRepository empleadoRepository, PropinaCalculoService propinaCalculoService) {
        this.empleadoRepository = empleadoRepository;
        this.propinaCalculoService = propinaCalculoService;
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResumen> listar(String cargo, String estado, String q) {
        return empleadoRepository.listar(cargo, estado, q);
    }

    @Transactional(readOnly = true)
    public Empleado buscarPorId(Integer id) {
        return empleadoRepository.buscarPorId(id).orElseThrow(EmpleadoNoEncontradoException::new);
    }

    @Transactional(readOnly = true)
    public EmpleadoDetalleVista detalle(Integer id) {
        Empleado empleado = buscarPorId(id);
        var usuarioAsociado = empleadoRepository.buscarUsuarioAsociado(id).orElse(null);
        var resumenTurnos = empleadoRepository.resumenTurnosMesActual(id);
        YearMonth mesActual = YearMonth.now();
        ResumenPropinas propinas = propinaCalculoService.calcular(id, mesActual.atDay(1), mesActual.atEndOfMonth());
        return new EmpleadoDetalleVista(empleado, usuarioAsociado, resumenTurnos, propinas.totalPropinas());
    }

    @Transactional(readOnly = true)
    public ResumenPropinas propinas(Integer id, LocalDate fechaInicio, LocalDate fechaFin) {
        buscarPorId(id);
        return propinaCalculoService.calcular(id, fechaInicio, fechaFin);
    }

    /** 409 (CedulaDuplicadaException) via DataIntegrityViolationException en el adapter — sin SELECT previo, ver DECISIONES YA TOMADAS. */
    @Transactional
    public Empleado crear(String nombre, String cedula, String cargo, String telefono, String estado) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Empleado empleado = new Empleado(tenantId, nombre, cedula, cargo, telefono, estado);
        empleado = empleadoRepository.guardar(empleado);
        empleado.asignarCodigo(GeneradorCodigo.generar(PREFIJO_CODIGO, empleado.getId()));
        return empleadoRepository.guardar(empleado);
    }

    @Transactional
    public Empleado actualizar(Integer id, String nombre, String cedula, String cargo,
                                JsonNullable<String> telefono, String estado) {
        Empleado empleado = buscarPorId(id);
        empleado.actualizar(nombre, cedula, cargo, telefono, estado);
        return empleadoRepository.guardar(empleado);
    }

    @Transactional
    public void eliminar(Integer id) {
        Empleado empleado = buscarPorId(id);
        empleadoRepository.eliminar(empleado);
    }
}
