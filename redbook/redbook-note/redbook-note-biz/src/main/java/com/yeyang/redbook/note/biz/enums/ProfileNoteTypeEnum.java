package com.yeyang.redbook.note.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author: yeyang
 * @url: www.yeyang.com
 * @date: 2023-08-15 10:33
 * @description: 个人主页笔记列表查询类型
 **/
@Getter
@AllArgsConstructor
public enum ProfileNoteTypeEnum {

    ALL(1), // 所有笔记
    COLLECTED(2), // 收藏
    LIKED(3), // 点赞
    ;
    private final Integer code;

    /**
     * 根据类型 code 获取对应的枚举
     *
     * @param code
     * @return
     */
    public static ProfileNoteTypeEnum valueOf(Integer code) {
        for (ProfileNoteTypeEnum profileNoteTypeEnum : ProfileNoteTypeEnum.values()) {
            if (Objects.equals(code, profileNoteTypeEnum.getCode())) {
                return profileNoteTypeEnum;
            }
        }
        throw new IllegalArgumentException("错误的笔记列表查询类型");
    }

}
