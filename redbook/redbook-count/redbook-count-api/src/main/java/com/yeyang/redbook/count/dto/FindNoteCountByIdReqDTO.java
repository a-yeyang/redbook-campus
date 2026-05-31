package com.yeyang.redbook.count.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: yeyang
 * @date: 2025/4/7 15:17
 * @version: v1.0.0
 * @description: 根据笔记 ID 查询计数
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindNoteCountByIdReqDTO {

    /**
     * 笔记 ID
     */
    @NotNull(message = "笔记 ID 不能为空")
    private Long noteId;

}
