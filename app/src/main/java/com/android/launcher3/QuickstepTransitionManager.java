package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.SurfaceControl;
import android.view.ViewRootImpl;
import android.view.animation.Interpolator;
import com.android.launcher3.uioverrides.QuickstepLauncher;

class ExponentialEaseOutInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float input) {
        return (float) (1 - Math.pow(2, -10 * input));
    }
}


public class QuickstepTransitionManager {

    protected final QuickstepLauncher mLauncher;

    public QuickstepTransitionManager(QuickstepLauncher mLauncher) {
        this.mLauncher = mLauncher;
    }

    public static class BlurAnimatorHandler implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        private final SurfaceControl mBlurLayer;
        private final SurfaceControl mDimLayer;
        private final SurfaceControl.Transaction mTransaction;
        private final float mMaxDimAmount;
        private final boolean mIsOpening;
        private boolean mIsCleaningUp = false;

        BlurAnimatorHandler(SurfaceControl blurLayer, SurfaceControl dimLayer, SurfaceControl.Transaction transaction, float maxDimAmount, boolean isOpening) {
            this.mBlurLayer = blurLayer;
            this.mDimLayer = dimLayer;
            this.mTransaction = transaction;
            this.mMaxDimAmount = maxDimAmount;
            this.mIsOpening = isOpening;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (mIsCleaningUp && animation.getDuration() != 0) {
                return;
            }
            float progress = (float) animation.getAnimatedValue();
            float blurRadius = progress * 40f;
            if (mBlurLayer != null && mBlurLayer.isValid()) {
                mTransaction.setBackgroundBlurRadius(mBlurLayer, (int) blurRadius);
                mTransaction.show(mBlurLayer);
            }
            float dimAlpha = progress * this.mMaxDimAmount;
            if (mDimLayer != null && mDimLayer.isValid()) {
                mTransaction.setAlpha(mDimLayer, dimAlpha);
                mTransaction.show(mDimLayer);
            }
            mTransaction.apply();
        }

        private void cleanup() {
            if (mBlurLayer != null && mBlurLayer.isValid()) {
                mTransaction.remove(mBlurLayer);
                mBlurLayer.release();
            }
            if (mDimLayer != null && mDimLayer.isValid()) {
                mTransaction.remove(mDimLayer);
                mDimLayer.release();
            }
            mTransaction.apply();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            cleanup();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            cleanup();
        }

        @Override public void onAnimationStart(Animator animation) {

        }
        @Override public void onAnimationRepeat(Animator animation) {

        }
    }
    public SurfaceControl getParentSurfaceControl() {
        ViewRootImpl viewRootImpl = mLauncher.getDragLayer().getViewRootImpl();
        if (viewRootImpl != null) {
            return viewRootImpl.getSurfaceControl();
        }
        return null;
    }

    public void addBlurAnimatorToSet(AnimatorSet animatorSet, boolean isAppOpening) {
        SurfaceControl parentSurface = getParentSurfaceControl();
        if (parentSurface == null) {
            return;
        }

        final float MAX_DIM_AMOUNT = 0.2f;

        final SurfaceControl blurLayer = new SurfaceControl.Builder()
                .setName("Blur-Layer")
                .setParent(parentSurface)
                .setEffectLayer()
                .build();

        final SurfaceControl dimLayer = new SurfaceControl.Builder()
                .setName("Dimming-Mask-Layer")
                .setParent(parentSurface)
                .setColorLayer()
                .build();

        final SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();

        transaction.setColor(dimLayer, new float[]{0f, 0f, 0f});
        transaction.setLayer(blurLayer, 1);
        transaction.setLayer(dimLayer, 2);

        ValueAnimator blurAnimator = isAppOpening ? ValueAnimator.ofFloat(0f, 1f) : ValueAnimator.ofFloat(1f, 0f);
        blurAnimator.setDuration(350L);
        blurAnimator.setInterpolator(new ExponentialEaseOutInterpolator());

        BlurAnimatorHandler handler = new BlurAnimatorHandler(blurLayer, dimLayer, transaction, MAX_DIM_AMOUNT, isAppOpening);
        blurAnimator.addUpdateListener(handler);
        blurAnimator.addListener(handler);

        animatorSet.play(blurAnimator);
    }

}