package com.badwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BadwalletApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BadwalletApiApplication.class, args);
    }
}