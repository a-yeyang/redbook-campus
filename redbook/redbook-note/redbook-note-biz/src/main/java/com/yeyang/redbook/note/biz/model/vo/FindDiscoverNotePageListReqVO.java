package com.yeyang.redbook.note.biz.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 查询发现页笔记列表
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindDiscoverNotePageListReqVO {

    /**
     * 频道 ID
     */
    private Long channelId;

    @NotNull(message = "页码不能为空")
    private Integer pageNo = 1;

}
