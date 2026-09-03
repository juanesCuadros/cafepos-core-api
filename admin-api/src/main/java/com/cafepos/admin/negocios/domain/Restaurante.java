package com.cafepos.admin.negocios.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Mapea restaurantes de capa tenant. */
@Entity
@Table(name = "restaurantes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private Integer tenantId;

    @Column(name = "nombre_negocio", nullable = false)
    private String nombreNegocio;

    @Column(name = "nit")
    private String nit;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "correo")
    private String correo;

    public Restaurante(Integer tenantId, String nombreNegocio) {
        this.tenantId = tenantId;
        this.nombreNegocio = nombreNegocio;
    }

    public void actualizarNombre(String nombreNegocio) {
        this.nombreNegocio = nombreNegocio;
    }

    public void actualizarInfo(String nombreNegocio, String nit, String direccion,
                               String departamento, String ciudad, String telefono, String correo) {
        if (nombreNegocio != null && !nombreNegocio.isBlank()) {
            this.nombreNegocio = nombreNegocio;
        }
        this.nit = nit;
        this.direccion = direccion;
        this.departamento = departamento;
        this.ciudad = ciudad;
        this.telefono = telefono;
        this.correo = correo;
    }
}
