package com.cafepos.admin.negocios.domain;

import java.util.List;
import java.util.Optional;

public interface RolRepository {

    Optional<Rol> findByNombre(String nombre);

    List<Rol> findAll();
}
