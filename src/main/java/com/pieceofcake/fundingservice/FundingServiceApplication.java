package com.pieceofcake.fundingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FundingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FundingServiceApplication.class, args);
    }

}
