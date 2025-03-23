package com.yeyang.redbook.note.biz.model.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 模糊查询话题
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTopicListReqVO {

    @NotBlank(message = "话题关键词不能为空")
    private String keyword;

}
