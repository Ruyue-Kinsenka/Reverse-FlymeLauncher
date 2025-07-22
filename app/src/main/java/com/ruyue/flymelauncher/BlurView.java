package com.ruyue.flymelauncher;

import android.content.Context;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class BlurView extends View {
    public BlurView(Context context) {
        super(context);
//        init();
    }

    public BlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init();
    }

//    private void init() {
//        setWillNotDraw(false);
//        RenderEffect blurEffect = RenderEffect.createBlurEffect(50f, 50f, Shader.TileMode.CLAMP);
//        setRenderEffect(blurEffect);
//    }
}
