package com.tzl.agriculture.comment.activity;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.tzl.agriculture.R;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.MyWebViewClient;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ShareUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import config.Article;
import okhttp3.Call;

/**
 * 文章的详情
 */
public class OrderHtmlActivity extends BaseHtmlActivity {

    @BindView(R.id.web_view)
    WebView webView;

    @BindView(R.id.back)
    ImageView ivBack;

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    @Override
    public int setLayout() {
        return R.layout.activity_html_orther;
    }

    /**
     * 获取文章信息，绑定数据
     */
    @Override
    public void initView() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 有网络时采用缓存
        webView.getSettings().setUserAgentString(System.getProperty("http.agent"));
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new MyWebViewClient(webView));
        webView.loadUrl(getIntent().getStringExtra("html"));

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    //加载完成
                    spinKitView.setVisibility(View.GONE);
                }
            }
        });
    }
}
