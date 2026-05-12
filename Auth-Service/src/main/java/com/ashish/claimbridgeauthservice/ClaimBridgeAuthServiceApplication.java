package com.ashish.claimbridgeauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ClaimBridgeAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClaimBridgeAuthServiceApplication.class, args);
    }

}
