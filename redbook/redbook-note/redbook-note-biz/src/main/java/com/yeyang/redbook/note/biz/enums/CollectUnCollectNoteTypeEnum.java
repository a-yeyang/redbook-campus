package com.yeyang.redbook.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: coder
 * @url: www.yeyang.com
 * @date: 2023-08-15 10:33
 * @description: 笔记收藏、取消收藏 Type
 **/
@Getter
@AllArgsConstructor
public enum CollectUnCollectNoteTypeEnum {
    // 收藏
    COLLECT(1),
    // 取消收藏
    UN_COLLECT(0),
    ;

    private final Integer code;

}
