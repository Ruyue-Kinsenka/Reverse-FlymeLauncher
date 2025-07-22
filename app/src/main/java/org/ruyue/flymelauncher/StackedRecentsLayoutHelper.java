package org.ruyue.flymelauncher;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.DecelerateInterpolator;

import com.android.quickstep.views.RecentsView; // 假设 RecentsView 是 ViewGroup
import com.android.quickstep.views.TaskView;

public class StackedRecentsLayoutHelper {

    // --- Alpha Properties ---
    private float mBaseAlphaFactor = 1.0f;      // 基础透明度 (最前景卡片)
    private float mAlphaDecayFactor = 0.85f;    // 透明度衰减因子 (每层深度应用的乘数, 0.0-1.0, 越小衰减越快)

    // --- Animation Properties ---
    private long mAnimationDurationMs = 200L;    // 动画时长 (ms)
    private Interpolator mInterpolator = new DecelerateInterpolator(1.5f); // 动画插值器

    // --- Stacking Visual Properties (NEW) ---
    private float mStackedCardHorizontalOffsetDp = 30f; // 每层卡片的基础水平偏移量 (DP)
    private float mStackedCardVerticalOffsetDp = 8f;   // 每层卡片的基础垂直偏移量 (DP, 通常是向上偏移)
    private float mStackedCardBaseScale = 0.95f;       // 紧随前景卡片之后的第一张卡片的缩放比例
    private float mStackedCardScaleDecrement = 0.04f;  // 每向后一层，缩放比例的额外递减值
    private int mMaxBackgroundVisualDepth = 3;       // 背景卡片最大可见深度 (影响视觉效果计算的层数)

    // --- RTL State ---
    private boolean mIsRtl = false; // 是否为从右到左的布局

    // --- Pixel values (to be initialized) ---
    private float mStackedCardHorizontalOffsetPx = 0f;
    private float mStackedCardVerticalOffsetPx = 0f;
    private boolean mInitialized = false; // 标记DP值是否已转换为PX

    public StackedRecentsLayoutHelper() {
        // 构造函数
    }

