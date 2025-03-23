package com.yeyang.redbook.note.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.yeyang.redbook.note.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.yeyang.redbook")
public class RedbookNoteBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedbookNoteBizApplication.class, args);
    }

}
