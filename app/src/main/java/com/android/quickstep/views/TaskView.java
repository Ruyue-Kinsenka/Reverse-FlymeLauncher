package com.android.quickstep.views;

import android.content.Context;
import android.util.FloatProperty;

public class TaskView extends android.view.View {
    public TaskView(Context context) {
        super(context);
    }

    public void setVisibility(int gone) {
    }

    public void setAlpha(float v) {
    }


    public float getTranslationX() {
        return 0.1f;
    }

    public void setTranslationX(float translationX) {
    }

    public void setTranslationY(float translationY) {
    }

    public void setScaleX(float scale) {
    }

    public void setScaleY(float scale) {
    }

    public void setTranslationZ(int i) {
    }

    public FloatProperty<TaskView> getPrimaryTaskOffsetTranslationProperty() {
        return null;
    }

    public boolean isFocusedTask() {
        return false;
    }

    public void setGridTranslationY(float gridTranslationY) {
    }


    public boolean isDesktopTask() {
        return true;
    }

    public int getTaskViewId() {
        return 0;
    }

    public float getScrollAdjustment(boolean b) {
        return 0.1f;
    }

    public void setGridTranslationX(float v) {
    }

    public FloatProperty getSecondaryTaskOffsetTranslationProperty() {
        return null;
    }
}
