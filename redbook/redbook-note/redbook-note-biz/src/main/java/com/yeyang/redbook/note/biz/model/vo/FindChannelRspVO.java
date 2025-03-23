package com.yeyang.redbook.note.biz.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 查询频道
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindChannelRspVO {

    /**
     * 频道 ID
     */
    private Long id;

    /**
     * 频道名称
     */
    private String name;

}
