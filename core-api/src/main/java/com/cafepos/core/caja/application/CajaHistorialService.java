package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.CajaJornada;
import com.cafepos.core.caja.domain.CajaJornadaRepository;
import com.cafepos.core.caja.domain.JornadaNoEncontradaException;
import com.cafepos.core.caja.domain.VentaRepository;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.seguridad.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Historial de caja — Parte 3. */
@Service
public class CajaHistorialService {

    private final CajaJornadaRepository cajaJornadaRepository;
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CajaJornadaService cajaJornadaService;

    public CajaHistorialService(CajaJornadaRepository cajaJornadaRepository, VentaRepository ventaRepository,
                                 UsuarioRepository usuarioRepository, CajaJornadaService cajaJornadaService) {
        this.cajaJornadaRepository = cajaJornadaRepository;
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.cajaJornadaService = cajaJornadaService;
    }

    @Transactional(readOnly = true)
    public List<JornadaArqueoVista> listarJornadas(LocalDate fechaInicio, LocalDate fechaFin) {
        return cajaJornadaRepository.listarEnRango(fechaInicio, fechaFin).stream()
                .map(this::aResumen)
                .toList();
    }

    @Transactional(readOnly = true)
    public JornadaArqueoVista detalle(Integer id) {
        CajaJornada jornada = cajaJornadaRepository.buscarPorId(id).orElseThrow(JornadaNoEncontradaException::new);
        return cajaJornadaService.construirArqueo(jornada);
    }

    /** Version liviana para el listado — sin resumen_por_metodo_pago ni movimientos (ver JornadaArqueoVista). */
    private JornadaArqueoVista aResumen(CajaJornada jornada) {
        Usuario usuarioApertura = usuarioRepository.findById(jornada.getUsuarioAperturaId()).orElseThrow();
        String usuarioCierreNombre = jornada.getUsuarioCierreId() == null ? null
                : usuarioRepository.findById(jornada.getUsuarioCierreId()).map(Usuario::getNombre).orElse(null);
        var totalVentas = ventaRepository.sumaTotalCobradoDeJornada(jornada.getId());
        return new JornadaArqueoVista(jornada, usuarioApertura.getNombre(), usuarioCierreNombre, totalVentas,
                List.of(), List.of());
    }
}
