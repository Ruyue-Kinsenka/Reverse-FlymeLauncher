package com.android.launcher3.uioverrides;

import com.android.launcher3.LauncherState;
import com.android.quickstep.views.RecentsView;
import com.meizu.flyme.launcher.MzLauncher;

public abstract class QuickstepLauncher extends MzLauncher {
    private static final LauncherState OVERVIEW = null;

    @Override
    public void onStateSetStart(LauncherState state) {
        super.onStateSetStart(state);
        if (state == LauncherState.OVERVIEW) {
            RecentsView recentsView = getOverviewPanel();
            recentsView.getPagedOrientationHandler(); // Ensure handler is initialized
            recentsView.getPagedViewOrientedState().ignoreAllowHomeRotationPreference();
        }
    }

}
