package com.ruyue.flymelauncher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class GlobalBlurController {
    private static volatile GlobalBlurController sInstance;
    private ValueAnimator mCurrentAnimator;
    private float mCurrentRadius = 0f;
    private static final ColorMatrixColorFilter EMPTY_COLOR_FILTER =
            new ColorMatrixColorFilter(new ColorMatrix());
    private static final float UPDATE_THRESHOLD = 3f; // 更新频率
    private static float MAX_BLUR_RADIUS = 90f;       // 模糊半径
    private static long FORWARD_DURATION = 310L;      // 正向动画时间
    private static long REVERSE_DURATION = 100L;      // 反向动画时间
    private static long OVERLAY_DURATION = 2000L;     // 遮罩延迟隐藏时间
    private static float BLACK_VATE = 0.5f;           // 黑色遮罩强度

    private static final RenderEffect BASE_EFFECT =
            RenderEffect.createColorFilterEffect(EMPTY_COLOR_FILTER);

    private int mStepCount;
    private RenderEffect[] mBlurEffects;
    private final WeakHashMap<ViewGroup, Integer> mOriginalLayerTypes = new WeakHashMap<>();
    private View mDimOverlay;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mHideRunnable;

    private GlobalBlurController() {
        initEffects();
    }

/// 签名复用
    public void startBlurAnimation(ViewGroup rootView, long duration) {
        FORWARD_DURATION = duration;
        startBlurAnimation(rootView);
    }


    private void initEffects() {
        mStepCount = (int) Math.ceil(MAX_BLUR_RADIUS / UPDATE_THRESHOLD) + 1;
        mBlurEffects = new RenderEffect[mStepCount];
        for (int i = 0; i < mStepCount; i++) {
            float radius = i * UPDATE_THRESHOLD;
            mBlurEffects[i] = RenderEffect.createBlurEffect(
                    radius, radius, BASE_EFFECT, Shader.TileMode.MIRROR);
        }
    }

    public static GlobalBlurController getInstance() {
        if (sInstance == null) {
            synchronized (GlobalBlurController.class) {
                if (sInstance == null) {
                    sInstance = new GlobalBlurController();
                }
            }
        }
        return sInstance;
    }

    // fix not found method
    public static void setMaxBlurRadius(float radius) {
        MAX_BLUR_RADIUS = radius;
        if (sInstance != null) sInstance.initEffects();
    }
    public static void setForwardDuration(long d) {
        FORWARD_DURATION = d;
    }
    public static void setReverseDuration(long d) {
        REVERSE_DURATION = d;
    }
    public static void setOverlayDuration(long d) {
        OVERLAY_DURATION = d;
    }
    public static void setBlackVate(float v) {
        BLACK_VATE = v;
    }

    public void startBlurAnimation(ViewGroup rootView) {
        if (rootView == null) return;
        if (mHideRunnable != null) {
            mHandler.removeCallbacks(mHideRunnable);
            mHideRunnable = null;
        }
        ensureDimOverlay(rootView);
        mDimOverlay.setVisibility(View.VISIBLE);

        prepareLayerForAnimation(rootView);
        cancelCurrentAnimation();

        ValueAnimator forward = ValueAnimator.ofFloat(0f, MAX_BLUR_RADIUS);
        forward.setDuration(FORWARD_DURATION);
        forward.addUpdateListener(anim -> {
            float r = (float) anim.getAnimatedValue();
            applyCachedBlurEffect(rootView, r);
            updateDimAlpha(r);
        });
        forward.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) { startReverseBlur(rootView); }
            @Override public void onAnimationCancel(Animator animation) { resetEffect(rootView); }
        });

        mCurrentAnimator = forward;
        forward.start();
    }
    // ai
    private void startReverseBlur(ViewGroup rootView) {
        cancelCurrentAnimation();
        ValueAnimator reverse = ValueAnimator.ofFloat(MAX_BLUR_RADIUS, 0f);
        reverse.setDuration(REVERSE_DURATION);
        reverse.addUpdateListener(anim -> {
            float r = (float) anim.getAnimatedValue();
            applyCachedBlurEffect(rootView, r);
            updateDimAlpha(r);
        });
        reverse.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) { resetEffect(rootView); }
            @Override public void onAnimationCancel(Animator animation) { resetEffect(rootView); }
        });
        mCurrentAnimator = reverse;
        reverse.start();
    }
    // ai
    private void prepareLayerForAnimation(ViewGroup rootView) {
        mOriginalLayerTypes.put(rootView, rootView.getLayerType());
        if (rootView.getLayerType() != View.LAYER_TYPE_HARDWARE) {
            rootView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }
    // ai
    private void applyCachedBlurEffect(ViewGroup target, float radius) {
        if (Math.abs(radius - mCurrentRadius) < UPDATE_THRESHOLD / 2f) return;
        try {
            int index = Math.min(mStepCount - 1, (int) Math.round(radius / UPDATE_THRESHOLD));
            target.setRenderEffect(mBlurEffects[index]);
            mCurrentRadius = radius;
        } catch (Exception e) {
            Log.e("GlobalBlurController", "apply blur failed", e);
        }
    }
    // black overviwe
    private void updateDimAlpha(float radius) {
        if (mDimOverlay != null) {
            float alpha = (radius / MAX_BLUR_RADIUS) * BLACK_VATE;
            mDimOverlay.setAlpha(alpha);
        }
    }

    private void cancelCurrentAnimation() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
            mCurrentAnimator.removeAllListeners();
            mCurrentAnimator.removeAllUpdateListeners();
            mCurrentAnimator = null;
        }
    }
    private void ensureDimOverlay(ViewGroup rootView) {
        if (mDimOverlay == null) {
            ViewGroup decor = (ViewGroup) rootView.getRootView();
            mDimOverlay = new View(decor.getContext());
            mDimOverlay.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mDimOverlay.setBackgroundColor(Color.BLACK);
            mDimOverlay.setAlpha(0f);
            mDimOverlay.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            decor.addView(mDimOverlay);
        }
    }
    // ai
    private void resetEffect(ViewGroup target) {
        try {
            target.setRenderEffect(null);
            Integer orig = mOriginalLayerTypes.remove(target);
            if (orig != null && target.getLayerType() != orig) {
                target.setLayerType(orig, null);
            }
        } catch (Exception e) {
            Log.e("GlobalBlurController", "reset failed", e);
        } finally {
            mCurrentRadius = 0f;
            scheduleHideOverlay();
        }
    }
    // ai
    private void scheduleHideOverlay() {
        if (mDimOverlay == null) return;
        if (mHideRunnable != null) mHandler.removeCallbacks(mHideRunnable);
        mHideRunnable = () -> mDimOverlay.setVisibility(View.GONE);
        mHandler.postDelayed(mHideRunnable, OVERLAY_DURATION);
    }
}
