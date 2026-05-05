package com.banking.api.bakalaura_darbs_reactive_streams.service;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository.CustomPaymentDao;
import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.Account;
import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository.AccountRepository;
import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository.UserRepository;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.BankTransferRequest;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.BankTransferResponse;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.FraudCheckRequest;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.FraudCheckResponse;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.LoyaltyRewardRequest;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.external.LoyaltyRewardResponse;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.payment.CreatePaymentRequest;
import com.banking.api.bakalaura_darbs_reactive_streams.dto.payment.PaymentResponse;
import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.Payment;
import com.banking.api.bakalaura_darbs_reactive_streams.exception.BadRequestException;
import com.banking.api.bakalaura_darbs_reactive_streams.exception.ExternalServiceException;
import com.banking.api.bakalaura_darbs_reactive_streams.exception.FraudDetectedException;
import com.banking.api.bakalaura_darbs_reactive_streams.service.integration.BankServiceClient;
import java.math.BigDecimal;
import java.util.UUID;

import com.banking.api.bakalaura_darbs_reactive_streams.service.integration.FraudServiceClient;
import com.banking.api.bakalaura_darbs_reactive_streams.service.integration.LoyaltyServiceClient;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentService.class);
    private static final String BANK_STATUS_APPROVED = "APPROVED";
    private static final String LOYALTY_STATUS_SUCCESS = "SUCCESS";

    private final BankServiceClient bankServiceClient;
    private final FraudServiceClient fraudServiceClient;
    private final LoyaltyServiceClient loyaltyServiceClient;
    private final CustomPaymentDao customPaymentDao;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PaymentDbService paymentDbService;

    public PaymentService(
            BankServiceClient bankServiceClient,
            FraudServiceClient fraudServiceClient,
            LoyaltyServiceClient loyaltyServiceClient,
            CustomPaymentDao customPaymentDao,
            AccountRepository accountRepository,
            UserRepository userRepository,
            PaymentDbService paymentDbService) {
        this.bankServiceClient = bankServiceClient;
        this.fraudServiceClient = fraudServiceClient;
        this.loyaltyServiceClient = loyaltyServiceClient;
        this.customPaymentDao = customPaymentDao;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.paymentDbService = paymentDbService;
    }

    private Mono<Void> callAndValidateFraud(Long userId, BigDecimal amount, UUID paymentId) {
        return fraudServiceClient.checkFraud(new FraudCheckRequest(userId, amount))
                .flatMap(fraudResponse -> {
                    if (validateFraundResponse(fraudResponse)) {
                        return Mono.error(new FraudDetectedException(MessageFormatter.format(
                                "Payment with ID {} rejected by fraud service for user with such userID: {}",
                                paymentId,
                                userId
                        ).getMessage()));
                    }
                    return Mono.empty();
                });
    }

    private Boolean validateFraundResponse(FraudCheckResponse fraudResponse){
        return Boolean.TRUE.equals(fraudResponse.isFraud());
    }

    public Mono<PaymentResponse> getPaymentById(UUID id) {
        log.info("Processing db search getPaymentById, thread name={} (virtual={})", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return customPaymentDao.findPaymentById(id)
                .map(this::toResponse);
    }

    public Mono<Slice<PaymentResponse>> search(Pageable pageable) {
        log.info("Processing db heavy search, thread name={} (virtual={})", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return customPaymentDao.findPayments(pageable)
                .map(slice -> slice.map(this::toResponse));
    }


    public Mono<PaymentResponse> createPayment(CreatePaymentRequest request) {
        log.info("Processing post payment, thread name={} (virtual={})", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return Mono.defer(() -> {
                    validateCreatePaymentRequest(request);
                    return Mono.zip(
                            findAccountById(request.senderAccountId()),
                            findAccountById(request.receiverAccountId())
                    );
                })
                .flatMap(accounts -> {
                    Account senderAccount = accounts.getT1();
                    Account receiverAccount = accounts.getT2();
                    return paymentDbService.createPendingPayment(request, senderAccount, receiverAccount)
                            .flatMap(payment -> callAndValidateFraud(senderAccount.getUserId(), payment.getAmount(), payment.getId())
                                    .onErrorResume(FraudDetectedException.class,
                                            ex -> paymentDbService.rejectPayment(payment.getId()).then(Mono.error(ex)))
                                    .then(callAndValidateFraud(receiverAccount.getUserId(), payment.getAmount(), payment.getId())
                                            .onErrorResume(FraudDetectedException.class,
                                                    ex -> paymentDbService.rejectPayment(payment.getId()).then(Mono.error(ex))))
                                    .then(executeExternalCalls(payment, senderAccount, receiverAccount))
                                    .flatMap(updated -> paymentDbService.markPaymentSuccess(payment.getId()))
                                    .map(this::toResponse));
                });
    }

    private void validateCreatePaymentRequest(CreatePaymentRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        if (request.senderAccountId() == null || request.receiverAccountId() <= 0) {
            throw new BadRequestException("userId must be a positive number");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("amount must be greater than zero");
        }
    }

    private void ensureExternalResponsesAreSuccessful(
            BankTransferResponse bankResponse,
            LoyaltyRewardResponse loyaltyResponse
    ) {
        if (!BANK_STATUS_APPROVED.equalsIgnoreCase(bankResponse.status())) {
            throw new ExternalServiceException("Bank transfer was not approved");
        }
        if (!LOYALTY_STATUS_SUCCESS.equalsIgnoreCase(loyaltyResponse.status())) {
            throw new ExternalServiceException("Loyalty reward did not complete successfully");
        }
    }

        private Mono<Payment> executeExternalCalls(Payment payment, Account senderAccount, Account receiverAccount) {
        Mono<BankTransferResponse> bankMono = bankServiceClient.transfer(
            new BankTransferRequest(senderAccount, receiverAccount, payment.getAmount(), payment.getId())
        );

        Mono<LoyaltyRewardResponse> loyaltyMono = userRepository.findById(payment.getSenderUserId())
            .switchIfEmpty(Mono.error(new ExternalServiceException("Sender user not found for loyalty reward")))
            .flatMap(user -> loyaltyServiceClient.reward(new LoyaltyRewardRequest(user, payment.getAmount())));

        return Mono.zip(bankMono, loyaltyMono)
            .map(result -> {
                ensureExternalResponsesAreSuccessful(result.getT1(), result.getT2());
                return payment;
            });
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getSenderUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    private Mono<Account> findAccountById(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Account with ID " + id + " not found")));
    }
}
