package com.cafepos.core.caja.domain;

/**
 * Cliente ya mapeado al vocabulario de Factus — ver FacturaDianTransmisionService
 * (unico builder). names/company son mutuamente excluyentes: names para
 * persona natural (legalOrganizationCode "2"), company para persona
 * juridica (legalOrganizationCode "1").
 */
public record ClienteTransmisionFactus(String identificationDocumentCode, String identification,
                                        String legalOrganizationCode, String names, String company, String email) {
}
