package com.android.launcher3.touch;

import android.util.FloatProperty;
import android.view.View;

public interface PagedOrientationHandler {
    FloatProperty<View> getPrimaryViewTranslate();

}
