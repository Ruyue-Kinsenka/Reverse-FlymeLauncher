package com.meizu.flyme.launcher;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.quickstep.views.RecentsView;
import com.meizu.flyme.launcher.states.LauncherStateManager;

public abstract class MzLauncher extends Launcher {
    public LauncherStateManager getStateManager() {
        return null;
    }

    public FlymeDragLayer getDragLayer() {
        return null;
    }


    public void getViewRootImpl() {
        return ;
    }

    public boolean isInState(LauncherState overview) {
        return true;
    }


    public void onStateSetStart(LauncherState state) {
    }
}
