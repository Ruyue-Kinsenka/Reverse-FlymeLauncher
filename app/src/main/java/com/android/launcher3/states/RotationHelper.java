package com.android.launcher3.states;

import android.os.Handler;
import android.os.Message;

public class RotationHelper {
    private boolean mInitialized;
    private boolean mDestroyed;
    private int mStateHandlerRequest;
    private int mCurrentTransitionRequest;
    private int mCurrentStateRequest;
    private boolean mIgnoreAutoRotateSettings;
    private boolean mHomeRotationEnabled;
    private boolean mForceAllowRotationForTesting;
    private int mLastActivityFlags;
    private Handler mRequestOrientationHandler;
    public static final int REQUEST_ROTATE = 1;

    public RotationHelper() {
        this.mInitialized = false;
        this.mDestroyed = false;
        this.mStateHandlerRequest = 0;
        this.mCurrentTransitionRequest = 0;
        this.mCurrentStateRequest = 0;
        this.mIgnoreAutoRotateSettings = false;
        this.mHomeRotationEnabled = false;
        this.mForceAllowRotationForTesting = false;
        this.mLastActivityFlags = -1;
        this.mRequestOrientationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };
    }

    public void setCurrentStateRequest(int request) {
        this.mCurrentStateRequest = request;
        notifyChange();
    }

    private void notifyChange() {
        int activityFlags;
        if (!this.mInitialized || this.mDestroyed) {
            return;
        }
        if (this.mStateHandlerRequest != 0) {
            activityFlags = this.mStateHandlerRequest != 2 ? -1 : 14;
        } else {
            int activityFlags2 = this.mCurrentTransitionRequest;
            if (activityFlags2 != 0) {
                activityFlags = this.mCurrentTransitionRequest != 2 ? -1 : 14;
            } else {
                int activityFlags3 = this.mCurrentStateRequest;
                if (activityFlags3 == 2) {
                    activityFlags = 14;
                } else if (this.mIgnoreAutoRotateSettings || this.mCurrentStateRequest == 1 || this.mHomeRotationEnabled || this.mForceAllowRotationForTesting) {
                    activityFlags = -1;
                } else {
                    activityFlags = 5;
                }
            }
        }
        if (activityFlags == -1) {
            activityFlags = 5;
        }
        if (activityFlags != this.mLastActivityFlags) {
            this.mLastActivityFlags = activityFlags;
            this.mRequestOrientationHandler.sendEmptyMessage(activityFlags);
        }
    }

    public void initialize() {
        this.mInitialized = true;
        notifyChange();
    }

    public void destroy() {
        this.mDestroyed = true;
    }

    public void setIgnoreAutoRotateSettings(boolean ignore) {
        this.mIgnoreAutoRotateSettings = ignore;
        notifyChange();
    }

    public void setHomeRotationEnabled(boolean enabled) {
        this.mHomeRotationEnabled = enabled;
        notifyChange();
    }

    public void setForceAllowRotationForTesting(boolean force) {
        this.mForceAllowRotationForTesting = force;
        notifyChange();
    }

    public void setStateHandlerRequest(int request) {
        this.mStateHandlerRequest = request;
        notifyChange();
    }

    public void setCurrentTransitionRequest(int request) {
        this.mCurrentTransitionRequest = request;
        notifyChange();
    }
}
