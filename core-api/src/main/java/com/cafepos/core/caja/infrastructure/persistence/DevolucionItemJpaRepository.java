package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.DevolucionItem;
import com.cafepos.core.shared.tenant.TenantAwareRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

interface DevolucionItemJpaRepository extends TenantAwareRepository<DevolucionItem, Integer> {

    List<DevolucionItem> findByDevolucionId(Integer devolucionId);

    @Query("SELECT COALESCE(SUM(di.cantidad), 0) FROM DevolucionItem di WHERE di.pedidoItemId = :pedidoItemId")
    BigDecimal sumaCantidadPorPedidoItem(@Param("pedidoItemId") Integer pedidoItemId);
}
