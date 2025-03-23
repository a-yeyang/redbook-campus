package com.yeyang.redbook.user.biz.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 获取用户主页信息
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserProfileRspVO {

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * redbook ID
     */
    private String redbookId;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 岁数
     */
    private Integer age;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 个人介绍
     */
    private String introduction;

    // TODO: 关注数、粉丝数、获赞与点赞

}
