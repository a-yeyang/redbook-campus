package com.yeyang.redbook.search.biz.index;

/**
 * @author: coder
 * @date: 2025/3/3 21:37
 * @version: v1.0.0
 * @description: 用户索引
 **/
public class UserIndex {

    /**
     * 索引名称
     */
    public static final String NAME = "user";

    /**
     * 用户ID
     */
    public static final String FIELD_USER_ID = "id";

    /**
     * 昵称
     */
    public static final String FIELD_USER_NICKNAME = "nickname";

    /**
     * 头像
     */
    public static final String FIELD_USER_AVATAR = "avatar";

    /**
     * redbookID
     */
    public static final String FIELD_USER_XIAOHASHU_ID = "redbook_id";

    /**
     * 发布笔记总数
     */
    public static final String FIELD_USER_NOTE_TOTAL = "note_total";

    /**
     * 粉丝总数
     */
    public static final String FIELD_USER_FANS_TOTAL = "fans_total";

}
