package com.android.launcher3;

import com.android.launcher3.statemanager.BaseState;

public class LauncherState extends BaseState<LauncherState> {
    public static final LauncherState SPRING_LOADED = new LauncherState();
    public static final LauncherState EDIT_MODE = new LauncherState();
    public static final LauncherState ALL_APPS = new LauncherState();
    public static final int FLAG_MULTI_PAGE = 1;
    public static final LauncherState OVERVIEW = null;


    public boolean hasFlag(int flag) {
        return false;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }
}
