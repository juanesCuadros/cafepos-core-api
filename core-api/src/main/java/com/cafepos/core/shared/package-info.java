/**
 * Shared kernel — infraestructura común a todos los módulos de negocio:
 *   - tenant: TenantContext, resolución de tenant por subdominio, filtro SET LOCAL.
 *   - seguridad: JWT, PermissionEvaluator (RBAC dinámico), flujo de PIN de step-up.
 *   - auditoria: aspecto @Auditable, publicación a evento_auditoria.
 *   - excepciones: manejo global de errores, respuestas consistentes.
 *   - openapi: configuración de Swagger UI / OpenAPI (solo documentación/testing).
 *
 * Declarado como módulo OPEN: cualquier otro módulo puede depender
 * libremente de sus clases, sin las restricciones de encapsulación que
 * Spring Modulith aplica entre módulos de negocio.
 */
@org.springframework.modulith.ApplicationModule(type = org.springframework.modulith.ApplicationModule.Type.OPEN)
package com.cafepos.core.shared;
