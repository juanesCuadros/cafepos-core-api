package com.cafepos.admin.auditoria.infrastructure.web;

import com.cafepos.admin.auditoria.application.AuditoriaAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auditoria")
@Tag(name = "Auditoria")
public class AuditoriaController {

    private final AuditoriaAdminService auditoriaService;

    public AuditoriaController(AuditoriaAdminService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    @Operation(summary = "Lista eventos de auditoría de plataforma", description = "Permite auditar todas las mutaciones realizadas por los superadministradores con paginación.")
    public Page<AuditoriaResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String entidadTipo,
            @RequestParam(required = false) Integer entidadId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaHora"));
        if (entidadTipo != null && entidadId != null) {
            return auditoriaService.listarPorEntidad(entidadTipo, entidadId, pageable).map(AuditoriaResponse::de);
        }
        return auditoriaService.listar(pageable).map(AuditoriaResponse::de);
    }
}
