package com.banking.api.bakalaura_darbs_reactive_streams.service.integration;

import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.LoyaltyRewardRequest;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.LoyaltyRewardResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoyaltyServiceClient {

    private final ExternalServiceClient externalClient;
//    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoyaltyServiceClient.class);

    public LoyaltyServiceClient(@Qualifier("externalServiceClient") ExternalServiceClient externalClient) {
        this.externalClient = externalClient;
    }

    public Mono<LoyaltyRewardResponse> reward(LoyaltyRewardRequest request) {
//        log.info("Processing reward call, thread name={} (virtual={})", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return externalClient.post(
                "/api/external/loyalty/reward",
                request,
                LoyaltyRewardResponse.class,
                "Loyalty service call failed"
        );
    }
}
