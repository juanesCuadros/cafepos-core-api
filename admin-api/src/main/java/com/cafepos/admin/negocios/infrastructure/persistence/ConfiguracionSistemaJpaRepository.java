package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.ConfiguracionSistema;
import com.cafepos.admin.negocios.domain.ConfiguracionSistemaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionSistemaJpaRepository
        extends JpaRepository<ConfiguracionSistema, Integer>, ConfiguracionSistemaRepository {
}
