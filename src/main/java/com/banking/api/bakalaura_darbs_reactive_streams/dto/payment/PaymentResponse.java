package com.banking.api.bakalaura_darbs_reactive_streams.dto.payment;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        Long userId,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime createdAt
) {
}
