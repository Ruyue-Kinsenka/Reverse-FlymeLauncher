package com.android.quickstep.views;

import com.android.launcher3.ActionsView;
import com.android.launcher3.anim.AnimatedFloat;

public class OverviewActionsView {

    private AnimatedFloat[] mAlphaProperties;

    public AnimatedFloat getIndexScrollAlpha() {
        return this.mAlphaProperties[5];
    }

}
