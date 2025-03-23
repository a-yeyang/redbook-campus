package com.yeyang.redbook.count.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author: coder
 * @url: www.yeyang.com
 * @date: 2023-08-15 10:33
 * @description: 笔记点赞、取消点赞 Type
 **/
@Getter
@AllArgsConstructor
public enum LikeUnlikeNoteTypeEnum {
    // 点赞
    LIKE(1),
    // 取消点赞
    UNLIKE(0),
    ;

    private final Integer code;

    public static LikeUnlikeNoteTypeEnum valueOf(Integer code) {
        for (LikeUnlikeNoteTypeEnum likeUnlikeNoteTypeEnum : LikeUnlikeNoteTypeEnum.values()) {
            if (Objects.equals(code, likeUnlikeNoteTypeEnum.getCode())) {
                return likeUnlikeNoteTypeEnum;
            }
        }
        return null;
    }

}
