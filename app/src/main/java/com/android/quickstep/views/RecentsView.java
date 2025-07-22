package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.FloatProperty;
import android.view.View;
import android.widget.Toast;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.PagedView;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.IntSet;
import com.android.quickstep.GestureState;
import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.util.TaskVisualsChangeListener;
import com.android.launcher3.config.FeatureFlags;
import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.util.TaskViewSimulator;
import com.meizu.flyme.launcher.quickstep.special.FixFor1214345;

import org.ruyue.flymelauncher.StackedRecentsAppLayoutHelper;

import java.util.function.Consumer;

public class RecentsView<ACTIVITY_TYPE extends StatefulActivity<STATE_TYPE>, STATE_TYPE extends BaseState<STATE_TYPE>> extends PagedView implements Insettable, TaskVisualsChangeListener {

    private float mAdjacentPageHorizontalOffset;
    private StackedRecentsAppLayoutHelper mStackedRecentsHelper;

    private ClearAllButton mClearAllButton;
    private float mGridProgress;
    protected int mRunningTaskViewId;
    protected boolean mRunningTaskTileHidden;
    protected PagedOrientationHandler mOrientationHandler;
    protected final ACTIVITY_TYPE mActivity;
    private final IntSet mTopRowIdSet;

    protected final Rect mLastComputedTaskSize;
    protected final Rect mLastComputedDesktopTaskSize;
    protected final Rect mLastComputedGridSize;
    protected final Rect mLastComputedGridTaskSize;

    private final Toast mSplitUnsupportedToast;

    private float mTaskGridVerticalDiff;
    private float mTopBottomRowHeightDiff;
    private int mClearAllShortTotalWidthTranslation;

    protected int mTaskWidth;
    private boolean mShowEmptyMessage;
    private boolean mGestureActive;
    protected GestureState.GestureEndTarget mCurrentGestureEndTarget;


    public RecentsView(Context context, ACTIVITY_TYPE mActivity, IntSet mTopRowIdSet, Rect mLastComputedTaskSize, Rect mLastComputedDesktopTaskSize, Rect mLastComputedGridSize, Rect mLastComputedGridTaskSize, Toast mSplitUnsupportedToast, Rect mTempRect) {
        super(context);
        mStackedRecentsHelper = new StackedRecentsAppLayoutHelper(context);
        this.mActivity = mActivity;
        this.mTopRowIdSet = mTopRowIdSet;
        this.mLastComputedTaskSize = mLastComputedTaskSize;
        this.mLastComputedDesktopTaskSize = mLastComputedDesktopTaskSize;
        this.mLastComputedGridSize = mLastComputedGridSize;
        this.mLastComputedGridTaskSize = mLastComputedGridTaskSize;
        this.mSplitUnsupportedToast = mSplitUnsupportedToast;
        this.mTempRect = mTempRect;
    }

    public boolean isSplitSelectionActive() {
        return true;
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        dispatchScrollChanged();
    }


    public void dispatchScrollChanged() {
    }

    private void updatePageTransformationsRuyue(boolean animate) {
        if (mShowEmptyMessage || getTaskViewCount() == 0) {
            if (mStackedRecentsHelper.hasAppliedStackingPreviously()) {
                mStackedRecentsHelper.resetViewsForDefaultLayout(this, animate);
            }
            return;
        }

        if (shouldApplyStackingEffectRuyue()) {
            mStackedRecentsHelper.applyStackingLayout(this, animate);
            mAdjacentPageHorizontalOffset = 0f;
        } else {
            if (mStackedRecentsHelper.hasAppliedStackingPreviously()) {
                mStackedRecentsHelper.resetViewsForDefaultLayout(this, animate);
            }
        }
    }

    public boolean shouldApplyStackingEffectRuyue() {
        return !mShowEmptyMessage && getTaskViewCount() > 0 &&
                !showAsGrid() &&
                !isSplitSelectionActive() &&
                mTaskModalness == 0.0f &&
                !mGestureActive &&
                (mCurrentGestureEndTarget == null || mCurrentGestureEndTarget == GestureState.GestureEndTarget.RECENTS);
    }



