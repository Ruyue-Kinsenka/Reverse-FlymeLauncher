package com.ruyue.flymelauncher;

import android.view.animation.Interpolator;

class ExponentialEaseOutInterpolator implements Interpolator {
    //插值器_risingos_kde
    @Override
    public float getInterpolation(float input) {
        return (float) (1 - Math.pow(2, -10 * input));
    }
}