package com.cafepos.core.productosmenu.application;

import com.cafepos.core.productosmenu.domain.DiasSemanaConverter;
import com.cafepos.core.productosmenu.domain.ItemParaPromocion;
import com.cafepos.core.productosmenu.domain.ProductoNoEncontradoException;
import com.cafepos.core.productosmenu.domain.ProductoRef;
import com.cafepos.core.productosmenu.domain.ProductoRepository;
import com.cafepos.core.productosmenu.domain.ProductosIdsRequeridosException;
import com.cafepos.core.productosmenu.domain.Promocion;
import com.cafepos.core.productosmenu.domain.PromocionNoEncontradaException;
import com.cafepos.core.productosmenu.domain.PromocionRepository;
import com.cafepos.core.productosmenu.domain.PromocionResumen;
import com.cafepos.core.productosmenu.domain.PromocionSugerida;
import com.cafepos.core.productosmenu.domain.ValorDescuentoInvalidoException;
import com.cafepos.core.productosmenu.domain.VigenciaInvalidaException;
import com.cafepos.core.shared.tenant.TenantContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.operacion
 * evalue promociones_sugeridas al ver el detalle de un pedido (ver
 * evaluarSugeridas) — solo cruzan ItemParaPromocion/PromocionSugerida
 * (tambien anotados), nunca la entidad Promocion completa.
 */
