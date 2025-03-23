package com.yeyang.redbook.note.biz.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 查询话题
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTopicRspVO {

    /**
     * 话题 ID
     */
    private Long id;

    /**
     * 话题名称
     */
    private String name;

}
