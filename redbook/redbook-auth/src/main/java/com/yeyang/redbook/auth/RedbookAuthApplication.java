package com.yeyang.redbook.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.yeyang.redbook")
public class RedbookAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedbookAuthApplication.class, args);
    }

}
