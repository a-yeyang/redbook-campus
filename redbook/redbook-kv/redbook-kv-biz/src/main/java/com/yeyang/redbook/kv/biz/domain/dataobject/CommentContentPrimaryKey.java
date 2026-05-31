package com.yeyang.redbook.kv.biz.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author: coder
 * @date: 2025/2/14 16:19
 * @version: v1.0.0
 * @description: 评论内容主键
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentContentPrimaryKey {

    private Long noteId; // 分区键1

    private String yearMonth; // 分区键2

    private UUID contentId; // 聚簇键

}
