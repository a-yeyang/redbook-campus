package com.yeyang.framework.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author: coder
 * @date: 2025/2/15 22:23
 * @version: v1.0.0
 * @description: TODO
 **/
public class EmailNumberValidator implements ConstraintValidator<EmailNumber, String> {

    @Override
    public void initialize(EmailNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String emailNumber, ConstraintValidatorContext context) {
        // 校验逻辑：正则表达式判断邮箱号是否为 11 位数字
        return emailNumber != null && emailNumber.matches(".*@.*");
    }
}
