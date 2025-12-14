package com.android.launcher3.statemanager;

import com.android.launcher3.LauncherState;

public class StateManager<STATE_TYPE extends BaseState<STATE_TYPE>, L extends StatefulActivity<LauncherState>> {
    public LauncherState mState;

    public STATE_TYPE getState() {
        return null;
    }

    public STATE_TYPE getCurrentStableState() {
        return null;
    }
}
