package com.yeyang.redbook.count.biz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
public class LikeUnlikeCommentMqDTO {

    private Long userId;

    private Long commentId;

    /**
     * 0: 取消点赞， 1：点赞
     */
    private Integer type;

    private LocalDateTime createTime;
}
