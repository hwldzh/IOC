package com.tencent.mm.wxperformancetool.testioc;

import android.text.TextUtils;
import android.view.View;

import com.tencent.mm.wxperformancetool.testioc.annotation.Event;
import com.tencent.mm.wxperformancetool.testioc.entity.ViewInfo;
import com.tencent.mm.wxperformancetool.testioc.utils.ViewFinder;

import org.xutils.common.util.DoubleKeyValueMap;
import org.xutils.common.util.LogUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by RunningH on 2017/7/29.
 */

public final class EventListenerManager {
    private final static long QUICK_EVENT_TIME_SPAN = 300;
    private final static HashSet<String> AVOID_QUICK_EVENT_SET = new HashSet<String>(2);
    static {
        AVOID_QUICK_EVENT_SET.add("onClick");
        AVOID_QUICK_EVENT_SET.add("onItemClick");
    }

    /**
     * k1: viewInjectInfo
     * k2: interface Type
     * value: listener
     */
    private final static DoubleKeyValueMap<ViewInfo, Class<?>, Object>
            listenerCache = new DoubleKeyValueMap<ViewInfo, Class<?>, Object>();

    public static void addEventMethod(
            //根据页面或View hodler生成的ViewFinder
            ViewFinder finder,
            //根据当前注解ID生产的ViewInfo
            ViewInfo viewInfo,
            Event event,
            Object handler,
            Method method) {
        try {
            View view = finder.findViewByInfo(viewInfo);
            if (view != null) {
                Class<?> listenerType = event.type();
                String listenerSetter = event.setter();
                if (TextUtils.isEmpty(listenerSetter)) {
                    listenerSetter = "set" + listenerType.getSimpleName();
                }
                String methodName = event.method();
                boolean addNewMethod = false;
                Object listener = listenerCache.get(viewInfo, listenerType);
                if (listener != null) {
                    DynamicHandler dynamicHandler = (DynamicHandler) Proxy.getInvocationHandler(listener);
                    addNewMethod = handler.equals(dynamicHandler.getHandler());
                    if (addNewMethod) {
                        dynamicHandler.addMethod(methodName, method);
                    }
                }
                if (!addNewMethod) {
                    DynamicHandler dynamicHandler = new DynamicHandler(handler);
                    dynamicHandler.addMethod(methodName, method);
                    listener = Proxy.newProxyInstance(
                            listenerType.getClassLoader(), new Class<?>[]{listenerType}, dynamicHandler);
                    listenerCache.put(viewInfo, listenerType, listener);
                }
                //相当于调用了View的set#***方法，比如说调用了View的setOnclicklistener方法，但是这里使用的是动态代理
                Method setEventListenerMethod = view.getClass().getMethod(listenerSetter, listenerType);
                setEventListenerMethod.invoke(view, listener);

            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static class DynamicHandler implements InvocationHandler {
        // 存放代理对象，比如Fragment或view holder
        private WeakReference<Object> handlerRef;
        // 存放代理方法
        private final HashMap<String, Method> methodMap = new HashMap<String, Method>(1);

        private static long lastClickTime = 0;

        public DynamicHandler(Object handler) {
            this.handlerRef = new WeakReference<Object>(handler);
        }

        public void addMethod(String name, Method method) {
            methodMap.put(name, method);
        }

        public Object getHandler() {
            return handlerRef.get();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object handler = handlerRef.get();
            if (handler != null) {

                String eventMethod = method.getName();
                if ("toString".equals(eventMethod)) {
                    return EventListenerManager.DynamicHandler.class.getSimpleName();
                }

                method = methodMap.get(eventMethod);
                if (method == null && methodMap.size() == 1) {
                    for (Map.Entry<String, Method> entry : methodMap.entrySet()) {
                        if (TextUtils.isEmpty(entry.getKey())) {
                            method = entry.getValue();
                        }
                        break;
                    }
                }

                if (method != null) {

                    if (AVOID_QUICK_EVENT_SET.contains(eventMethod)) {
                        long timeSpan = System.currentTimeMillis() - lastClickTime;
                        if (timeSpan < QUICK_EVENT_TIME_SPAN) {
                            LogUtil.d("onClick cancelled: " + timeSpan);
                            return null;
                        }
                        lastClickTime = System.currentTimeMillis();
                    }

                    try {
                        return method.invoke(handler, args);
                    } catch (Throwable ex) {
                        throw new RuntimeException("invoke method error:" +
                                handler.getClass().getName() + "#" + method.getName(), ex);
                    }
                } else {
                    LogUtil.w("method not impl: " + eventMethod + "(" + handler.getClass().getSimpleName() + ")");
                }
            }
            return null;
        }
    }
}
