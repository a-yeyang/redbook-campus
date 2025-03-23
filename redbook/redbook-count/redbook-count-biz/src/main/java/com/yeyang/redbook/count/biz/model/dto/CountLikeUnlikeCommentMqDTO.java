package com.yeyang.redbook.count.biz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 点赞、取消点赞评论
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountLikeUnlikeCommentMqDTO {

    private Long userId;

    private Long commentId;

    /**
     * 0: 取消点赞， 1：点赞
     */
    private Integer type;
}
