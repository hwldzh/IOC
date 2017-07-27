package com.tencent.mm.wxperformancetool.testioc;

import com.tencent.mm.wxperformancetool.testioc.view.ViewInjector;
import com.tencent.mm.wxperformancetool.testioc.view.ViewInjectorImpl;

/**
 * Created by willenhuang on 2017/7/27.
 */

public final class x {
    public static ViewInjector view() {
        if (Ext.viewInjector == null) {
            ViewInjectorImpl.registerInstance();
        }
        return Ext.viewInjector;
    }

    public static class Ext{
        private static ViewInjector viewInjector;

        public static void setViewInjector(ViewInjector viewInjector) {
            Ext.viewInjector = viewInjector;
        }
    }
}
