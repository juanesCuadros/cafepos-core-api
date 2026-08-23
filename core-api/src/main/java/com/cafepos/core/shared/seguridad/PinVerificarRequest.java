package com.cafepos.core.shared.seguridad;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * modulo+accion en vez de un campo "accion" plano de negocio — mismo
 * formato del catalogo "permiso" ya existente (ver Permiso), en vez de una
 * lista paralela de nombres de acciones.
 */
public record PinVerificarRequest(
        @NotBlank(message = "usuario_autoriza_correo es obligatorio")
        @Email(message = "usuario_autoriza_correo no es válido")
        String usuarioAutorizaCorreo,

        @NotBlank(message = "El PIN es obligatorio")
        String pin,

        @NotBlank(message = "modulo es obligatorio")
        String modulo,

        @NotBlank(message = "accion es obligatoria")
        String accion,

        @NotBlank(message = "recurso_tipo es obligatorio")
        String recursoTipo,

        @NotNull(message = "recurso_id es obligatorio")
        Integer recursoId) {
}
