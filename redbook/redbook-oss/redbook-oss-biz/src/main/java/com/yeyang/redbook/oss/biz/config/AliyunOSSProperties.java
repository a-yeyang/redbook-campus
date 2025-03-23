package com.yeyang.redbook.oss.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: coder
 * @url: www.yeyang.com
 * @date: 2023-05-11 8:49
 * @description: 阿里云 OSS 配置项
 **/
@ConfigurationProperties(prefix = "storage.aliyun-oss")
@Component
@Data
public class AliyunOSSProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
}
