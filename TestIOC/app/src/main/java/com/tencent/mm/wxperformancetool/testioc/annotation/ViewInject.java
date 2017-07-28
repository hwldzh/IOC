package com.tencent.mm.wxperformancetool.testioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by willenhuang on 2017/7/28.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInject {
    int value();

    /**
     * 父View的id
     * @return
     */
    int parentId() default 0;
}
