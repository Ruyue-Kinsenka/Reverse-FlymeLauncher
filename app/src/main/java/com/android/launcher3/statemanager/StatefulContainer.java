package com.android.launcher3.statemanager;

public interface StatefulContainer<STATE_TYPE> {
    default void onStateSetStart(BaseState<STATE_TYPE> state) {
    }

    void closeOpenViewsOnState(STATE_TYPE state);

}