    public int getChildCount() {
        return 0;
    }

    public View getChildAt(int i) {
        return null;
    }

    public boolean isRtl() {
        return true;
    }


    public void invalidate() {
    }

    public void invalidateTaskList() {
    }

    public int indexOfChild(View tv) {
        return 0;
    }

    public float getContentAlpha() {
        return 0;
    }

    public View getClearAllButton() {
        return null;
    }

    public int getTaskViewCount() {
        return 0;
    }

    private void updateGridProperties(boolean isTaskDismissal, int startRebalanceAfter) {
        int bottomRowWidth;
        float bottomAccumulatedTranslationX;
        DeviceProfile deviceProfile;
        int focusedTaskWidthAndSpacing;
        int i;
        float shorterRowCompensation;
        int bottomRowWidth2;
        int i2;
        int taskTopMargin;
        boolean isTopRow;
        int focusedTaskWidthAndSpacing2;
        int focusedTaskShift;
        IntSet bottomSet;
        int taskWidthAndSpacing;
        int bottomRowWidth3;
        int focusedTaskWidthAndSpacing3;
        int focusedTaskWidthAndSpacing4;
        int focusedTaskShift2;
        int taskCount = getTaskViewCount();
        if (taskCount == 0) {
            return;
        }
        DeviceProfile deviceProfile2 = this.mActivity.getDeviceProfile();
        int taskTopMargin2 = deviceProfile2.overviewTaskThumbnailTopMarginPx;
        IntSet topSet = new IntSet();
        IntSet bottomSet2 = new IntSet();
        float[] gridTranslations = new float[taskCount];
        int snappedPage = getNextPage();
        TaskView snappedTaskView = getTaskViewAt(snappedPage);
        TaskView homeTaskView = getHomeTaskView();
        TaskView nextFocusedTaskView = null;
        if (isTaskDismissal) {
            bottomRowWidth = 0;
        } else {
            bottomRowWidth = 0;
            this.mTopRowIdSet.clear();
        }
        int snappedTaskRowWidth = 0;
        int snappedTaskRowWidth2 = 0;
        float bottomAccumulatedTranslationX2 = 0.0f;
        int bottomRowWidth4 = bottomRowWidth;
        int focusedTaskWidthAndSpacing5 = 0;
        int focusedTaskIndex = Integer.MAX_VALUE;
        float topAccumulatedTranslationX = 0.0f;
        int i3 = 0;
        int topRowWidth = 0;
        int snappedPage2 = Integer.MAX_VALUE;
        while (true) {
            bottomAccumulatedTranslationX = bottomAccumulatedTranslationX2;
            if (i3 >= taskCount) {
                break;
            }
            TaskView taskView = requireTaskViewAt(i3);
            int taskCount2 = taskCount;
            IntSet bottomSet3 = bottomSet2;
            int taskWidthAndSpacing2 = taskView.getLayoutParams().width + this.mPageSpacing;
            if (taskView.isFocusedTask()) {
                topRowWidth += taskWidthAndSpacing2;
                bottomRowWidth4 += taskWidthAndSpacing2;
                int focusedTaskIndex2 = i3;
                focusedTaskWidthAndSpacing3 = taskWidthAndSpacing2;
                gridTranslations[i3] = gridTranslations[i3] + snappedTaskRowWidth2;
                gridTranslations[i3] = gridTranslations[i3] + (this.mIsRtl ? taskWidthAndSpacing2 : -taskWidthAndSpacing2);
                taskView.setGridTranslationY(((this.mLastComputedTaskSize.height() + taskTopMargin2) - taskView.getLayoutParams().height) / 2.0f);
                if (taskView != snappedTaskView) {
                    taskTopMargin = taskTopMargin2;
                    focusedTaskIndex = focusedTaskIndex2;
                    bottomAccumulatedTranslationX2 = bottomAccumulatedTranslationX;
                    bottomSet = bottomSet3;
                } else {
                    snappedTaskRowWidth = taskWidthAndSpacing2;
                    taskTopMargin = taskTopMargin2;
                    focusedTaskIndex = focusedTaskIndex2;
                    bottomAccumulatedTranslationX2 = bottomAccumulatedTranslationX;
                    bottomSet = bottomSet3;
                }
            } else if (taskView.isDesktopTask()) {
                int desktopTaskIndex = i3;
                int desktopTaskIndex2 = taskView.getVisibility();
                if (desktopTaskIndex2 != 8) {
                    if (!FeatureFlags.ENABLE_GRID_ONLY_OVERVIEW.get()) {
                        gridTranslations[i3] = gridTranslations[i3] + (this.mIsRtl ? taskWidthAndSpacing2 : -taskWidthAndSpacing2);
                    }
                    taskView.setGridTranslationY(((this.mLastComputedDesktopTaskSize.height() + taskTopMargin2) - taskView.getLayoutParams().height) / 2.0f);
                }
                taskTopMargin = taskTopMargin2;
                bottomAccumulatedTranslationX2 = bottomAccumulatedTranslationX;
                snappedPage2 = desktopTaskIndex;
                focusedTaskWidthAndSpacing3 = focusedTaskWidthAndSpacing5;
                bottomSet = bottomSet3;
            } else {
                if (i3 > focusedTaskIndex) {
                    taskTopMargin = taskTopMargin2;
                    gridTranslations[i3] = gridTranslations[i3] + (this.mIsRtl ? focusedTaskWidthAndSpacing5 : -focusedTaskWidthAndSpacing5);
                } else {
                    taskTopMargin = taskTopMargin2;
                    snappedTaskRowWidth2 += this.mIsRtl ? taskWidthAndSpacing2 : -taskWidthAndSpacing2;
                }
                int taskViewId = taskView.getTaskViewId();
                if (isTaskDismissal) {
                    if (i3 > startRebalanceAfter) {
                        this.mTopRowIdSet.remove(taskViewId);
                        isTopRow = topRowWidth <= bottomRowWidth4;
                    } else {
                        isTopRow = this.mTopRowIdSet.contains(taskViewId);
                    }
                } else {
                    isTopRow = topRowWidth <= bottomRowWidth4;
                }
                if (isTopRow) {
                    if (homeTaskView != null && nextFocusedTaskView == null) {
                        nextFocusedTaskView = taskView;
                    } else {
                        topRowWidth += taskWidthAndSpacing2;
                    }
                    topSet.add(i3);
                    int topRowWidth2 = topRowWidth;
                    this.mTopRowIdSet.add(taskViewId);
                    taskView.setGridTranslationY(this.mTaskGridVerticalDiff);
                    float widthOffset = 0.0f;
                    int taskViewId2 = i3 - 1;
                    while (!topSet.contains(taskViewId2) && taskViewId2 >= 0) {
                        if (taskViewId2 == focusedTaskIndex) {
                            focusedTaskWidthAndSpacing4 = focusedTaskWidthAndSpacing5;
                            focusedTaskShift2 = snappedTaskRowWidth2;
                        } else if (taskViewId2 == snappedPage2) {
                            focusedTaskWidthAndSpacing4 = focusedTaskWidthAndSpacing5;
                            focusedTaskShift2 = snappedTaskRowWidth2;
                        } else {
                            focusedTaskShift2 = snappedTaskRowWidth2;
                            int i4 = requireTaskViewAt(taskViewId2).getLayoutParams().width;
                            focusedTaskWidthAndSpacing4 = focusedTaskWidthAndSpacing5;
                            int focusedTaskWidthAndSpacing6 = this.mPageSpacing;
                            widthOffset += i4 + focusedTaskWidthAndSpacing6;
                        }
                        taskViewId2--;
                        focusedTaskWidthAndSpacing5 = focusedTaskWidthAndSpacing4;
                        snappedTaskRowWidth2 = focusedTaskShift2;
                    }
                    focusedTaskWidthAndSpacing2 = focusedTaskWidthAndSpacing5;
                    focusedTaskShift = snappedTaskRowWidth2;
                    float currentTaskTranslationX = this.mIsRtl ? widthOffset : -widthOffset;
                    gridTranslations[i3] = gridTranslations[i3] + topAccumulatedTranslationX + currentTaskTranslationX;
                    topAccumulatedTranslationX += currentTaskTranslationX;
                    topRowWidth = topRowWidth2;
                    bottomSet = bottomSet3;
                } else {
                    focusedTaskWidthAndSpacing2 = focusedTaskWidthAndSpacing5;
                    focusedTaskShift = snappedTaskRowWidth2;
                    int bottomRowWidth5 = bottomRowWidth4 + taskWidthAndSpacing2;
                    bottomSet = bottomSet3;
                    bottomSet.add(i3);
                    taskView.setGridTranslationY(this.mTopBottomRowHeightDiff + this.mTaskGridVerticalDiff);
                    float widthOffset2 = 0.0f;
                    int j = i3 - 1;
                    while (!bottomSet.contains(j) && j >= 0) {
                        if (j == focusedTaskIndex) {
                            taskWidthAndSpacing = taskWidthAndSpacing2;
                            bottomRowWidth3 = bottomRowWidth5;
                        } else if (j == snappedPage2) {
                            taskWidthAndSpacing = taskWidthAndSpacing2;
                            bottomRowWidth3 = bottomRowWidth5;
                        } else {
                            taskWidthAndSpacing = taskWidthAndSpacing2;
                            int i5 = requireTaskViewAt(j).getLayoutParams().width;
                            bottomRowWidth3 = bottomRowWidth5;
                            int bottomRowWidth6 = this.mPageSpacing;
                            widthOffset2 += i5 + bottomRowWidth6;
                        }
                        j--;
                        bottomRowWidth5 = bottomRowWidth3;
                        taskWidthAndSpacing2 = taskWidthAndSpacing;
                    }
                    int bottomRowWidth7 = bottomRowWidth5;
                    float currentTaskTranslationX2 = this.mIsRtl ? widthOffset2 : -widthOffset2;
                    gridTranslations[i3] = gridTranslations[i3] + bottomAccumulatedTranslationX + currentTaskTranslationX2;
                    bottomAccumulatedTranslationX += currentTaskTranslationX2;
                    bottomRowWidth4 = bottomRowWidth7;
                }
                if (taskView != snappedTaskView) {
                    bottomAccumulatedTranslationX2 = bottomAccumulatedTranslationX;
                    focusedTaskWidthAndSpacing3 = focusedTaskWidthAndSpacing2;
                    snappedTaskRowWidth2 = focusedTaskShift;
                } else {
                    snappedTaskRowWidth = isTopRow ? topRowWidth : bottomRowWidth4;
                    bottomAccumulatedTranslationX2 = bottomAccumulatedTranslationX;
                    focusedTaskWidthAndSpacing3 = focusedTaskWidthAndSpacing2;
                    snappedTaskRowWidth2 = focusedTaskShift;
                }
            }
            i3++;
            bottomSet2 = bottomSet;
            focusedTaskWidthAndSpacing5 = focusedTaskWidthAndSpacing3;
            taskCount = taskCount2;
            taskTopMargin2 = taskTopMargin;
        }
        int taskCount3 = taskCount;
        int focusedTaskWidthAndSpacing7 = focusedTaskWidthAndSpacing5;
        IntSet bottomSet4 = bottomSet2;
        float snappedTaskNonGridScrollAdjustment = 0.0f;
        float snappedTaskGridTranslationX = 0.0f;
        if (snappedTaskView != null) {
            snappedTaskNonGridScrollAdjustment = snappedTaskView.getScrollAdjustment(false);
            snappedTaskGridTranslationX = gridTranslations[snappedPage];
        }
        float clearAllAccumulatedTranslation = topSet.contains(taskCount3 + (-1)) ? topAccumulatedTranslationX : bottomAccumulatedTranslationX;
        float shorterRowCompensation2 = 0.0f;
        if (topRowWidth <= bottomRowWidth4) {
            if (topSet.contains(taskCount3 - 1)) {
                shorterRowCompensation2 = bottomRowWidth4 - topRowWidth;
            }
        } else if (bottomSet4.contains(taskCount3 - 1)) {
            shorterRowCompensation2 = topRowWidth - bottomRowWidth4;
        }
        float clearAllShorterRowCompensation = this.mIsRtl ? -shorterRowCompensation2 : shorterRowCompensation2;
        float clearAllShortTotalWidthTranslation = 0.0f;
        int longRowWidth = Math.max(topRowWidth, bottomRowWidth4);
        if (longRowWidth >= this.mLastComputedGridSize.width()) {
            deviceProfile = deviceProfile2;
            this.mClearAllShortTotalWidthTranslation = 0;
        } else {
            if (this.mIsRtl) {
                bottomRowWidth2 = this.mLastComputedTaskSize.right;
                deviceProfile = deviceProfile2;
            } else {
                deviceProfile = deviceProfile2;
                int bottomRowWidth8 = deviceProfile.widthPx;
                bottomRowWidth2 = bottomRowWidth8 - this.mLastComputedTaskSize.left;
            }
            this.mClearAllShortTotalWidthTranslation = (bottomRowWidth2 - longRowWidth) - deviceProfile.overviewGridSideMargin;
            if (!this.mIsRtl) {
                i2 = this.mClearAllShortTotalWidthTranslation;
            } else {
                i2 = -this.mClearAllShortTotalWidthTranslation;
            }
            clearAllShortTotalWidthTranslation = i2;
            int snappedTaskRowWidth3 = snappedTaskRowWidth;
            if (snappedTaskRowWidth3 != longRowWidth) {
                snappedTaskRowWidth = snappedTaskRowWidth3;
            } else {
                snappedTaskRowWidth = snappedTaskRowWidth3 + this.mClearAllShortTotalWidthTranslation;
            }
            int snappedTaskRowWidth4 = this.mClearAllShortTotalWidthTranslation;
            longRowWidth += snappedTaskRowWidth4;
        }
        float clearAllTotalTranslationX = clearAllAccumulatedTranslation + clearAllShorterRowCompensation + clearAllShortTotalWidthTranslation + snappedTaskNonGridScrollAdjustment;
        if (focusedTaskIndex < taskCount3) {
            if (this.mIsRtl) {
                focusedTaskWidthAndSpacing = focusedTaskWidthAndSpacing7;
                shorterRowCompensation = focusedTaskWidthAndSpacing;
            } else {
                focusedTaskWidthAndSpacing = focusedTaskWidthAndSpacing7;
                shorterRowCompensation = -focusedTaskWidthAndSpacing;
            }
            clearAllTotalTranslationX += shorterRowCompensation;
        } else {
            focusedTaskWidthAndSpacing = focusedTaskWidthAndSpacing7;
        }
        if (snappedTaskView != null) {
            int distanceFromClearAll = longRowWidth - snappedTaskRowWidth;
            if (!this.mIsRtl) {
                i = deviceProfile.widthPx - this.mLastComputedTaskSize.right;
            } else {
                i = this.mLastComputedTaskSize.left;
            }
            int minimumDistance = (((i - deviceProfile.overviewGridSideMargin) - this.mPageSpacing) + (this.mTaskWidth - snappedTaskView.getLayoutParams().width)) - this.mClearAllShortTotalWidthTranslation;
            if (distanceFromClearAll < minimumDistance) {
                int distanceDifference = minimumDistance - distanceFromClearAll;
                snappedTaskGridTranslationX += this.mIsRtl ? distanceDifference : -distanceDifference;
            }
        }
        for (int i6 = 0; i6 < taskCount3; i6++) {
            requireTaskViewAt(i6).setGridTranslationX((gridTranslations[i6] - snappedTaskGridTranslationX) + snappedTaskNonGridScrollAdjustment);
        }
        this.mClearAllButton.setGridTranslationPrimary(clearAllTotalTranslationX - snappedTaskGridTranslationX);
        this.mClearAllButton.setGridScrollOffset(this.mIsRtl ? this.mLastComputedTaskSize.left - this.mLastComputedGridSize.left : this.mLastComputedTaskSize.right - this.mLastComputedGridSize.right);
        setGridProgress(this.mGridProgress);
    }

