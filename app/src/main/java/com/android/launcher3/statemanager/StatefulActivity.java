package com.android.launcher3.statemanager;

import com.android.launcher3.BaseActivity;
import com.android.launcher3.Launcher;

public abstract class StatefulActivity<STATE_TYPE> extends BaseActivity implements StatefulContainer<STATE_TYPE> {

    public boolean isInState(STATE_TYPE state) {
        return true;
    }


    public void onStateSetStart(BaseState<STATE_TYPE> state) {
        // StatefulContainer.super.onStateSetStart(state);
    }
}
