package com.yeyang.redbook.search.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.yeyang.redbook.search.biz.domain.mapper")
public class RedbookSearchBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedbookSearchBizApplication.class, args);
    }

}
