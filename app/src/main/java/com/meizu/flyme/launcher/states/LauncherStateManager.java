package com.meizu.flyme.launcher.states;

import com.android.launcher3.LauncherState;
import com.android.launcher3.statemanager.StateManager;

public class LauncherStateManager extends StateManager<LauncherState, com.android.launcher3.Launcher> {
    public LauncherState getCurrentStableState() {
        return new LauncherState();
    }
}