    public float gridProgress = 0;
    private void setGridProgress(float mGridProgress) {
        this.mClearAllButton.setGridProgress(gridProgress);
    }
    public TaskView requireTaskViewAt(int i3) {
        return null;
    }


    public TaskView getHomeTaskView() {
        return null;
    }

    public TaskView getTaskViewAt(int snappedPage) {
        return null;
    }

    private java.util.List<android.graphics.PointF> bezierPoints = null;

    private void updatePageOffsetsForFlyme() {
        final float density = getContext().getResources().getDisplayMetrics().density;
        final float currentStackXOffsetPx = 20f * density;
        final float currentStackYOffsetPx = -15f * density;
        final float currentStackScaleDecrement = 0.05f;
        final float currentStackMinScale = 0.85f;
        final float currentStackAlphaDecrement = 0.15f; // 用于透明度
        final float currentStackMinAlpha = 0.7f;
        final float currentStackZOffsetPx = -2f * density;
        final float currentStackBaseTranslationZPx = 10f * density;
        final int currentStackMaxVisibleBehind = 2;

        int count = getChildCount();
        if (count == 0) {
            return;
        }

        int focusedTaskIndex = getNextPage();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (!(child instanceof TaskView)) {
                continue;
            }

            TaskView taskView = (TaskView) child;
            int relativeIndex = i - focusedTaskIndex;

            float targetTranslationX, targetTranslationY, targetScale, targetAlpha, targetTranslationZ;
            boolean isVisible = true;

            if (relativeIndex == 0) {
                targetTranslationX = 0f;
                targetTranslationY = 0f;
                targetScale = 1.0f;
                targetAlpha = 1.0f;
                targetTranslationZ = currentStackBaseTranslationZPx;
            } else if (relativeIndex > 0) {
                if (relativeIndex <= currentStackMaxVisibleBehind) {
                    targetTranslationX = relativeIndex * currentStackXOffsetPx;
                    targetTranslationY = relativeIndex * currentStackYOffsetPx;
                    targetScale = Math.max(currentStackMinScale, 1.0f - (relativeIndex * currentStackScaleDecrement));
                    targetAlpha = Math.max(currentStackMinAlpha, 1.0f - (relativeIndex * currentStackAlphaDecrement));
                    targetTranslationZ = currentStackBaseTranslationZPx + (relativeIndex * currentStackZOffsetPx);
                } else {
                    isVisible = false;
                    targetAlpha = 0f;
                    targetTranslationX = (currentStackMaxVisibleBehind + 1) * currentStackXOffsetPx;
                    targetTranslationY = (currentStackMaxVisibleBehind + 1) * currentStackYOffsetPx;
                    targetScale = currentStackMinScale - 0.1f;
                    targetTranslationZ = currentStackBaseTranslationZPx + ((currentStackMaxVisibleBehind + 1) * currentStackZOffsetPx);
                }
            } else {
                targetTranslationX = relativeIndex * taskView.getWidth() * 0.9f;
                targetTranslationY = 0f;
                targetScale = 1.0f;
                targetAlpha = 1.0f;
                targetTranslationZ = currentStackBaseTranslationZPx + (relativeIndex * currentStackZOffsetPx);

                if (targetTranslationX + taskView.getWidth() < 0 && !isPageInTransition()) {
                    targetAlpha = 0.2f;
                }
            }

            taskView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);