    // --- Initialization ---
    private void initializeIfNeeded(Context context) {
        if (!mInitialized && context != null) {
            mStackedCardHorizontalOffsetPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, mStackedCardHorizontalOffsetDp,
                    context.getResources().getDisplayMetrics());
            mStackedCardVerticalOffsetPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, mStackedCardVerticalOffsetDp,
                    context.getResources().getDisplayMetrics());
            mInitialized = true;
        }
    }

    // --- Setter Methods ---
    public void setBaseAlphaFactor(float factor) { this.mBaseAlphaFactor = Math.max(0f, Math.min(1f, factor)); }
    public void setAlphaDecayFactor(float factor) { this.mAlphaDecayFactor =  Math.max(0f, Math.min(1f, factor)); }
    public void setAnimationDuration(long durationMs) { this.mAnimationDurationMs = Math.max(0, durationMs); }
    public void setInterpolator(Interpolator interpolator) { this.mInterpolator = interpolator; }
    public void setMaxBackgroundVisualDepth(int depth) { this.mMaxBackgroundVisualDepth = Math.max(1, depth); }

    public void setStackedCardHorizontalOffsetDp(float offsetDp) { this.mStackedCardHorizontalOffsetDp = offsetDp; this.mInitialized = false; }
    public void setStackedCardVerticalOffsetDp(float offsetDp) { this.mStackedCardVerticalOffsetDp = offsetDp; this.mInitialized = false; }
    public void setStackedCardBaseScale(float scale) { this.mStackedCardBaseScale = Math.max(0.1f, Math.min(1f, scale)); }
    public void setStackedCardScaleDecrement(float decrement) { this.mStackedCardScaleDecrement = Math.max(0f, decrement); }
    public void setIsRtl(boolean isRtl) { this.mIsRtl = isRtl; }


    /**
     * 应用iOS风格的堆叠布局到RecentsView中的任务卡片。
     * 此方法应替代原有的 updatePageOffsetsForFlyme() 的核心逻辑。
     *
     * @param recentsView      包含TaskView子项的RecentsView实例。
     * @param focusedTaskIndex 当前获取焦点（即视觉上在最前方）的任务卡片的索引。
     *                         如果为-1，则可能没有运行中的任务或焦点不明确，可以考虑默认行为或不处理。
     */

    public void applyStackLayout(RecentsView recentsView, int focusedTaskIndex) {
        if (recentsView == null) return;

        int childCount = recentsView.getChildCount();
        if (childCount <= 0) return;

        focusedTaskIndex = Math.max(0, Math.min(focusedTaskIndex, childCount - 1));

        for (int i = 0; i < childCount; i++) {
            View child = recentsView.getChildAt(i);
            if (!(child instanceof TaskView)) continue;

            child.animate().cancel();

            int depth = i - focusedTaskIndex;
            int effectiveDepth = depth > 0
                    ? Math.min(depth, mMaxBackgroundVisualDepth)
                    : 0;

            float targetAlpha = mBaseAlphaFactor
                    * (float) Math.pow(mAlphaDecayFactor, effectiveDepth);
            targetAlpha = Math.max(0f, Math.min(1f, targetAlpha));

            child.animate()
                    .alpha(targetAlpha)
                    .setInterpolator(mInterpolator)
                    .setDuration(mAnimationDurationMs)
                    .start();
        }
    }
    public void applyIOSStyleStackLayout(RecentsView recentsView, int focusedTaskIndex) {
        if (recentsView == null) {
            return;
        }
        // 确保DP值已转换为PX值
        initializeIfNeeded(recentsView.getContext());

        int childCount = recentsView.getChildCount();
        if (childCount <= 0) {
            return;
        }

        // 如果 focusedTaskIndex 无效 (例如 -1), 可以选择一个默认焦点或直接返回
        if (focusedTaskIndex < 0 || focusedTaskIndex >= childCount) {
            // 可以设置一个默认焦点，比如第一个或中间的，或者根据具体情况处理
            // focusedTaskIndex = 0; // 或者 childCount / 2;
            // 为简单起见，如果焦点无效，我们暂时不应用特殊堆叠，让它们保持原样或默认状态
            // 或者，可以给所有卡片一个基础的非堆叠排列
            for (int i = 0; i < childCount; i++) {
                View child = recentsView.getChildAt(i);
                if (child instanceof TaskView) {
                    child.animate().cancel();
                    child.animate()
                            .translationX(0)
                            .translationY(0)
                            .scaleX(1)
                            .scaleY(1)
                            .alpha(1)
                            .setInterpolator(mInterpolator)
                            .setDuration(mAnimationDurationMs)
                            .start();
                }
            }
            return;
        }


        for (int i = 0; i < childCount; i++) {
            View child = recentsView.getChildAt(i);
            if (!(child instanceof TaskView)) {
                continue;
            }

            TaskView taskView = (TaskView) child;
            taskView.animate().cancel(); // 取消任何正在进行的动画，以应用新的状态

            // depth: 0 表示当前焦点卡片, >0 表示在焦点卡片之后 (堆叠在下方), <0 表示在焦点卡片之前 (已划过)
            int depth = i - focusedTaskIndex;

            float targetTranslationX = 0f;
            float targetTranslationY = 0f;
            float targetScale = 1f;
            float targetAlpha = mBaseAlphaFactor; // 默认是基础透明度

            if (depth == 0) { // 当前焦点卡片
                // 保持原位，最大尺寸，基础透明度
                targetTranslationX = 0f;
                targetTranslationY = 0f;
                targetScale = 1f;
                targetAlpha = mBaseAlphaFactor;
            } else if (depth > 0) { // 在焦点卡片之后的卡片 (堆叠在下方/后方)
                // effectiveDepth 用于计算视觉效果，不超过最大可见深度
                int effectiveDepth = Math.min(depth, mMaxBackgroundVisualDepth);

                // 水平偏移: 根据深度和方向进行偏移
                targetTranslationX = effectiveDepth * mStackedCardHorizontalOffsetPx;
                if (mIsRtl) {
                    targetTranslationX *= -1; // RTL模式下反向偏移
                }

                // 垂直偏移: 通常是轻微向上偏移
                targetTranslationY = -effectiveDepth * mStackedCardVerticalOffsetPx; // 负值表示向上

                // 缩放: 越深越小
                targetScale = mStackedCardBaseScale;
                if (effectiveDepth > 1) { // 从第二层堆叠卡片开始应用额外的缩减
                    targetScale -= (effectiveDepth - 1) * mStackedCardScaleDecrement;
                }
                targetScale = Math.max(0.1f, targetScale); // 防止缩放过小

                // 透明度: 越深越透明
                // mAlphaDecayFactor (0.85): depth 1 -> 1*0.85, depth 2 -> 1*0.85*0.85
                targetAlpha = mBaseAlphaFactor;
                for(int d=0; d<effectiveDepth; ++d) {
                    targetAlpha *= mAlphaDecayFactor;
                }
                targetAlpha = Math.max(0f, targetAlpha); // 防止透明度为负

            } else { // 在焦点卡片之前的卡片 (depth < 0, 已经划过的卡片)
                // 这些卡片通常会向相反方向移出屏幕
                float taskWidth = taskView.getWidth();
                if (taskWidth == 0 && recentsView.getWidth() > 0) { // 尝试获取父容器宽度作为估算
                    taskWidth = recentsView.getWidth() / 1.2f; // 估算值
                } else if (taskWidth == 0) {
                    taskWidth = 300; // 最终备用估算值
                }

                // 将卡片向其“划出”方向大幅度偏移
                // 'depth' is negative here.
                targetTranslationX = depth * (taskWidth + mStackedCardHorizontalOffsetPx);
                if (mIsRtl) {
                    // 对于RTL，"之前"的卡片在视觉右侧，所以负depth乘以正偏移，需要反转。
                    // 或者说，depth * (negative effective screen width)
                    targetTranslationX = depth * -(taskWidth + mStackedCardHorizontalOffsetPx);
                }

                // 可以让这些卡片也缩小并完全透明
                targetScale = mStackedCardBaseScale - mMaxBackgroundVisualDepth * mStackedCardScaleDecrement;
                targetScale = Math.max(0.1f, targetScale);
                targetAlpha = 0f; // 完全透明
            }

            // 应用计算出的变换属性
            taskView.animate()
                    .translationX(targetTranslationX)
                    .translationY(targetTranslationY)
                    .scaleX(targetScale)
                    .scaleY(targetScale)
                    .alpha(targetAlpha)
                    .setInterpolator(mInterpolator)
                    .setDuration(mAnimationDurationMs)
                    .start();
        }
    }
}