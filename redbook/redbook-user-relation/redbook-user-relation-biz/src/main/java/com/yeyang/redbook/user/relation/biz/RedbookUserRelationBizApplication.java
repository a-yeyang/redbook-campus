package com.yeyang.redbook.user.relation.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.yeyang.redbook.user.relation.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.yeyang.redbook")
public class RedbookUserRelationBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedbookUserRelationBizApplication.class, args);
    }

}
