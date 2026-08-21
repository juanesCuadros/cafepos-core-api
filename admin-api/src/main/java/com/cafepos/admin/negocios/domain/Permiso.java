package com.cafepos.admin.negocios.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Catalogo global fijo (151 filas, sembradas por core-api en V2__catalogo_permisos.sql). Solo lectura aca. */
@Entity
@Table(name = "permiso")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permiso {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String modulo;

    @Column(nullable = false)
    private String accion;
}
