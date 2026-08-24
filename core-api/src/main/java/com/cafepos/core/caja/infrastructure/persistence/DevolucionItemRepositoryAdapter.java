package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.DevolucionItem;
import com.cafepos.core.caja.domain.DevolucionItemRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class DevolucionItemRepositoryAdapter implements DevolucionItemRepository {

    private final DevolucionItemJpaRepository jpaRepository;

    DevolucionItemRepositoryAdapter(DevolucionItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DevolucionItem guardar(DevolucionItem item) {
        return jpaRepository.save(item);
    }

    @Override
    public List<DevolucionItem> listarDeDevolucion(Integer devolucionId) {
        return jpaRepository.findByDevolucionId(devolucionId);
    }
}
