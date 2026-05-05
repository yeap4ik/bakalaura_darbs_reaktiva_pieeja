package com.banking.api.bakalaura_darbs_reactive_streams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
public class BakalauraDarbsReactiveStreamsApplication {

    private static final Logger log = LoggerFactory.getLogger(BakalauraDarbsReactiveStreamsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BakalauraDarbsReactiveStreamsApplication.class, args);
    }

    @Bean
    ApplicationRunner startupLogger(Environment env) {
        return args -> {
            log.info("REACTIVE_STREAM_API_STARTED");
            log.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
            log.info("Virtual threads enabled: {}", env.getProperty("spring.threads.virtual.enabled"));
            log.info("Startup thread: {} (virtual={})",
                    Thread.currentThread().getName(), Thread.currentThread().isVirtual());
        };

    }
}
