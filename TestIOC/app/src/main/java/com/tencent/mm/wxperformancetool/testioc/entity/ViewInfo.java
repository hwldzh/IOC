package com.tencent.mm.wxperformancetool.testioc.entity;

/**
 * Created by willenhuang on 2017/7/28.
 */

public class ViewInfo {
    public int value;
    public int parentId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ViewInfo viewInfo = (ViewInfo) object;

        if (value != viewInfo.value) return false;
        return parentId == viewInfo.parentId;

    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + parentId;
        return result;
    }
}
