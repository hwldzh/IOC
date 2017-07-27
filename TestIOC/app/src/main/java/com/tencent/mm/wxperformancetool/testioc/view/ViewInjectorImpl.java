package com.tencent.mm.wxperformancetool.testioc.view;

import com.tencent.mm.wxperformancetool.testioc.x;

/**
 * Created by willenhuang on 2017/7/27.
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
