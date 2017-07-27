package com.tencent.mm.wxperformancetool.testioc.view;

import android.app.Activity;

import com.tencent.mm.wxperformancetool.testioc.x;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

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
        ContentView contentView = findContentView(handlerType);
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
}
