package com.quartzwatch.api;

import java.lang.annotation.*;

/**
 * 标记内部API
 * 被标记的类或方法不应该被外部直接使用
 * 
 * @author QuartzWatch
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Internal {
    String value() default "内部API，请勿直接使用";
} 