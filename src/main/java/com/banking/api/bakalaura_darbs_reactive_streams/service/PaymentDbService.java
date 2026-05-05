package com.banking.api.bakalaura_darbs_reactive_streams.service;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.Account;
import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.Payment;
import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.PaymentStatus;
import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository.PaymentRepository;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.payment.CreatePaymentRequest;
import com.banking.api.bakalaura_darbs_reactive_streams.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import reactor.core.publisher.Mono;

@Service
public class PaymentDbService {

    private final PaymentRepository paymentRepository;

    public PaymentDbService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Mono<Payment> createPendingPayment(CreatePaymentRequest request, Account senderAccount, Account receiverAccount) {
        Payment payment = Payment.createPending(senderAccount, receiverAccount, request);
        applyTimestamps(payment, true);
        return paymentRepository.save(payment);
    }

    public Mono<Payment> rejectPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Payment not found: " + paymentId)))
                .flatMap(payment -> {
                    payment.setStatus(PaymentStatus.REJECTED);
                    applyTimestamps(payment, false);
                    return paymentRepository.save(payment);
                });
    }

    public Mono<Payment> markPaymentSuccess(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Payment not found: " + paymentId)))
                .flatMap(payment -> {
                    payment.setStatus(PaymentStatus.SUCCESS);
                    applyTimestamps(payment, false);
                    return paymentRepository.save(payment);
                });
    }

    private void applyTimestamps(Payment payment, boolean isNew) {
        LocalDateTime now = LocalDateTime.now();
        if (isNew && payment.getCreatedAt() == null) {
            payment.setCreatedAt(now);
        }
        payment.setUpdatedAt(now);
    }
}
