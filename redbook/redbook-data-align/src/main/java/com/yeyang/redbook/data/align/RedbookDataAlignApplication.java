package com.yeyang.redbook.data.align;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.yeyang.redbook.data.align.domain.mapper")
@EnableFeignClients(basePackages = "com.yeyang.redbook")
public class RedbookDataAlignApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedbookDataAlignApplication.class, args);
    }

}
