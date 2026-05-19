package com.banking.api.bakalaura_darbs_reactive_streams.dto.external;

import java.math.BigDecimal;

public record FraudCheckRequest(Long userId, BigDecimal amount) {
}
