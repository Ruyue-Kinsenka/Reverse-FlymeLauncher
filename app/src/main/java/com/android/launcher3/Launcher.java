package com.android.launcher3;

import static com.android.launcher3.LauncherState.OVERVIEW;

import android.view.View;

import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.states.RotationHelper;
import static com.android.launcher3.states.RotationHelper.REQUEST_ROTATE;
import java.util.Optional;

public abstract class Launcher extends StatefulActivity<LauncherState> {
    private boolean mDeferOverlayCallbacks;
    private Workspace mWorkspace;
    private StateManager<LauncherState, Launcher> mStateManager;
    private LauncherState mPrevLauncherState;
    private InstanceIdSequence.InstanceId mAllAppsSessionLogId;
    private View mOverviewPanel;
    private RotationHelper mRotationHelper;

    public Launcher() {
        this.mRotationHelper = new RotationHelper();
    }

    public void onStateSetStart(LauncherState state) {
        super.onStateSetStart((BaseState<LauncherState>) state);
        if (this.mDeferOverlayCallbacks) {
            scheduleDeferredCheck();
        }
        addActivityFlags(64);
        if (state == LauncherState.SPRING_LOADED || state == LauncherState.EDIT_MODE) {
            ItemInstallQueue.INSTANCE.lambda$get$1(this).pauseModelPush(4);
            getRotationHelper().setCurrentStateRequest(2);
            this.mWorkspace.showPageIndicatorAtCurrentScroll();
            this.mWorkspace.setClipChildren(false);
        } else if (state == OVERVIEW) {
            getRotationHelper().setCurrentStateRequest(REQUEST_ROTATE);
        }
        ((PageIndicator) this.mWorkspace.getPageIndicator()).setShouldAutoHide(!state.hasFlag(LauncherState.FLAG_MULTI_PAGE));
        this.mPrevLauncherState = this.mStateManager.getCurrentStableState();
        if (this.mPrevLauncherState != state && LauncherState.ALL_APPS.equals(state) && this.mAllAppsSessionLogId == null) {
            this.mAllAppsSessionLogId = new InstanceIdSequence().newInstanceId();
            if (getAllAppsEntryEvent().isPresent()) {
                getStatsLogManager().logger().withContainerInfo(LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(getWorkspace().getCurrentPage()).build()).build()).log(getAllAppsEntryEvent().get());
            }
        }
        updateDisallowBack();
    }

    public RotationHelper getRotationHelper() {
        return this.mRotationHelper;

    }

    public StatsLogManager getStatsLogManager() {
        return new StatsLogManager();
    }

    public Optional<Object> getAllAppsEntryEvent() {
        return Optional.empty();
    }

    public Workspace getWorkspace() {
        if (this.mWorkspace == null) {
            this.mWorkspace = new Workspace();
        }
        return this.mWorkspace;
    }

    private void updateDisallowBack() {
    }

    public <T extends View> T getOverviewPanel() {
        return (T) this.mOverviewPanel;
    }

}
