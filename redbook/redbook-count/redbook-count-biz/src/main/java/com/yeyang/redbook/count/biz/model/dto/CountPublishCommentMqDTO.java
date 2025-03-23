package com.yeyang.redbook.count.biz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 发送给计数服务：评论发布
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountPublishCommentMqDTO {

    /**
     * 笔记 ID
     */
    private Long noteId;

    /**
     * 评论 ID
     */
    private Long commentId;

    /**
     * 评论级别
     */
    private Integer level;

    /**
     * 父 ID
     */
    private Long parentId;

}
