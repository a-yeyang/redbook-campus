package com.yeyang.redbook.auth.alarm.impl;

import com.yeyang.redbook.auth.alarm.AlarmInterface;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: coder
 * @date: 2025/3/7 15:26
 * @version: v1.0.0
 * @description: 邮件告警
 **/
@Slf4j
public class MailAlarmHelper implements AlarmInterface {

    /**
     * 发送告警信息
     *
     * @param message
     * @return
     */
    @Override
    public boolean send(String message) {
        log.info("==> 【邮件告警】：{}", message);

        // 业务逻辑...

        return true;
    }
}
