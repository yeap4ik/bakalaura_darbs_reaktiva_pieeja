package com.banking.api.bakalaura_darbs_reactive_streams.service.integration;

import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.FraudCheckRequest;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.FraudCheckResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FraudServiceClient {

    private final ExternalServiceClient externalClient;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FraudServiceClient.class);

    public FraudServiceClient(@Qualifier("externalServiceClient") ExternalServiceClient externalClient) {
        this.externalClient = externalClient;
    }

    public Mono<FraudCheckResponse> checkFraud(FraudCheckRequest request) {
        log.info("Processing fraud check, thread name={} (virtual={})", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return externalClient.post(
                "/api/external/fraud/check",
                request,
                FraudCheckResponse.class,
                "Fraud service call failed"
        );
    }
}
