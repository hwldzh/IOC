package com.tencent.mm.wxperformancetool.testioc.view;

import com.tencent.mm.wxperformancetool.testioc.x;

import org.xutils.view.annotation.ViewInject;


/**
 * Created by RunningH on 2017/7/28.
 */

public final class ViewInjectorImpl implements ViewInjector {
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
}
