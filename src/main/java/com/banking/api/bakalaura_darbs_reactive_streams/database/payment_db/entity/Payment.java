package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity;

import com.banking.api.bakalaura_darbs_reactive_streams.dto.payment.CreatePaymentRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;

    @Column("sender_id")
    private Long senderUserId;

    @Column("sender_account_id")
    private Long senderAccountId;

    @Column("receiver_account_id")
    private Long receiverAccountId;

    @Column("amount")
    private BigDecimal amount;

    // (EUR, USD, GBP, etc.)
    @Column("currency")
    private String currency;

    @Column("description")
    private String description;

    @Column("status")
    private PaymentStatus status;

    @Column("bank_transaction_id")
    private String bankTransactionId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    // To ensure there are no duplicates
    @Column("deduplicate_key")
    private String deduplicateKey;

    // Metadata, for ex: "{"device": "iOS", "ip": "192.168.1.1"}")
    // tipe: TEXT
    @Column("metadata")
    private String metadata;

    @Column("receiver_name")
    private String receiverName;

    @Column("receiver_bic")
    private String receiverBic;

    @Column("payment_type")
    private PaymentType paymentType;

    @Column("fee_amount")
    private BigDecimal feeAmount;

    @Transient
    private boolean isNew = false;

    public static Payment createPending(
            Account senderAccount,
            Account receiverAccount, // if receiver is other bank can be null
            CreatePaymentRequest request
    ) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setNew(true);
        payment.setSenderUserId(senderAccount.getUserId());
        payment.setSenderAccountId(senderAccount.getId());
        payment.setReceiverAccountId(receiverAccount != null ? receiverAccount.getId() : null);
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setDescription(request.description());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setDeduplicateKey(request.deduplicationKey());
        payment.setReceiverName(request.receiverName());
        payment.setReceiverBic(request.receiverBic());
        payment.setPaymentType(request.paymentType());
        payment.setFeeAmount(request.amount() != null ? request.amount() : BigDecimal.ZERO);
        payment.setMetadata(request.metadata());
        return payment;
    }
}
