package com.yeyang.redbook.search.biz.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 搜索用户
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserRspVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 昵称：关键词高亮
     */
    private String highlightNickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * redbookID
     */
    private String redbookId;

    /**
     * 笔记发布总数
     */
    private Integer noteTotal;

    /**
     * 粉丝总数
     */
    private String fansTotal;

}
