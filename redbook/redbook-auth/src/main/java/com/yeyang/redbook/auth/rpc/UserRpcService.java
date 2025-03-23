package com.yeyang.redbook.auth.rpc;

import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.user.api.UserFeignApi;
import com.yeyang.redbook.user.dto.req.FindUserByEmailReqDTO;
import com.yeyang.redbook.user.dto.req.RegisterUserReqDTO;
import com.yeyang.redbook.user.dto.req.UpdateUserPasswordReqDTO;
import com.yeyang.redbook.user.dto.resp.FindUserByEmailRspDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author: coder
 * @date: 2025/2/13 23:29
 * @version: v1.0.0
 * @description: 用户服务
 **/
@Component
public class UserRpcService {

    @Resource
    private UserFeignApi userFeignApi;

    /**
     * 用户注册
     *
     * @param email
     * @return
     */
    public Long registerUser(String email) {
        RegisterUserReqDTO registerUserReqDTO = new RegisterUserReqDTO();
        registerUserReqDTO.setEmail(email);

        Response<Long> response = userFeignApi.registerUser(registerUserReqDTO);

        if (!response.isSuccess()) {
            return null;
        }

        return response.getData();
    }

    /**
     * 根据邮箱号查询用户信息
     *
     * @param email
     * @return
     */
    public FindUserByEmailRspDTO findUserByEmail(String email) {
        FindUserByEmailReqDTO findUserByEmailReqDTO = new FindUserByEmailReqDTO();
        findUserByEmailReqDTO.setEmail(email);

        Response<FindUserByEmailRspDTO> response = userFeignApi.findByEmail(findUserByEmailReqDTO);

        if (!response.isSuccess()) {
            return null;
        }

        return response.getData();
    }

    /**
     * 密码更新
     *
     * @param encodePassword
     */
    public void updatePassword(String encodePassword) {
        UpdateUserPasswordReqDTO updateUserPasswordReqDTO = new UpdateUserPasswordReqDTO();
        updateUserPasswordReqDTO.setEncodePassword(encodePassword);

        userFeignApi.updatePassword(updateUserPasswordReqDTO);
    }

}
