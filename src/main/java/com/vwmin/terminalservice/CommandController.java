package com.vwmin.terminalservice;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author vwmin
 * @version 1.0
 * @date 2020/4/9 13:27
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CommandController {
    @AliasFor(annotation = Component.class)
    String value() default "";

    String bind();
}
