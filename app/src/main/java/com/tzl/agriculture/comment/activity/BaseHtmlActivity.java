package com.tzl.agriculture.comment.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.MyWebViewClient;
import com.tzl.agriculture.util.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.ButterKnife;
import config.Article;
import okhttp3.Call;

public abstract class BaseHtmlActivity extends AppCompatActivity {

    private Context mContext;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();
    }


    public abstract int setLayout();

    public abstract void initView();

    public abstract void initData();

    public Context getContext() {
        return this.mContext;
    }

    public void setWebView(WebView webViewx, SpinKitView spinKitView, String html) {
        this.webView = webViewx;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 有网络时采用缓存
        webView.getSettings().setUserAgentString(System.getProperty("http.agent"));
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new MyWebViewClient(webView));
        webView.loadDataWithBaseURL(null, getStyle()+html, "text/html","utf-8", null);

        //webView.loadData(html, "text/html", "UTF-8");
        //webView.getSettings().setTextZoom(300);


        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    //加载完成
                    spinKitView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void addBrow(String articleId) {
        Map<String, String> map = new HashMap<>();
        map.put("articleId", articleId);
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(getContext(), 1).getkey("token", "");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.addBrowseNum, str, token,
                new GsonObjectCallback<String>(Article.BASE) {
                    @Override
                    public void onUi(String result) {
//                        try {
//                            JSONObject object = new JSONObject(result);
//                            ToastUtil.showShort(getContext(), "添加浏览量：" + TextUtil.checkStr2Str(object.optString("msg")));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                    }
                });
    }

    private String getStyle(){
        return "<style>* {font-size:50px;line-height:80px;} p {color:#3B3B3B;} img {width:100%;height:100%}pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;}</style>";
    }

    /**
     * 修改状态栏颜色
     * @param color
     */
    public void setStatusColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    /**
     *  设置状态栏字体颜色
     *  谷歌特定修改
     *  需要在跟布局加上 android:fitsSystemWindows="true" 这句话
     * @param dark   ture 黑色，false 白色，或者 根据flag去切换状态的颜色，具体的规则还不知道
     */
    public void setAndroidNativeLightStatusBar(boolean dark) {
        View decor = getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public void startSendServer(String articleId,String category){
        Map<String,String>map = new HashMap<>();
        map.put("articleId",articleId);
        map.put("category",category);
        map.put("optionType","2");
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this,1).getkey("token","");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.toOperatArticle, str, token, new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != webView) {
            webView.destroy();
        }
        if (mContext != null) {
            mContext = null;
        }
    }
}
