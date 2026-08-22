package com.cafepos.core.shared.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Mapea permiso: catalogo global de modulo+accion (ver V2__catalogo_permisos.sql). Solo lectura. */
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