            taskView.setTranslationX(targetTranslationX);
            taskView.setTranslationY(targetTranslationY);
            taskView.setScaleX(targetScale);
            taskView.setScaleY(targetScale);
            taskView.setAlpha(targetAlpha);
            taskView.setTranslationZ(targetTranslationZ);
        }
    }
    protected float mTaskModalness;
    private TaskView mSelectedTask;
    protected boolean mEnableDrawingLiveTile;
    private boolean mOverviewStateEnabled;
    private boolean mPendingLayoutRequested;
    private boolean mShowAsGridLastOnLayout;
    protected final Rect mTempRect;
    protected Float mLastComputedTaskStartPushOutDistance;
    protected Float mLastComputedTaskEndPushOutDistance;


    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!this.mOverviewStateEnabled && !this.mFirstLayout && !this.mPendingLayoutRequested) {
            FixFor1214345.tagRecentsViewSkipOnLayoutWhenPsiTrue(pageScrollsInitialized());
            return;
        }
        FixFor1214345.tagRecentsViewOnLayoutCalled();
        this.mShowAsGridLastOnLayout = showAsGrid();
        this.mPendingLayoutRequested = false;
        super.onLayout(changed, left, top, right, bottom);
        updateEmptyStateUi(changed);
        getTaskSize(this.mTempRect);
        updatePivots();
        setTaskModalness(this.mTaskModalness);
        this.mLastComputedTaskStartPushOutDistance = null;
        this.mLastComputedTaskEndPushOutDistance = null;
        updatePageOffsets();
        setImportantForAccessibility(isModal() ? 2 : 0);
    }

    private boolean isModal() {
        return false;
    }

    private void updatePageOffsets() {
        if (shouldApplyStackingEffectRuyue()) {
            return; // 堆叠逻辑接管
        }
        updatePageOffsetsForFlyme();
    }

    private void setTaskModalness(float mTaskModalness) {
    }

    private void updatePivots() {
    }

    public void getTaskSize(Rect outRect) {
    }


    private void updateEmptyStateUi(boolean changed) {
    }


    private void updatePageOffsetsBsp() {
        if (shouldApplyStackingEffectRuyue()) {
            return; // 堆叠逻辑接管
        }
        float leftOffsetSize;
        float rightOffsetSize;
        float f;
        float translation;
        float modalTranslation;
        FloatProperty translationPropertyX;
        float modalOffset;
        RecentsView<ACTIVITY_TYPE, STATE_TYPE> recentsView = this;
        float offset = recentsView.mAdjacentPageHorizontalOffset;
        float modalOffset2 = Interpolators.ACCEL_0_75.getInterpolation(recentsView.mTaskModalness);
        int count = getChildCount();
        boolean showAsGrid = showAsGrid();
        int i = -1;
        TaskView runningTask = (recentsView.mRunningTaskViewId == -1 || !recentsView.mRunningTaskTileHidden) ? null : getRunningTaskView();
        int midpoint = runningTask == null ? -1 : recentsView.indexOfChild(runningTask);
        int modalMidpoint = getCurrentPage();
        boolean isModalGridWithoutFocusedTask = showAsGrid && FeatureFlags.ENABLE_GRID_ONLY_OVERVIEW.get() && recentsView.mTaskModalness > 0.0f;
        if (isModalGridWithoutFocusedTask) {
            modalMidpoint = recentsView.indexOfChild(recentsView.mSelectedTask);
        }
        if (midpoint - 1 >= 0) {
            leftOffsetSize = recentsView.getHorizontalOffsetSize(midpoint - 1, midpoint, offset);
        } else {
            leftOffsetSize = 0.0f;
        }
        if (midpoint + 1 < count) {
            rightOffsetSize = recentsView.getHorizontalOffsetSize(midpoint + 1, midpoint, offset);
        } else {
            rightOffsetSize = 0.0f;
        }
        float modalLeftOffsetSize = 0.0f;
        float modalRightOffsetSize = 0.0f;
        float gridOffsetSize = 0.0f;
        if (showAsGrid) {
            int referenceIndex = modalMidpoint == 0 ? 1 : 0;
            gridOffsetSize = referenceIndex < count ? recentsView.getHorizontalOffsetSize(referenceIndex, modalMidpoint, modalOffset2) : 0.0f;
        } else {
            if (modalMidpoint - 1 >= 0) {
                f = recentsView.getHorizontalOffsetSize(modalMidpoint - 1, modalMidpoint, modalOffset2);
            } else {
                f = 0.0f;
            }
            modalLeftOffsetSize = f;
            modalRightOffsetSize = modalMidpoint + 1 < count ? recentsView.getHorizontalOffsetSize(modalMidpoint + 1, modalMidpoint, modalOffset2) : 0.0f;
        }
        int i2 = 0;
        while (i2 < count) {
            if (i2 == midpoint) {
                translation = 0.0f;
            } else if (i2 < midpoint) {
                translation = leftOffsetSize;
            } else {
                translation = rightOffsetSize;
            }
            if (isModalGridWithoutFocusedTask) {
                float gridOffsetSize2 = recentsView.getHorizontalOffsetSize(i2, modalMidpoint, modalOffset2);
                float abs = Math.abs(gridOffsetSize2);
                if (i2 <= modalMidpoint) {
                    i = 1;
                }
                gridOffsetSize = abs * i;
            }
            if (i2 == modalMidpoint) {
                modalTranslation = 0.0f;
            } else if (showAsGrid) {
                modalTranslation = gridOffsetSize;
            } else {
                modalTranslation = i2 < modalMidpoint ? modalLeftOffsetSize : modalRightOffsetSize;
            }
            final float totalTranslationX = translation + modalTranslation;
            float offset2 = offset;
            View child = recentsView.getChildAt(i2);
            int count2 = count;
            if (child instanceof TaskView) {
                translationPropertyX = ((TaskView) child).getPrimaryTaskOffsetTranslationProperty();
            } else {
                translationPropertyX = recentsView.mOrientationHandler.getPrimaryViewTranslate();
            }
            TaskView runningTask2 = runningTask;
            translationPropertyX.set(child, Float.valueOf(totalTranslationX));
            if (recentsView.mEnableDrawingLiveTile && i2 == getRunningTaskIndex()) {
                recentsView.runActionOnRemoteHandles(new Consumer() { // from class: com.android.quickstep.views.RecentsView$$ExternalSyntheticLambda53
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
//                        ((RemoteTargetGluer.RemoteTargetHandle) obj).getTaskViewSimulator().taskPrimaryTranslation.value = totalTranslationX;
                    }
                });
                redrawLiveTile();
            }
            if (!showAsGrid || !FeatureFlags.ENABLE_GRID_ONLY_OVERVIEW.get() || !(child instanceof TaskView)) {
                modalOffset = modalOffset2;
            } else {
                float totalTranslationY = recentsView.getVerticalOffsetSize(i2, modalOffset2);
                FloatProperty translationPropertyY = ((TaskView) child).getSecondaryTaskOffsetTranslationProperty();
                modalOffset = modalOffset2;
                translationPropertyY.set( child, Float.valueOf(totalTranslationY));
            }
            i2++;
            recentsView = this;
            offset = offset2;
            count = count2;
            runningTask = runningTask2;
            modalOffset2 = modalOffset;
            i = -1;
        }
        updateCurveProperties();
    }

    private float getVerticalOffsetSize(int i2, float modalOffset2) {
        return 0;
    }

    public void redrawLiveTile() {
    }

    public void updateCurveProperties() {
    }

    public void runActionOnRemoteHandles(Consumer consumer) {
    }

    public int getRunningTaskIndex() {
        return 1;
    }

    public boolean showAsGrid() {
        return true;
    }


    private float getHorizontalOffsetSize(int i, int midpoint, float offset) {
        return 0.1f;
    }

    public TaskView getRunningTaskView() {
        return null;
    }

}
