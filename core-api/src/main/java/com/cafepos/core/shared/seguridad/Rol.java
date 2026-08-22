package com.cafepos.core.shared.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Mapea rol: catalogo global fijo de 5 roles (ver V1__schema_v4.sql). Solo lectura. */
@Entity
@Table(name = "rol")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rol {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String nombre;
}
