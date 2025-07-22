package org.ruyue.flymelauncher;

import android.content.Context;
import android.view.View;
import android.animation.ObjectAnimator;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;


public class StackedRecentsAppLayoutHelper {

    private static final float STACK_X_OFFSET_DP = 20f;
    private static final float STACK_Y_OFFSET_DP = -15f;
    private static final float STACK_SCALE_DECREMENT = 0.05f;
    private static final float STACK_MIN_SCALE = 0.85f;
    private static final float STACK_ALPHA_DECREMENT = 0.15f;
    private static final float STACK_MIN_ALPHA = 0.7f;
    private static final float STACK_Z_OFFSET_DP = -2f;
    private static final float STACK_BASE_TRANSLATION_Z_DP = 10f;
    private static final int STACK_MAX_VISIBLE_BEHIND = 2;

    private final float mStackXOffsetPx;
    private final float mStackYOffsetPx;
    private final float mStackZOffsetPx;
    private final float mStackBaseTranslationZPx;

    private boolean mHasAppliedStackingPreviously = false;


    public StackedRecentsAppLayoutHelper(Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        mStackXOffsetPx = STACK_X_OFFSET_DP * density;
        mStackYOffsetPx = STACK_Y_OFFSET_DP * density;
        mStackZOffsetPx = STACK_Z_OFFSET_DP * density;
        mStackBaseTranslationZPx = STACK_BASE_TRANSLATION_Z_DP * density;
    }


    public void applyStackingLayout(RecentsView recentsView, boolean animate) {
        if (recentsView.getTaskViewCount() == 0) {
            mHasAppliedStackingPreviously = false;
            return;
        }

        int focusedTaskIndex = recentsView.getNextPage();

        for (int i = 0; i < recentsView.getChildCount(); i++) {
            View child = recentsView.getChildAt(i);
            if (!(child instanceof TaskView)) {
                continue;
            }
            TaskView taskView = (TaskView) child;

            int relativeIndex = recentsView.indexOfChild(taskView) - focusedTaskIndex;

            float targetTranslationX = 0f;
            float targetTranslationY = 0f;
            float targetScale = 1.0f;
            float targetAlpha = 1.0f;
            float targetTranslationZ = 0f;
            boolean isVisible = true;

            if (relativeIndex == 0) { // 焦点卡片
                targetTranslationX = 0f;
                targetTranslationY = 0f;
                targetScale = 1.0f;
                targetAlpha = 1.0f;
                targetTranslationZ = mStackBaseTranslationZPx;
            } else if (relativeIndex > 0) { // 右侧堆叠
                if (relativeIndex <= STACK_MAX_VISIBLE_BEHIND) {
                    targetTranslationX = relativeIndex * mStackXOffsetPx;
                    targetTranslationY = relativeIndex * mStackYOffsetPx;
                    targetScale = Math.max(STACK_MIN_SCALE, 1.0f - (relativeIndex * STACK_SCALE_DECREMENT));
                    targetAlpha = Math.max(STACK_MIN_ALPHA, 1.0f - (relativeIndex * STACK_ALPHA_DECREMENT));
                    targetTranslationZ = mStackBaseTranslationZPx + (relativeIndex * mStackZOffsetPx);
                } else { // 超出范围
                    isVisible = false;
                    targetAlpha = 0f;
                    targetTranslationX = (STACK_MAX_VISIBLE_BEHIND + 1) * mStackXOffsetPx;
                    targetTranslationY = (STACK_MAX_VISIBLE_BEHIND + 1) * mStackYOffsetPx;
                    targetScale = STACK_MIN_SCALE - 0.1f;
                    targetTranslationZ = mStackBaseTranslationZPx + ((STACK_MAX_VISIBLE_BEHIND + 1) * mStackZOffsetPx);
                }
            } else { // 左侧
                targetTranslationX = relativeIndex * taskView.getWidth() * 0.9f;
                targetTranslationY = 0f;
                targetScale = 1.0f;
                targetAlpha = 1.0f;
                targetTranslationZ = mStackBaseTranslationZPx + (relativeIndex * mStackZOffsetPx);
                if (targetTranslationX + taskView.getWidth() < 0 && !recentsView.isPageInTransition()) {
                    targetAlpha = 0.2f;
                }
            }
            taskView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
            applyTransformationsInternal(taskView, targetTranslationX, targetTranslationY, targetScale, targetAlpha, targetTranslationZ, animate, true);
        }
        mHasAppliedStackingPreviously = true;
    }


    public void resetViewsForDefaultLayout(RecentsView recentsView, boolean animate) {
        if (!mHasAppliedStackingPreviously && !animate) {
        }

        for (int i = 0; i < recentsView.getChildCount(); i++) { // 遍历所有子View
            View child = recentsView.getChildAt(i);
            if (!(child instanceof TaskView)) {

                continue;
            }
            TaskView taskView = (TaskView) child;

            applyTransformationsInternal(taskView, 0f, 0f, 1f, 1f, 0f, animate, false);
        }
        mHasAppliedStackingPreviously = false;
    }


    private void applyTransformationsInternal(TaskView taskView, float transX, float transY, float scale, float alpha, float transZ, boolean animate, boolean isStackingEffectCall) {
        long duration = animate ? 150 : 0; // 动画时长
        DecelerateInterpolator interpolator = new DecelerateInterpolator();


        ViewPropertyAnimator animator = taskView.animate();
        animator.translationX(transX)
                .translationY(transY)
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(duration)
                .setInterpolator(interpolator);

        if (isStackingEffectCall) {
            animator.alpha(alpha);
        } else {

            if (alpha == 1.0f && taskView.getAlpha() != 1.0f) {
                animator.alpha(1.0f);
            }
        }
        animator.start();

        if (animate && taskView.getTranslationZ() != transZ) {
            if (isStackingEffectCall || (!isStackingEffectCall && transZ == 0f)) {
                ObjectAnimator.ofFloat(taskView, View.TRANSLATION_Z, transZ).setDuration(duration).setInterpolator(interpolator);
            }
        } else if (!animate) {
            if (isStackingEffectCall || (!isStackingEffectCall && transZ == 0f)) {
                taskView.setTranslationZ(transZ);
            }
        }
    }

    public boolean hasAppliedStackingPreviously() {
        return mHasAppliedStackingPreviously;
    }
}