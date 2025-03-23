package com.yeyang.redbook.oss.biz.service;

import com.yeyang.framework.common.response.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: coder
 * @date: 2025/2/11 17:12
 * @version: v1.0.0
 * @description: TODO
 **/
public interface FileService {

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    Response<?> uploadFile(MultipartFile file);
}
