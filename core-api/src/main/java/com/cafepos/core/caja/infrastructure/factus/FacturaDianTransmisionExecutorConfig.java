package com.cafepos.core.caja.infrastructure.factus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hilo aparte para el intento real de transmision a Factus tras el commit
 * de POST /ventas — ver FacturaDianTransmisionService.programarTransmisionTrasCommit.
 * Virtual threads (Java 21): la llamada a Factus es I/O de red bloqueante
 * (RestClient), nunca CPU-bound, encaja exactamente en el caso de uso de
 * virtual threads sin necesidad de tunear un pool de tamano fijo.
 */
@Configuration
class FacturaDianTransmisionExecutorConfig {

    @Bean(destroyMethod = "shutdown")
    ExecutorService facturaDianTransmisionExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
