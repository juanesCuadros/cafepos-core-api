package com.cafepos.core.shared.excepciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Escribe una respuesta de error con la misma forma que GlobalExceptionHandler
 * (ErrorResponse), para los filtros que rechazan un request ANTES de que
 * DispatcherServlet lo reciba (TenantFilter, SuscripcionFilter,
 * DebeCambiarPasswordFilter) — @RestControllerAdvice no los alcanza porque
 * corren fuera del ciclo de manejo de excepciones de Spring MVC.
 */
@Component
public class FilterErrorWriter {

    private final ObjectMapper objectMapper;

    public FilterErrorWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void escribir(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(mensaje)));
    }
}
