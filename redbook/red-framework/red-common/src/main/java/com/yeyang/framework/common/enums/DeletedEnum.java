package com.yeyang.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: coder
 * @url: www.yeyang.com
 * @date: 2023-08-15 10:33
 * @description: 逻辑删除
 **/
@Getter
@AllArgsConstructor
public enum DeletedEnum {

    YES(true),
    NO(false);

    private final Boolean value;
}
