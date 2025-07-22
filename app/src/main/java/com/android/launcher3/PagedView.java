package com.android.launcher3;

import android.content.Context;
import android.util.FloatProperty;
import android.view.View;

public class PagedView extends android.view.View{
    protected boolean mIsRtl;
    protected int mPageSpacing;
    protected boolean mFirstLayout;

    public boolean pageScrollsInitialized() {
        return true;
    }

    public PagedView(Context context) {
        super(context);
    }

    public int getNextPage (){
        return 0;
    }


    public int getCurrentPage() {
        return 1;
    }

    public boolean isPageInTransition() {
        return true;
    }

    protected void onPageEndTransition() {
    }
}
