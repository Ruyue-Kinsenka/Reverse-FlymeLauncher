package com.android.launcher3.statemanager;

import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;

public class StatefulActivity<STATE_TYPE> extends BaseActivity {

    public boolean isInState(STATE_TYPE state) {
        return true;
    }


}
