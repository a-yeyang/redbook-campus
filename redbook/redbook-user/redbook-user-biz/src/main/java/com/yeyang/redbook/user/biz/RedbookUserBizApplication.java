package com.yeyang.redbook.user.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.yeyang.redbook.user.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.yeyang.redbook")
public class RedbookUserBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedbookUserBizApplication.class, args);
    }

}
