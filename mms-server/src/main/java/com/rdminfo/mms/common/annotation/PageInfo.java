package com.rdminfo.mms.common.annotation;

import java.lang.annotation.*;

/**
 * 分页注入注解
 *
 * @author rdminfo 2023/12/04 9:43
 */
@Target(ElementType.PARAMETER)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PageInfo {}

