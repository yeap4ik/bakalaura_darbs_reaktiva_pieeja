package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository")
public class PaymentDbConfig {

//    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentDbConfig.class);

    @Bean(name = {"r2dbcEntityTemplate", "paymentDbEntityTemplate"})
    public R2dbcEntityTemplate paymentDbEntityTemplate(ConnectionFactory connectionFactory) {
        logConnectionFactory("R2dbcEntityTemplate", connectionFactory);
        return new R2dbcEntityTemplate(connectionFactory);
    }

    @Bean
    public ReactiveTransactionManager paymentDbTransactionManager(ConnectionFactory connectionFactory) {
        logConnectionFactory("R2dbcTransactionManager", connectionFactory);
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public TransactionalOperator paymentDbTransactionalOperator(ReactiveTransactionManager paymentDbTransactionManager) {
//        log.info("Creating TransactionalOperator for payment DB");
        return TransactionalOperator.create(paymentDbTransactionManager);
    }

    private void logConnectionFactory(String component, ConnectionFactory connectionFactory) {
        ConnectionFactoryMetadata metadata = connectionFactory.getMetadata();
        String name = metadata != null ? metadata.getName() : "unknown";
//        log.info("Creating {} using ConnectionFactory: {} ({})", component, connectionFactory.getClass().getName(), name);
    }
}
