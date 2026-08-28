package com.cafepos.core.reportes;

import com.cafepos.core.reportes.application.ReportesService;
import com.cafepos.core.shared.tenant.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("dev") // Usa la base de datos dev real (localhost:5434)
public class ReportesManualTester {

    @Autowired
    private ReportesService reportesService;

    @Test
    public void testAllReports() throws Exception {
        // Simular que estamos en el tenant 6
        TenantContext.setCurrentTenantId(6);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        System.out.println("=========================================");
        System.out.println("REPORTE: VENTAS (GENERAL)");
        Object ventas = reportesService.obtenerReporteVentas(null, null, null, null);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ventas));

        System.out.println("=========================================");
        System.out.println("REPORTE: PRODUCTOS MAS VENDIDOS");
        Object productos = reportesService.obtenerReporteProductos(null, null, null);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(productos));

        System.out.println("=========================================");
        System.out.println("REPORTE: INGREDIENTES MAS USADOS (Debe estar vacio)");
        Object ingredientes = reportesService.obtenerReporteIngredientes(null, null, null);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ingredientes));

        System.out.println("=========================================");
        System.out.println("REPORTE: VENTAS POR MESERO");
        Object meseros = reportesService.obtenerReporteVentasMesero(null, null, null);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(meseros));

        System.out.println("=========================================");
        System.out.println("REPORTE: TICKET POR DIA");
        Object tickets = reportesService.obtenerReporteTicketDia(null, null);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tickets));

        System.out.println("=========================================");
        System.out.println("REPORTE: CLIENTES FRECUENTES");
        Object clientes = reportesService.obtenerReporteClientes(null, null);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(clientes));

        System.out.println("=========================================");
        System.out.println("REPORTE: DEMANDA (HORA)");
        Object demandaHora = reportesService.obtenerReporteDemanda(null, null, "hora");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(demandaHora));

        System.out.println("=========================================");
        System.out.println("REPORTE: DEMANDA (DIA)");
        Object demandaDia = reportesService.obtenerReporteDemanda(null, null, "dia");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(demandaDia));
        
        TenantContext.clear();
    }
}
