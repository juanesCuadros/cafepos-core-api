package com.cafepos.core.personal.application;

import com.cafepos.core.personal.domain.EmpleadoNoEncontradoException;
import com.cafepos.core.personal.domain.EmpleadoRepository;
import com.cafepos.core.personal.domain.HoraFinAntesDeInicioException;
import com.cafepos.core.personal.domain.Turno;
import com.cafepos.core.personal.domain.TurnoNoEncontradoException;
import com.cafepos.core.personal.domain.TurnoRepository;
import com.cafepos.core.personal.domain.TurnoResumen;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Gestion Admin/Jefe de turnos (8.2) — distinta del autoregistro del
 * empleado (ver operacion.application.TurnoService, misma tabla).
 * Bean nombrado explicitamente "personalTurnoService": el nombre de clase
 * simple coincide con operacion.application.TurnoService y Spring usa el
 * nombre de clase en minuscula como bean name por defecto — sin esto,
 * ConflictingBeanDefinitionException al arrancar (confirmado real).
 */
@Service("personalTurnoService")
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final EmpleadoRepository empleadoRepository;

    public TurnoService(TurnoRepository turnoRepository, EmpleadoRepository empleadoRepository) {
        this.turnoRepository = turnoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(readOnly = true)
    public List<TurnoResumen> listar(LocalDate fechaInicio, LocalDate fechaFin, Integer empleadoId) {
        return turnoRepository.listar(fechaInicio, fechaFin, empleadoId);
    }

    /**
     * usuario_id = quien registra (el admin/jefe del JWT), NUNCA el
     * empleado. horas_trabajadas siempre calculado (ver Turno.calcularHoras)
     * — un valor de horas_trabajadas en el request, si viniera, se ignora
     * por completo (no llega ni a este metodo, ver TurnoCrearRequest).
     */
    @Transactional
    public Turno crear(Integer empleadoId, LocalDate fecha, OffsetDateTime horaInicio, OffsetDateTime horaFin,
                        String observaciones, Integer usuarioIdQueRegistra) {
        empleadoRepository.buscarPorId(empleadoId).orElseThrow(EmpleadoNoEncontradoException::new);
        if (!horaFin.isAfter(horaInicio)) {
            throw new HoraFinAntesDeInicioException();
        }
        Integer tenantId = TenantContext.getCurrentTenantId();
        Turno turno = new Turno(tenantId, empleadoId, usuarioIdQueRegistra, fecha, horaInicio, horaFin,
                observaciones);
        return turnoRepository.guardar(turno);
    }

    @Transactional
    public Turno actualizar(Integer id, Integer empleadoId, LocalDate fecha, OffsetDateTime horaInicio,
                             OffsetDateTime horaFin, JsonNullable<String> observaciones) {
        if (empleadoId != null) {
            empleadoRepository.buscarPorId(empleadoId).orElseThrow(EmpleadoNoEncontradoException::new);
        }
        Turno turno = buscarPorId(id);
        OffsetDateTime inicioEfectivo = horaInicio != null ? horaInicio : turno.getHoraInicio();
        OffsetDateTime finEfectivo = horaFin != null ? horaFin : turno.getHoraFin();
        if (finEfectivo != null && !finEfectivo.isAfter(inicioEfectivo)) {
            throw new HoraFinAntesDeInicioException();
        }
        turno.actualizar(empleadoId, fecha, horaInicio, horaFin, observaciones);
        return turnoRepository.guardar(turno);
    }

    @Transactional
    public void eliminar(Integer id) {
        Turno turno = buscarPorId(id);
        turnoRepository.eliminar(turno);
    }

    private Turno buscarPorId(Integer id) {
        return turnoRepository.buscarPorId(id).orElseThrow(TurnoNoEncontradoException::new);
    }
}
