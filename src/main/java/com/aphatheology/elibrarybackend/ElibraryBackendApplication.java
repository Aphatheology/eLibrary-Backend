package com.aphatheology.elibrarybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
@EnableAsync
public class ElibraryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElibraryBackendApplication.class, args);
    }

}