@org.springframework.modulith.NamedInterface("promocionService")
@Service
public class PromocionService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    /**
     * Orden lunes-primero, igual que DiasSemanaConverter.DIAS_VALIDOS — indexado
     * por DayOfWeek.getValue()-1 (MONDAY=1 ... SUNDAY=7).
     */
    private static final List<String> DIAS_POR_INDICE = List.of(
            "lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo");

    private final PromocionRepository promocionRepository;
    private final ProductoRepository productoRepository;

    public PromocionService(PromocionRepository promocionRepository, ProductoRepository productoRepository) {
        this.promocionRepository = promocionRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<PromocionResumen> listar() {
        return promocionRepository.listar();
    }

    @Transactional(readOnly = true)
    public Promocion buscarPorId(Integer id) {
        return promocionRepository.buscarPorId(id).orElseThrow(PromocionNoEncontradaException::new);
    }

    @Transactional(readOnly = true)
    public List<ProductoRef> productosDe(Integer promocionId) {
        return promocionRepository.productosDe(promocionId);
    }

    @Transactional
    public Promocion crear(String nombre, String tipoDescuento, BigDecimal valorDescuento, String aplicaA,
                            List<Integer> productosIds, LocalDate vigenciaInicio, LocalDate vigenciaFin,
                            List<String> diasSemana, LocalTime horaInicio, LocalTime horaFin,
                            Integer cantidadMinima, BigDecimal montoMinimo, String estado) {
        validarValorDescuento(tipoDescuento, valorDescuento);
        validarVigencia(vigenciaInicio, vigenciaFin);
        String diasSemanaCsv = DiasSemanaConverter.aCsv(diasSemana);

        Integer tenantId = TenantContext.getCurrentTenantId();
        Promocion promocion = new Promocion(tenantId, nombre, tipoDescuento, valorDescuento, aplicaA,
                vigenciaInicio, vigenciaFin, diasSemanaCsv, horaInicio, horaFin, cantidadMinima, montoMinimo,
                estado);
        promocion = promocionRepository.guardar(promocion);

        if (Promocion.APLICA_PRODUCTO.equals(aplicaA)) {
            validarProductosIds(productosIds);
            promocionRepository.reemplazarProductos(promocion.getId(), tenantId, productosIds);
        }
        return promocion;
    }

    @Transactional
    public Promocion actualizar(Integer id, String nombre, String tipoDescuento, BigDecimal valorDescuento,
                                 String aplicaA, List<Integer> productosIds, LocalDate vigenciaInicio,
                                 LocalDate vigenciaFin, JsonNullable<List<String>> diasSemana,
                                 JsonNullable<LocalTime> horaInicio, JsonNullable<LocalTime> horaFin,
                                 JsonNullable<Integer> cantidadMinima, JsonNullable<BigDecimal> montoMinimo,
                                 String estado) {
        Promocion promocion = promocionRepository.buscarPorId(id).orElseThrow(PromocionNoEncontradaException::new);

        if (tipoDescuento != null || valorDescuento != null) {
            String tipoDescuentoFinal = tipoDescuento != null ? tipoDescuento : promocion.getTipoDescuento();
            BigDecimal valorDescuentoFinal = valorDescuento != null ? valorDescuento : promocion.getValorDescuento();
            validarValorDescuento(tipoDescuentoFinal, valorDescuentoFinal);
        }
        if (vigenciaInicio != null || vigenciaFin != null) {
            LocalDate vigenciaInicioFinal = vigenciaInicio != null ? vigenciaInicio : promocion.getVigenciaInicio();
            LocalDate vigenciaFinFinal = vigenciaFin != null ? vigenciaFin : promocion.getVigenciaFin();
            validarVigencia(vigenciaInicioFinal, vigenciaFinFinal);
        }

        promocion.actualizar(nombre, tipoDescuento, valorDescuento, aplicaA, vigenciaInicio, vigenciaFin,
                horaInicio, horaFin, cantidadMinima, montoMinimo, estado);

        if (diasSemana.isPresent()) {
            promocion.actualizarDiasSemana(DiasSemanaConverter.aCsv(diasSemana.get()));
        }

        promocion = promocionRepository.guardar(promocion);

        if (productosIds != null && Promocion.APLICA_PRODUCTO.equals(promocion.getAplicaA())) {
            validarProductosIds(productosIds);
            promocionRepository.reemplazarProductos(id, promocion.getTenantId(), productosIds);
        }
        return promocion;
    }

    @Transactional
    public void eliminar(Integer id) {
        Promocion promocion = promocionRepository.buscarPorId(id).orElseThrow(PromocionNoEncontradaException::new);
        promocionRepository.eliminar(promocion);
    }

    private void validarValorDescuento(String tipoDescuento, BigDecimal valorDescuento) {
        if (Promocion.TIPO_PORCENTAJE.equals(tipoDescuento)) {
            if (valorDescuento.compareTo(BigDecimal.ZERO) <= 0 || valorDescuento.compareTo(CIEN) > 0) {
                throw new ValorDescuentoInvalidoException(
                        "valor_descuento debe ser mayor a 0 y menor o igual a 100 para promociones de tipo porcentaje");
            }
        } else if (Promocion.TIPO_VALOR_FIJO.equals(tipoDescuento)) {
            if (valorDescuento.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValorDescuentoInvalidoException("valor_descuento debe ser mayor a 0");
            }
        }
    }

    private void validarVigencia(LocalDate vigenciaInicio, LocalDate vigenciaFin) {
        if (vigenciaInicio.isAfter(vigenciaFin)) {
            throw new VigenciaInvalidaException();
        }
    }

    private void validarProductosIds(List<Integer> productosIds) {
        if (productosIds == null || productosIds.isEmpty()) {
            throw new ProductosIdsRequeridosException();
        }
        for (Integer productoId : productosIds) {
            productoRepository.buscarPorId(productoId).orElseThrow(ProductoNoEncontradoException::new);
        }
    }

    /**
     * Evalua las promociones activas y vigentes AHORA MISMO contra los items
     * de un pedido — usado por com.cafepos.core.operacion para
     * promociones_sugeridas[] en GET /pedidos/{id}. No aplica descuentos,
     * solo sugiere: quien decide aplicar una promocion real es Caja/Venta
     * (modulo futuro).
     */
    @Transactional(readOnly = true)
    public List<PromocionSugerida> evaluarSugeridas(List<ItemParaPromocion> items, BigDecimal subtotalPedido) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        String diaHoy = DIAS_POR_INDICE.get(hoy.getDayOfWeek().getValue() - 1);

        List<PromocionSugerida> sugeridas = new ArrayList<>();
        for (Promocion promo : promocionRepository.listarActivas()) {
            if (!vigenteHoy(promo, hoy) || !coincideDia(promo, diaHoy) || !coincideHora(promo, ahora)) {
                continue;
            }
            if (Promocion.APLICA_PRODUCTO.equals(promo.getAplicaA())) {
                evaluarAplicaProducto(promo, items).ifPresent(sugeridas::add);
            } else {
                evaluarAplicaVentaTotal(promo, subtotalPedido).ifPresent(sugeridas::add);
            }
        }
        return sugeridas;
    }

    private boolean vigenteHoy(Promocion promo, LocalDate hoy) {
        return !hoy.isBefore(promo.getVigenciaInicio()) && !hoy.isAfter(promo.getVigenciaFin());
    }

    private boolean coincideDia(Promocion promo, String diaHoy) {
        List<String> dias = DiasSemanaConverter.aLista(promo.getDiasSemana());
        return dias == null || dias.contains(diaHoy);
    }

    private boolean coincideHora(Promocion promo, LocalTime ahora) {
        if (promo.getHoraInicio() == null || promo.getHoraFin() == null) {
            return true;
        }
        return !ahora.isBefore(promo.getHoraInicio()) && !ahora.isAfter(promo.getHoraFin());
    }

    private Optional<PromocionSugerida> evaluarAplicaProducto(Promocion promo, List<ItemParaPromocion> items) {
        List<Integer> productosIds = promocionRepository.productosDe(promo.getId()).stream()
                .map(ProductoRef::id)
                .toList();
        List<ItemParaPromocion> coincidentes = items.stream()
                .filter(i -> productosIds.contains(i.productoId()))
                .toList();
        if (coincidentes.isEmpty()) {
            return Optional.empty();
        }
        BigDecimal cantidadTotal = coincidentes.stream().map(ItemParaPromocion::cantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal montoTotal = coincidentes.stream().map(ItemParaPromocion::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (promo.getCantidadMinima() != null
                && cantidadTotal.compareTo(BigDecimal.valueOf(promo.getCantidadMinima())) < 0) {
            return Optional.empty();
        }
        if (promo.getMontoMinimo() != null && montoTotal.compareTo(promo.getMontoMinimo()) < 0) {
            return Optional.empty();
        }
        return Optional.of(new PromocionSugerida(promo.getId(), promo.getNombre(),
                calcularDescuento(promo, montoTotal)));
    }

    private Optional<PromocionSugerida> evaluarAplicaVentaTotal(Promocion promo, BigDecimal subtotalPedido) {
        if (promo.getMontoMinimo() != null && subtotalPedido.compareTo(promo.getMontoMinimo()) < 0) {
            return Optional.empty();
        }
        return Optional.of(new PromocionSugerida(promo.getId(), promo.getNombre(),
                calcularDescuento(promo, subtotalPedido)));
    }

    /** valor_fijo nunca descuenta mas de lo que el monto base realmente vale. */
    private BigDecimal calcularDescuento(Promocion promo, BigDecimal montoBase) {
        BigDecimal descuento;
        if (Promocion.TIPO_PORCENTAJE.equals(promo.getTipoDescuento())) {
            descuento = montoBase.multiply(promo.getValorDescuento())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);
        } else {
            descuento = promo.getValorDescuento();
        }
        return descuento.min(montoBase);
    }
}
