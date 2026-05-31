package com.yeyang.redbook.data.align.constant;

/**
 * @author: coder
 * @date: 2025/1/21 15:04
 * @version: v1.0.0
 * @description: 表常量类
 **/
public class TableConstants {

    /**
     * 表名中的分隔符
     */
    private static final String TABLE_NAME_SEPARATE = "_";

    /**
     * 拼接表名后缀
     * @param hashKey
     * @return
     */
    public static String buildTableNameSuffix(String date, long hashKey) {
        // 拼接完整的表名
        return date + TABLE_NAME_SEPARATE + hashKey;
    }

}
