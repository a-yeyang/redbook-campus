package com.yeyang.redbook.data.align.config;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: coder
 * @url: www.yeyang.com
 * @date: 2025/3/30 11:16
 * @description: RocketMQ 配置
 **/
@Configuration
@Import(RocketMQAutoConfiguration .class)
public class RocketMQConfig {
}
