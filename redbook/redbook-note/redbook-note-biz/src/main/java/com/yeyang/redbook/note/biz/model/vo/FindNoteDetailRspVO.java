package com.yeyang.redbook.note.biz.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 查询笔记详情
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindNoteDetailRspVO {

    private Long id;

    private Integer type;

    private String title;

    private String content;

    private List<String> imgUris;

    /**
     * 话题集合
     */
    List<FindTopicRspVO> topics;

    private Long creatorId;

    private String creatorName;

    private String avatar;

    private String videoUri;

    /**
     * 编辑时间
     */
    private String updateTime;

    /**
     * 是否可见
     */
    private Integer visible;

}
