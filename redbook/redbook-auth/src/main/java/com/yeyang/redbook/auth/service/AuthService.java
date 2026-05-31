package com.yeyang.redbook.auth.service;

import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.auth.model.vo.user.UpdatePasswordReqVO;
import com.yeyang.redbook.auth.model.vo.user.UserLoginReqVO;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: TODO
 **/
public interface AuthService {

    /**
     * 登录与注册
     * @param userLoginReqVO
     * @return
     */
    Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO);

    /**
     * 退出登录
     * @return
     */
    Response<?> logout();

    /**
     * 修改密码
     * @param updatePasswordReqVO
     * @return
     */
    Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO);
}
