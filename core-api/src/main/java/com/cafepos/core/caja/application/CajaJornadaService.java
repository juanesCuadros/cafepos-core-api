package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.CajaJornada;
import com.cafepos.core.caja.domain.CajaJornadaRepository;
import com.cafepos.core.caja.domain.CajaMovimiento;
import com.cafepos.core.caja.domain.CajaMovimientoRepository;
import com.cafepos.core.caja.domain.JornadaNoAbiertaException;
import com.cafepos.core.caja.domain.VentaPagoRepository;
import com.cafepos.core.caja.domain.VentaRepository;
import com.cafepos.core.shared.seguridad.Usuario;
import com.cafepos.core.shared.seguridad.UsuarioRepository;
import com.cafepos.core.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Apertura/Cierre de caja — Parte 2. */
@Service
public class CajaJornadaService {

    private final CajaJornadaRepository cajaJornadaRepository;
    private final CajaMovimientoRepository cajaMovimientoRepository;
    private final VentaRepository ventaRepository;
    private final VentaPagoRepository ventaPagoRepository;
    private final UsuarioRepository usuarioRepository;

    public CajaJornadaService(CajaJornadaRepository cajaJornadaRepository,
                               CajaMovimientoRepository cajaMovimientoRepository, VentaRepository ventaRepository,
                               VentaPagoRepository ventaPagoRepository, UsuarioRepository usuarioRepository) {
        this.cajaJornadaRepository = cajaJornadaRepository;
        this.cajaMovimientoRepository = cajaMovimientoRepository;
        this.ventaRepository = ventaRepository;
        this.ventaPagoRepository = ventaPagoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Optional<JornadaActualVista> actual() {
        return cajaJornadaRepository.buscarAbierta().map(jornada -> {
            Usuario usuarioApertura = usuarioRepository.findById(jornada.getUsuarioAperturaId()).orElseThrow();
            BigDecimal totalVentas = ventaRepository.sumaTotalCobradoDeJornada(jornada.getId());
            return new JornadaActualVista(jornada, usuarioApertura, totalVentas, movimientosDe(jornada.getId()));
        });
    }

    /** 409 si ya hay una abierta — capturado via DataIntegrityViolationException en el adapter (RN-011), no un SELECT previo. */
    @Transactional
    public CajaJornada abrir(Integer usuarioAperturaId, BigDecimal montoInicial) {
        Integer tenantId = TenantContext.getCurrentTenantId();
        CajaJornada jornada = new CajaJornada(tenantId, usuarioAperturaId, montoInicial);
        return cajaJornadaRepository.guardar(jornada);
    }

    @Transactional
    public CajaMovimiento ingreso(Integer usuarioId, BigDecimal monto, String motivo) {
        CajaJornada jornada = jornadaAbierta();
        return registrarMovimiento(jornada, usuarioId, CajaMovimiento.TIPO_INGRESO, monto, motivo);
    }

    /** El chequeo de PIN corre ANTES de esto, en el controller (ver PinStepUpService). */
    @Transactional
    public CajaMovimiento egreso(Integer usuarioId, BigDecimal monto, String motivo) {
        CajaJornada jornada = jornadaAbierta();
        return registrarMovimiento(jornada, usuarioId, CajaMovimiento.TIPO_EGRESO, monto, motivo);
    }

    /** Solo cuenta pagos en EFECTIVO (RN ya decidida) — tarjetas/Nequi/etc solo aparecen en resumen_por_metodo_pago. */
    @Transactional
    public JornadaArqueoVista cerrar(Integer usuarioCierreId, BigDecimal montoFinalFisico) {
        CajaJornada jornada = jornadaAbierta();
        BigDecimal montoFinalSistema = calcularMontoFinalSistema(jornada);

        jornada.cerrar(usuarioCierreId, montoFinalSistema, montoFinalFisico);
        jornada = cajaJornadaRepository.guardar(jornada);

        return construirArqueo(jornada);
    }

    /** monto_inicial + ingresos - egresos + SOLO pagos en efectivo de ventas cobradas de esta jornada. */
    private BigDecimal calcularMontoFinalSistema(CajaJornada jornada) {
        BigDecimal efectivoVentas = ventaPagoRepository.sumaEfectivoDeJornada(jornada.getId());
        BigDecimal ingresos = cajaMovimientoRepository.listarDeJornada(jornada.getId()).stream()
                .filter(m -> CajaMovimiento.TIPO_INGRESO.equals(m.getTipo()))
                .map(CajaMovimiento::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal egresos = cajaMovimientoRepository.listarDeJornada(jornada.getId()).stream()
                .filter(m -> CajaMovimiento.TIPO_EGRESO.equals(m.getTipo()))
                .map(CajaMovimiento::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return jornada.getMontoInicial().add(efectivoVentas).add(ingresos).subtract(egresos);
    }

    /** Usado por CajaHistorialService.detalle — misma estructura que el arqueo de cierre, para una jornada YA cerrada. */
    JornadaArqueoVista construirArqueo(CajaJornada jornada) {
        Usuario usuarioApertura = usuarioRepository.findById(jornada.getUsuarioAperturaId()).orElseThrow();
        String usuarioCierreNombre = jornada.getUsuarioCierreId() == null ? null
                : usuarioRepository.findById(jornada.getUsuarioCierreId()).map(Usuario::getNombre).orElse(null);
        BigDecimal totalVentas = ventaRepository.sumaTotalCobradoDeJornada(jornada.getId());
        List<com.cafepos.core.caja.domain.ResumenMetodoPago> resumen =
                ventaPagoRepository.resumenPorMetodoDeJornada(jornada.getId());
        return new JornadaArqueoVista(jornada, usuarioApertura.getNombre(), usuarioCierreNombre, totalVentas, resumen,
                movimientosDe(jornada.getId()));
    }

    private List<MovimientoVista> movimientosDe(Integer jornadaId) {
        return cajaMovimientoRepository.listarDeJornada(jornadaId).stream()
                .map(m -> new MovimientoVista(m, usuarioRepository.findById(m.getUsuarioId())
                        .map(Usuario::getNombre).orElse(null)))
                .toList();
    }

    private CajaMovimiento registrarMovimiento(CajaJornada jornada, Integer usuarioId, String tipo, BigDecimal monto,
                                                String motivo) {
        CajaMovimiento movimiento = new CajaMovimiento(jornada.getTenantId(), jornada.getId(), usuarioId, tipo,
                monto, motivo);
        return cajaMovimientoRepository.guardar(movimiento);
    }

    private CajaJornada jornadaAbierta() {
        return cajaJornadaRepository.buscarAbierta().orElseThrow(JornadaNoAbiertaException::new);
    }
}
