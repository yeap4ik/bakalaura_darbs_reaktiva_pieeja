package com.banking.api.bakalaura_darbs_reactive_streams.dto.external;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.User;

import java.math.BigDecimal;

public record LoyaltyRewardRequest(User user, BigDecimal amount) {
}
