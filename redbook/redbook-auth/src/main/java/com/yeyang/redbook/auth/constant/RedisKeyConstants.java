package com.yeyang.redbook.auth.constant;

/**
 * @author: coder
 * @date: 2025/1/21 15:04
 * @version: v1.0.0
 * @description: TODO
 **/
public class RedisKeyConstants {

    /**
     * 验证码 KEY 前缀
     */
    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification_code:";


    /**
     * 构建验证码 KEY
     * @param email
     * @return
     */
    public static String buildVerificationCodeKey(String email) {
        return VERIFICATION_CODE_KEY_PREFIX + email;
    }

}
