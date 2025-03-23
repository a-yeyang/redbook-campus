package com.yeyang.redbook.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: coder
 * @url: www.yeyang.com
 * @date: 2023-08-15 10:33
 * @description: 笔记操作
 **/
@Getter
@AllArgsConstructor
public enum NoteOperateEnum {
    // 笔记发布
    PUBLISH(1),
    // 笔记删除
    DELETE(0),
    ;

    private final Integer code;

}
