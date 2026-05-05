package com.banking.api.bakalaura_darbs_reactive_streams.dto.payment;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.PaymentType;

import java.math.BigDecimal;

public record CreatePaymentRequest(
    Long senderAccountId,
    Long receiverAccountId,
    BigDecimal amount,
    String deduplicationKey,
    String receiverAccountIban,
    String receiverName,
    String receiverBic,
    String currency,
    String description,
    PaymentType paymentType,
    String metadata
) {}
