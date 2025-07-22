package com.android.launcher3.config;

import java.net.CookieHandler;
import java.util.function.BiPredicate;
import com.android.launcher3.uioverrides.flags.FlagsFactory;

public class FeatureFlags {
    public static final BooleanFlag ENABLE_GRID_ONLY_OVERVIEW = null;

    public static class BooleanFlag {
        private final boolean mCurrentValue;

        public BooleanFlag(boolean currentValue) {
            this.mCurrentValue = currentValue;
        }

        public boolean get() {
            return false;
        }
    }


}
