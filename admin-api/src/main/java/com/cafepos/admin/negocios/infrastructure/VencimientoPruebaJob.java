package com.cafepos.admin.negocios.infrastructure;

import com.cafepos.admin.negocios.application.VencimientoPruebaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Trigger de scheduling puro — la logica real vive en VencimientoPruebaService (application). */
@Component
public class VencimientoPruebaJob {

    private final VencimientoPruebaService vencimientoPruebaService;

    public VencimientoPruebaJob(VencimientoPruebaService vencimientoPruebaService) {
        this.vencimientoPruebaService = vencimientoPruebaService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void ejecutar() {
        vencimientoPruebaService.ejecutar();
    }
}
