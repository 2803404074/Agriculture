package com.tzl.agriculture.util;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

public class TextUtil {

    public static void setTextViewStyles(TextView text) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, text.getPaint().getTextSize()+100, 0, Color.parseColor("#668B8B"),
                Color.parseColor("#225953"), Shader.TileMode.CLAMP);
        text.getPaint().setShader(mLinearGradient);
        text.invalidate();
    }


    /**
     * @param str
     * @return
     */
    public static String checkStr2Num(String str) {
        if (StringUtils.isEmpty(str)) {
            return "0";
        } else {
            return str;
        }
    }


    public static String checkStr2Str(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        } else {
            return str;
        }
    }


    public static boolean checkStrNull(String str) {
        if (StringUtils.isEmpty(str) || str.equals("null")) {
            return true;
        } else {
            return false;
        }
    }
}
