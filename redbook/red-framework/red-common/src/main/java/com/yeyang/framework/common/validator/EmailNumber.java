package com.yeyang.framework.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author: coder
 * @date: 2025/2/15 22:22
 * @version: v1.0.0
 * @description: 自定义邮箱号校验注解
 **/
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailNumberValidator.class)
public @interface EmailNumber {

    String message() default "邮箱号格式不正确, 需为x位数字";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
