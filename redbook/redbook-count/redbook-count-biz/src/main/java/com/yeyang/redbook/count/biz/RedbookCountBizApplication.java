package com.yeyang.redbook.count.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yeyang.redbook.count.biz.domain.mapper")
public class RedbookCountBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedbookCountBizApplication.class, args);
    }

}
