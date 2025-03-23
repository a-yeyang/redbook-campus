package com.yeyang.redbook.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.yeyang.framework.common.exception.BizException;
import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.auth.constant.RedisKeyConstants;
import com.yeyang.redbook.auth.enums.ResponseCodeEnum;
import com.yeyang.redbook.auth.model.vo.verificationcode.SendVerificationCodeReqVO;
import com.yeyang.redbook.auth.service.VerificationCodeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: TODO
 **/
@Service
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private JavaMailSender mailSender;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @Value("${spring.mail.username}")
    private String mailFrom; // 发件人邮箱
    @Value("${spring.mail.password}")
    private String mailPassword; // 授权码

    /**
     * 发送短信验证码
     *
     * @param sendVerificationCodeReqVO
     * @return
     */
    @Override
    public Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO) {
        // 邮箱号
        String email = sendVerificationCodeReqVO.getEmail();

        // 构建验证码 redis key
        String key = RedisKeyConstants.buildVerificationCodeKey(email);

        // 判断是否已发送验证码
        boolean isSent = redisTemplate.hasKey(key);
        if (isSent) {
            // 若之前发送的验证码未过期，则提示发送频繁
            throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_SEND_FREQUENTLY);
        }

        // 生成 6 位随机数字验证码
        String verificationCode = RandomUtil.randomNumbers(6);

        log.info("==> 邮箱号: {}, 已生成验证码：【{}】", email, verificationCode);

        // 调用第三方短信发送服务
//        threadPoolTaskExecutor.submit(() -> {
//            String signName = "阿里云短信测试";
//            String templateCode = "SMS_154950909";
//            String templateParam = String.format("{\"code\":\"%s\"}", verificationCode);
//            // aliyunSmsHelper.sendMessage(signName, templateCode, email, templateParam);
//        });
        // 在邮件发送代码处可以添加详细日志
        threadPoolTaskExecutor.submit(() -> {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                // 主题
                mailMessage.setSubject("验证码邮件-邮箱登录测试-作者:椰羊");
                // 内容
                mailMessage.setText("您收到的验证码是: " + verificationCode );
                // 发给谁
                mailMessage.setTo(email);
                // 从配置文件中读取发件人邮箱
                mailMessage.setFrom(mailFrom);
                // 发送
                mailSender.send(mailMessage);
            } catch (Exception e) {
                log.error("==> 邮件发送异常，目标邮箱：{}，异常信息：{}", email, e.getMessage());
                // 可以添加重试逻辑或异常上报
            }
        });

        // 存储验证码到 redis, 并设置过期时间为 3 分钟
        redisTemplate.opsForValue().set(key, verificationCode, 300, TimeUnit.MINUTES);

        return Response.success();
    }
}
