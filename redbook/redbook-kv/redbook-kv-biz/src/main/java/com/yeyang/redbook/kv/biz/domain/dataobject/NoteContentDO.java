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
 * @description: 笔记内容
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteContentDO {

    private UUID id;

    private String content;
}
