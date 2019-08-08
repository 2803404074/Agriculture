package com.tzl.agriculture.comment.activity;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.tzl.agriculture.R;
import com.tzl.agriculture.model.XiangcMo;
import com.tzl.agriculture.util.JsonUtil;
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

        String str = getIntent().getStringExtra("html");

        setWebView(webView,spinKitView,str);
    }

    @Override
    public void initData() {

    }
}
