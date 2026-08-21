package com.cafepos.admin.negocios.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Catalogo global fijo (5 roles, sembrados por core-api en V1__schema_v4.sql). Solo lectura aca. */
@Entity
@Table(name = "rol")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rol {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
