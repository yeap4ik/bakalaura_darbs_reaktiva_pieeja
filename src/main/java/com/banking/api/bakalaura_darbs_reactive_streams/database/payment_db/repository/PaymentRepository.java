package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.Payment;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PaymentRepository extends ReactiveCrudRepository<Payment, UUID> {
}
