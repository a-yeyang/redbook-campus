package com.yeyang.redbook.auth.service;

import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.auth.model.vo.verificationcode.SendVerificationCodeReqVO;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: TODO
 **/
public interface VerificationCodeService {

    /**
     * 发送短信验证码
     *
     * @param sendVerificationCodeReqVO
     * @return
     */
    Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO);
}
