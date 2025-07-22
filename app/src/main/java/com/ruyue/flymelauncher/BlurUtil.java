package com.ruyue.flymelauncher;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurUtil {
//    public static Bitmap blur(Context context, Bitmap bitmap, float radius) {
//        if (radius <= 0f) return bitmap;
//        float r = Math.min(radius, 25f);
//
//        RenderScript rs = RenderScript.create(context);
//        Allocation input = Allocation.createFromBitmap(rs, bitmap);
//        Allocation output = Allocation.createTyped(rs, input.getType());
//        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//        script.setRadius(r);
//        script.setInput(input);
//        script.forEach(output);
//        output.copyTo(bitmap);
//        script.destroy();
//        input.destroy();
//        output.destroy();
//        rs.destroy();
//        return bitmap;
//    }
}