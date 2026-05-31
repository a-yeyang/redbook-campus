package com.yeyang.redbook.oss.biz.factory;

import com.yeyang.redbook.oss.biz.strategy.FileStrategy;
import com.yeyang.redbook.oss.biz.strategy.impl.MinioFileStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStrategyFactory {

    @Bean
    public FileStrategy getFileStrategy() {
        return new MinioFileStrategy();
    }
}
