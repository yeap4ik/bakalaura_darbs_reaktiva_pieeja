package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "liquibase")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class PaymentDbLiquibaseProperties {
    private String url;
    private String user;
    private String password;
}
