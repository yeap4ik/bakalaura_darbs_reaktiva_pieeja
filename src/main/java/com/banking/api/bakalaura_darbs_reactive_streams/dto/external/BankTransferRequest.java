package com.banking.api.bakalaura_darbs_reactive_streams.dto.external;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.Account;

import java.math.BigDecimal;
import java.util.UUID;

public record BankTransferRequest(Account sender, Account receiver, BigDecimal amount, UUID paymentId) {
}
