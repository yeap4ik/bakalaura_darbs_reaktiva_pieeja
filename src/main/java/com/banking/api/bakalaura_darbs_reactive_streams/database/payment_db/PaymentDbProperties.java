package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.db")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode

public class PaymentDbProperties {
    private String dbURL;
    private String dbUsername;
    private String dbPassword;
    private String dbDriverClassName;
    private int connectionTimeout;
    private int statementTimeout;
    private int poolSize;
    private Hibernate hibernateProperties;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class Hibernate {
        private String hb2ddl;
        private boolean showSql;
        private boolean formatSql;
        private int jdbcHibernateTimeout;
    }
}
