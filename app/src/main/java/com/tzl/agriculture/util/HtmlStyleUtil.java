package com.tzl.agriculture.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;

public class HtmlStyleUtil {

    public static WebSettings.ZoomDensity getZoomDensity(Context context) {
        int screenDensity = context.getResources().getDisplayMetrics().densityDpi;
        WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
        switch (screenDensity) {
            case DisplayMetrics.DENSITY_LOW:
                zoomDensity = WebSettings.ZoomDensity.CLOSE;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                zoomDensity = WebSettings.ZoomDensity.MEDIUM;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                zoomDensity = WebSettings.ZoomDensity.FAR;
                break;
        }
        return zoomDensity;
    }

    public static String pingHtml(String html) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1,user-scalable=no\" />\n" +
                "    <meta content=\"yes\" name=\"apple-mobile-web-app-capable\" />\n" +
                "    <meta content=\"black\" name=\"apple-mobile-web-app-status-bar-style\" />\n" +
                "    <meta content=\"telephone=no\" name=\"format-detection\" />\n" +
                "</head><style> img{ max-width:100%; height:auto}</style><body>" + html + "</body></html>";
    }

}
