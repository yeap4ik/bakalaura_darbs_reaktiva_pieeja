package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.Payment;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import reactor.core.publisher.Mono;

public interface CustomPaymentDao {
    Mono<Payment> findPaymentById(UUID id);

    Mono<Slice<Payment>> findPayments(Pageable pageable);
}
