package com.cafepos.core.personal.application;

import com.cafepos.core.personal.domain.ConfiguracionPropinaTenant;
import com.cafepos.core.personal.domain.EmpleadoRepository;
import com.cafepos.core.personal.domain.PropinaRepository;
import com.cafepos.core.personal.domain.UsuarioAsociado;
import com.cafepos.core.personal.domain.VentaConPropina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * Calculo de propinas atribuidas a un empleado — SOLO uso interno de este
 * modulo (EmpleadoService.detalle y EmpleadoService.propinas), y ahora
 * expuesto a reportes via @NamedInterface.
 *
 * Solo cuenta venta con estado='cobrado' (una anulada nunca genero
 * propina real). Si el empleado no tiene usuario asociado, no hay
 * ninguna propina que atribuirle — devuelve 0/vacio sin error.
 *
 * factor segun configuracion_sistema.propina_destino: 'mesero' -> 100%,
 * 'mixto' -> propina_pct_mesero% (NULL se trata como 0%, documentado
 * aca a proposito — sin ese porcentaje configurado no hay forma real de
 * saber cuanto le corresponde al mesero), 'restaurante' (o cualquier
 * otro valor inesperado) -> 0%.
 */
import org.springframework.modulith.NamedInterface;

@NamedInterface
@Service
public class PropinaCalculoService {

    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final String DESTINO_MESERO = "mesero";
    private static final String DESTINO_MIXTO = "mixto";

    private final EmpleadoRepository empleadoRepository;
    private final PropinaRepository propinaRepository;

    PropinaCalculoService(EmpleadoRepository empleadoRepository, PropinaRepository propinaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.propinaRepository = propinaRepository;
    }

    @Transactional(readOnly = true)
    public ResumenPropinas calcular(Integer empleadoId, LocalDate fechaInicio, LocalDate fechaFin) {
        Optional<UsuarioAsociado> usuario = empleadoRepository.buscarUsuarioAsociado(empleadoId);
        if (usuario.isEmpty()) {
            return new ResumenPropinas(BigDecimal.ZERO, List.of());
        }

        OffsetDateTime desde = fechaInicio == null ? null : fechaInicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime hasta = fechaFin == null ? null : fechaFin.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        List<VentaConPropina> ventas = propinaRepository.listarVentasConPropina(usuario.get().id(), desde, hasta);
        BigDecimal factor = resolverFactor(propinaRepository.obtenerConfiguracionPropina());

        List<DetallePropinaVenta> detalle = ventas.stream()
                .map(v -> new DetallePropinaVenta(v.codigo(), v.fecha(), v.propina(),
                        v.propina().multiply(factor).setScale(2, RoundingMode.HALF_UP)))
                .toList();
        BigDecimal total = detalle.stream()
                .map(DetallePropinaVenta::montoAtribuido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResumenPropinas(total, detalle);
    }

    private BigDecimal resolverFactor(ConfiguracionPropinaTenant config) {
        if (DESTINO_MESERO.equals(config.propinaDestino())) {
            return BigDecimal.ONE;
        }
        if (DESTINO_MIXTO.equals(config.propinaDestino())) {
            BigDecimal pct = config.propinaPctMesero();
            return pct != null ? pct.divide(CIEN, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }
}
