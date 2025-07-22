package com.android.quickstep;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;

import com.android.launcher3.QuickstepTransitionManager;

public class LauncherSwipeHandlerV2 {
    private QuickstepTransitionManager mTransitionManager;

    @SuppressLint({"NewApi"})
    private void startWindowCloseBlurAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        this.mTransitionManager.addBlurAnimatorToSet(animatorSet, true);
        animatorSet.start();
    }
}
