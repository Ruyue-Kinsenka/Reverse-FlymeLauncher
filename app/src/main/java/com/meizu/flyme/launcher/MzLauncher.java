package com.meizu.flyme.launcher;

import com.android.launcher3.LauncherState;
import com.meizu.flyme.launcher.states.LauncherStateManager;

public class MzLauncher {
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
}
