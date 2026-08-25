package com.cafepos.core.restaurante.domain;

import com.cafepos.core.shared.criptografia.FactusCredencialAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Mapea facturacion_dian_resolucion (ver V1__schema_v4.sql, Modulo 10.4 de
 * api_10_restaurante.md) — de solo lectura para su PROPIO modulo salvo una
 * unica excepcion: incrementarNumeracion() (numeracion_actual es un
 * contador de runtime que com.cafepos.core.caja necesita avanzar cada vez
 * que emite una factura real — ver FacturacionDianService.reservarSiguienteNumeroFactura).
 * La ESCRITURA de credenciales/rango/ambiente ya no vive aca ni en ningun
 * otro punto de core-api — la hace admin-api directo contra esta misma
 * tabla (Super Admin, POST /admin/negocios/{tenant_id}/facturacion-dian,
 * ver su propia entidad equivalente en ese proyecto). prefijo/
 * fecha_expedicion/fecha_vencimiento siguen sin tener forma de escribirse
 * desde ningun codigo — eso lo configura soporte tecnico directo en base
 * de datos.
 *
 * CRITICO DE SEGURIDAD: client_id_factus, client_secret_factus, username_factus
 * y password_factus (ver V20) SI se mapean aca porque @Convert necesita el
 * campo para poder cifrar/descifrar transparente (ver shared.criptografia)
 * — la columna real siempre queda cifrada con AES-256-GCM, nunca en texto
 * plano. La proteccion contra fuga ya no es "el campo no existe", ahora es
 * "@Getter(AccessLevel.NONE) en los 4 campos bloquea a Lombok de generar
 * getters publicos" — ninguna otra clase (ni web, ni application, ni el
 * resto de domain) tiene forma normal de leer el valor descifrado, salvo
 * el metodo puntual credencialesFactus() (ver su Javadoc — exclusivo para
 * el cliente real de Factus) y reflection explicita en tests (ver
 * FacturacionDianResolucionEncriptacionIT). Ver ademas FacturacionDianResponse
 * (DTO explicito, whitelist de campos permitidos) — sigue sin incluir estos
 * campos.
 */
@Entity
@Table(name = "facturacion_dian_resolucion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FacturacionDianResolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column
    private String prefijo;

    @Column(name = "rango_inicio")
    private Long rangoInicio;

    @Column(name = "rango_fin")
    private Long rangoFin;

    @Column(name = "numeracion_actual")
    private Long numeracionActual;

    @Column(name = "fecha_expedicion")
    private LocalDate fechaExpedicion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column
    private String ambiente;

    @Column
    private String estado;

    @Getter(AccessLevel.NONE)
    @Column(name = "client_id_factus")
    @Convert(converter = FactusCredencialAttributeConverter.class)
    private String clientIdFactus;

    @Getter(AccessLevel.NONE)
    @Column(name = "client_secret_factus")
    @Convert(converter = FactusCredencialAttributeConverter.class)
    private String clientSecretFactus;

    /** El OAuth de Factus (grant_type=password) exige username/password de la cuenta ADEMAS de client_id/secret — ver V20. */
    @Getter(AccessLevel.NONE)
    @Column(name = "username_factus")
    @Convert(converter = FactusCredencialAttributeConverter.class)
    private String usernameFactus;

    @Getter(AccessLevel.NONE)
    @Column(name = "password_factus")
    @Convert(converter = FactusCredencialAttributeConverter.class)
    private String passwordFactus;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /** Ver Javadoc de la clase — unico mutador permitido, numeracion_actual es un contador de runtime. */
    public void incrementarNumeracion() {
        this.numeracionActual = (this.numeracionActual != null ? this.numeracionActual : 0L) + 1;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Unico punto de LECTURA de las 4 credenciales Factus descifradas —
     * exclusivo para el cliente real de Factus (ver CredencialesFactus,
     * tambien @NamedInterface, y FacturacionDianService.credencialesFactusPara,
     * el unico caller autorizado). clientIdFactus/clientSecretFactus/
     * usernameFactus/passwordFactus YA estan en texto plano en memoria aca —
     * el @Convert de JPA los descifro al cargar la fila (ver
     * shared.criptografia); esta clase nunca maneja el valor cifrado
     * directamente. @Getter(AccessLevel.NONE) en los 4 campos sigue
     * bloqueando cualquier otra forma de leerlos.
     */
    public CredencialesFactus credencialesFactus() {
        return new CredencialesFactus(clientIdFactus, clientSecretFactus, usernameFactus, passwordFactus, ambiente);
    }
}
