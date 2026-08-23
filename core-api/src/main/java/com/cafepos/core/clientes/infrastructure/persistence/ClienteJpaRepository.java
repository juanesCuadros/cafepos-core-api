package com.cafepos.core.clientes.infrastructure.persistence;

import com.cafepos.core.clientes.domain.Cliente;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ClienteJpaRepository extends TenantAwareRepository<Cliente, Integer> {

    /** q busca por nombre O numero_documento (nunca solo nombre, ver contrato). */
    @Query(value = "SELECT id AS id, codigo AS codigo, nombre AS nombre, tipo_documento AS tipo_documento, "
            + "numero_documento AS numero_documento, telefono AS telefono, correo AS correo, "
            + "saldo_favor AS saldo_favor "
            + "FROM cliente "
            + "WHERE (:q IS NULL OR nombre ILIKE '%' || :q || '%' OR numero_documento ILIKE '%' || :q || '%') "
            + "ORDER BY nombre", nativeQuery = true)
    List<ClienteResumenRow> listar(@Param("q") String q);

    /**
     * Version liviana para el modal de seleccion de cliente en el POS —
     * ver Javadoc de ClienteBusqueda (inferencia pendiente de verificar
     * contra api_03_caja.md).
     */
    @Query(value = "SELECT id AS id, nombre AS nombre, tipo_documento AS tipo_documento, "
            + "numero_documento AS numero_documento, saldo_favor AS saldo_favor "
            + "FROM cliente "
            + "WHERE (:q IS NULL OR nombre ILIKE '%' || :q || '%' OR numero_documento ILIKE '%' || :q || '%') "
            + "ORDER BY nombre", nativeQuery = true)
    List<ClienteBusquedaRow> buscarLiviano(@Param("q") String q);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM venta WHERE cliente_id = :clienteId)", nativeQuery = true)
    boolean tieneVentasAsociadas(@Param("clienteId") Integer clienteId);

    /**
     * venta/factura_dian pertenecen conceptualmente al futuro modulo Caja
     * (todavia no existe) — se lee la tabla directo por nombre desde aca,
     * mismo patron ya usado para configuracion_sistema/lote_insumo en
     * restaurante/inventario. LEFT JOIN porque no toda venta tiene
     * factura todavia.
     */
    @Query(value = "SELECT v.id AS venta_id, v.fecha_hora AS fecha_hora, v.total AS total, "
            + "f.numero_factura AS factura_numero, v.estado AS estado "
            + "FROM venta v LEFT JOIN factura_dian f ON f.venta_id = v.id "
            + "WHERE v.cliente_id = :clienteId "
            + "ORDER BY v.fecha_hora DESC", nativeQuery = true)
    List<CompraHistorialRow> historialDe(@Param("clienteId") Integer clienteId);

    @Query(value = "SELECT id AS id, tipo AS tipo, monto AS monto, origen_tipo AS origen_tipo, "
            + "origen_id AS origen_id, fecha AS fecha, descripcion AS descripcion "
            + "FROM cliente_saldo_movimiento "
            + "WHERE cliente_id = :clienteId "
            + "ORDER BY fecha DESC", nativeQuery = true)
    List<SaldoMovimientoItemRow> movimientosDe(@Param("clienteId") Integer clienteId);
}
