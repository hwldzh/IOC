package com.tencent.mm.wxperformancetool.testioc.utils;

import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.view.View;

import com.tencent.mm.wxperformancetool.testioc.entity.ViewInfo;


/**
 * Created by willenhuang on 2017/7/28.
 */

public class ViewFinder {
    private Activity mActivity;

    public ViewFinder(Activity activity) {
        this.mActivity = activity;
    }
    public View findViewById(int id, int parentId) {
        View parentView = null;
        if (parentId > 0) {
            parentView = this.findViewById(parentId);
        }
        if (parentView != null) {
            return parentView.findViewById(id);
        } else {
            return this.findViewById(id);
        }
    }

    public View findViewById(int id) {
        if (mActivity != null) {
            return mActivity.findViewById(id);
        }
        return null;
    }

    public View findViewByInfo(ViewInfo viewInfo) {
        return findViewById(viewInfo.value, viewInfo.parentId);
    }
}
