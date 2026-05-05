package com.banking.api.bakalaura_darbs_reactive_streams.service.integration;

import com.banking.api.bakalaura_darbs_reactive_streams.exception.ExternalServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ExternalServiceClient {
    private final WebClient webClient;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExternalServiceClient.class);

    public ExternalServiceClient(WebClient externalServicesWebClient) {
        this.webClient = externalServicesWebClient;
    }

    public <T> Mono<T> post(String uri, Object requestBody, Class<T> responseType, String errorMessage) {
        log.info("Processing post http call, thread name={} (virtual={})", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return webClient.post()
                .uri(uri)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .switchIfEmpty(Mono.error(new ExternalServiceException(errorMessage + ": empty response body")))
                .onErrorMap(ex -> ex instanceof ExternalServiceException
                        ? ex
                        : new ExternalServiceException(errorMessage, ex));
    }
}
