package com.yeyang.redbook.kv.biz.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: coder
 * @date: 2025/2/14 16:19
 * @version: v1.0.0
 * @description: 评论内容
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentContentDO {

    private CommentContentPrimaryKey primaryKey;

    private String content;
}
