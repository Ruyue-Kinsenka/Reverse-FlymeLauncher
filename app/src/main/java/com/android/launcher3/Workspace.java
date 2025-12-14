package com.android.launcher3;

import android.view.View;
import com.android.launcher3.pageindicators.PageIndicator;

public class Workspace {
    private PageIndicatorImpl mPageIndicator;

    public Workspace() {
        mPageIndicator = new PageIndicatorImpl();
    }

    public void showPageIndicatorAtCurrentScroll() {
    }

    public void setClipChildren(boolean clipChildren) {
    }

    public View getPageIndicator() {
        return mPageIndicator;
    }

    public int getCurrentPage() {
        return 0;
    }

    private static class PageIndicatorImpl extends View implements PageIndicator {
        public PageIndicatorImpl() {
            super(null);
        }

        @Override
        public void setShouldAutoHide(boolean shouldAutoHide) {
        }
    }
}