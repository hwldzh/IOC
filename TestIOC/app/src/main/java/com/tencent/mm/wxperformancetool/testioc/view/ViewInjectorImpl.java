package com.tencent.mm.wxperformancetool.testioc.view;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.mtp.MtpConstants;
import android.view.View;

import com.tencent.mm.wxperformancetool.testioc.EventListenerManager;
import com.tencent.mm.wxperformancetool.testioc.annotation.ContentView;
import com.tencent.mm.wxperformancetool.testioc.annotation.Event;
import com.tencent.mm.wxperformancetool.testioc.annotation.ViewInject;
import com.tencent.mm.wxperformancetool.testioc.entity.ViewInfo;
import com.tencent.mm.wxperformancetool.testioc.utils.ViewFinder;
import com.tencent.mm.wxperformancetool.testioc.x;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;


/**
 * Created by RunningH on 2017/7/28.
 */

public final class ViewInjectorImpl implements ViewInjector {
    private static final HashSet<Class<?>> IGNORED = new HashSet<Class<?>>();

    static {
        IGNORED.add(Object.class);
        IGNORED.add(Activity.class);
        IGNORED.add(android.app.Fragment.class);
        try {
            IGNORED.add(Class.forName("android.support.v4.app.Fragment"));
            IGNORED.add(Class.forName("android.support.v4.app.FragmentActivity"));
        } catch (Throwable ignored) {
        }
    }
    private static volatile ViewInjectorImpl instance;

    public static void registerInstance() {
        if (instance == null) {
            synchronized (ViewInjectorImpl.class) {
                if (instance == null) {
                    instance = new ViewInjectorImpl();
                }
            }
        }
        x.Ext.setViewInjector(instance);
    }

    @Override
    public void inject(Activity activity) {
        Class<?> handlerType = activity.getClass();
        //设置ContentView
        try {
            ContentView contentView = findContentView(handlerType);
            if (contentView != null) {
                int valueId = contentView.value();
                if (valueId > 0) {
                    Method method = handlerType.getDeclaredMethod("setContentView", int.class);
                    method.invoke(activity, valueId);

                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        injectObject(activity, handlerType, new ViewFinder(activity)); //设置view的初始化并设置View的相关事件
    }

    private static ContentView findContentView(Class<?> handlerType) {
        if (handlerType == null || IGNORED.contains(handlerType)) {
            return null;
        }
        ContentView contentView = handlerType.getAnnotation(ContentView.class);
        if (contentView == null) {
            return findContentView(handlerType.getSuperclass());
        }
        return contentView;
    }

    private static void injectObject(Object handler, Class<?> handlerType, ViewFinder finder) {
        if (handlerType == null || IGNORED.contains(handlerType)) {
            return;
        }
        injectObject(handler, handlerType.getSuperclass(), finder);

        //开始注入View
        Field[] fields = handlerType.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Class<?> type = field.getType();
                //不注入静态字段，不注入final字段，不注入基本类型字段，不注入数组类型字段
                if (Modifier.isStatic(type.getModifiers()) || Modifier.isFinal(type.getModifiers())
                        || type.isPrimitive() || type.isArray()) {
                    continue;
                }
                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                if (viewInject != null) {
                    try {
                        View view = finder.findViewById(viewInject.value(), viewInject.parentId());
                        if (view != null) {
                            field.setAccessible(true);
                            field.set(handler, view);
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        } //View注入结束

        //开始注入事件
        Method[] methods = handlerType.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                //如果是静态方法或者是非私有的方法则不注入
                if (Modifier.isStatic(method.getModifiers())
                        || !Modifier.isPrivate(method.getModifiers())) {
                    continue;
                }
                Event event = method.getAnnotation(Event.class);
                if (event != null) {
                    int[] values = event.value();
                    int[] parentIds = event.parentId();
                    int parentIdLen = parentIds == null ? 0 : parentIds.length;
                    for (int i = 0; i < values.length; i++) {
                        int value = values[i];
                        if (value > 0) {
                            ViewInfo viewInfo = new ViewInfo();
                            viewInfo.value = value;
                            viewInfo.parentId = parentIdLen > i ? parentIds[i] : 0;
                            method.setAccessible(true);
                            EventListenerManager.addEventMethod(finder, viewInfo, event, handler);
                        }
                    }
                }
            }
        }
    }
}
