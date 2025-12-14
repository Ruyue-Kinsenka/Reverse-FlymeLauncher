package com.android.launcher3;

import android.app.Activity;

public class BaseActivity extends Activity {
    public DeviceProfile getDeviceProfile() {
        return new DeviceProfile(0);
    }

    public void addActivityFlags(int flags) {
    }

    protected void scheduleDeferredCheck() {
    }
}
