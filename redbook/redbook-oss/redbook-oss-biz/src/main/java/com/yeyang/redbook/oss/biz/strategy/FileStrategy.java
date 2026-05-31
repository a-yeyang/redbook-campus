package com.yeyang.redbook.oss.biz.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author: coder
 * @date: 2025/3/27 19:47
 * @version: v1.0.0
 * @description: 文件策略接口
 **/
public interface FileStrategy {

    /**
     * 文件上传
     *
     * @param file
     * @param bucketName
     * @return
     */
    String uploadFile(MultipartFile file, String bucketName);

}
