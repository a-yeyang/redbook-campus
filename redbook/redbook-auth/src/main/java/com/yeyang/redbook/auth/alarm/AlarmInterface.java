package com.yeyang.redbook.auth.alarm;

/**
 * @author: coder
 * @date: 2025/3/7 15:24
 * @version: v1.0.0
 * @description: 告警接口
 **/
public interface AlarmInterface {

    /**
     * 发送告警信息
     *
     * @param message
     * @return
     */
    boolean send(String message);
}
