package com.banking.api.bakalaura_darbs_reactive_streams.service.integration;

import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.*;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.BankTransferRequest;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.BankTransferResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BankServiceClient {
    private final ExternalServiceClient externalClient;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BankServiceClient.class);

    public BankServiceClient(@Qualifier("externalServiceClient") ExternalServiceClient externalClient) {
        this.externalClient = externalClient;
    }
    public Mono<BankTransferResponse> transfer(BankTransferRequest request) {
        log.info("Processing bank transfer, thread name={} (virtual={})", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return externalClient.post(
                "/api/external/bank/transfer",
                request,
                BankTransferResponse.class,
                "Bank service call failed"
        );
    }
}
