package com.cafepos.core.personal.infrastructure.persistence;

/** Proyeccion de la query nativa EmpleadoJpaRepository.listar — cedula RAW, el enmascarado se aplica en el adapter (mismo patron que Cliente). */
interface EmpleadoResumenRow {

    Integer getId();

    String getCodigo();

    String getNombre();

    String getCedula();

    String getCargo();

    String getTelefono();

    String getEstado();
}
