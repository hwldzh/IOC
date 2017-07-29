package com.tencent.mm.wxperformancetool.testioc.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by willenhuang on 2017/7/28.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
    /**
     * 控件的id集合，id小于1时不执行ui事件绑定
     * @return
     */
    int[] value();

    /**
     * 控件的parent的控件集合，组合为（value[i], parentId[i] or 0）
     * @return
     */
    int[] parentId() default 0;

    /**
     * 事件的listener，默认为点击事件
     * @return
     */
    Class<?> type() default View.OnClickListener.class;

    /**
     * 事件的setter方法名, 默认为set+type#simpleName.
     * @return
     */
    String setter() default "";
}
