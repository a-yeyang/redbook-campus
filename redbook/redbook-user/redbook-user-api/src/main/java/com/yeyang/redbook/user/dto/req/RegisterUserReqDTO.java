package com.yeyang.redbook.user.dto.req;

import com.yeyang.framework.common.validator.EmailNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 用户注册
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserReqDTO {

    /**
     * 邮箱号
     */
    @NotBlank(message = "邮箱号不能为空")
    @EmailNumber
    private String email;

}
