package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.Payment;
import com.banking.api.bakalaura_darbs_reactive_streams.exception.ResourceNotFoundException;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CustomPaymentDaoImpl implements CustomPaymentDao {

    private final PaymentRepository paymentRepository;
    private final R2dbcEntityTemplate entityTemplate;
//    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CustomPaymentDaoImpl.class);

    public CustomPaymentDaoImpl(PaymentRepository paymentRepository, R2dbcEntityTemplate entityTemplate) {
        this.paymentRepository = paymentRepository;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<Payment> findPaymentById(UUID id) {
//        log.info("Processing db search findPaymentById, thread name={} (virtual={})", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        return paymentRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Payment not found: " + id)));
    }

    @Override
    public Mono<Slice<Payment>> findPayments(Pageable pageable) {
//        log.info("Processing db search findPayments, thread name={} (virtual={})", Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        int pageSize = pageable.getPageSize();
        Query query = Query.empty().with(pageable).limit(pageSize + 1);

        return entityTemplate.select(query, Payment.class)
                .collectList()
                .map(result -> toSlice(result, pageable));
    }

    private Slice<Payment> toSlice(List<Payment> result, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        boolean hasNext = result.size() > pageSize;
        List<Payment> content = hasNext ? result.subList(0, pageSize) : result;
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
