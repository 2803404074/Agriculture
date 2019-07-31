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
import com.tzl.agriculture.util.TextUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import cn.sharesdk.onekeyshare.OnekeyShare;
import config.Article;
import okhttp3.Call;

/**
 * 乡愁文章的详情
 */
public class HtmlForXcActivity extends BaseHtmlActivity {

    @BindView(R.id.web_view)
    WebView webView;

    @BindView(R.id.tv_title)
    TextView textView;

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    @BindView(R.id.back)
    ImageView ivBack;

    @BindView(R.id.iv_share)
    ImageView ivShare;

    @BindView(R.id.tv_collection_num)
    TextView tvCollectionNum;

    @BindView(R.id.tv_dz_num)
    TextView tvDzNum;


    @BindView(R.id.bt_dz)
    ShineButton btDz;

    @BindView(R.id.bt_collect)
    ShineButton btCollect;

    @BindView(R.id.ll_collection)
    LinearLayout llCollection;

    @BindView(R.id.ll_dz)
    LinearLayout llDz;

    @Override
    public int setLayout() {
        return R.layout.activity_html_xc;
    }

    /**
     * 获取文章信息，绑定数据
     */
    @Override
    public void initView() {
        setAndroidNativeLightStatusBar(true);
        setStatusColor(R.color.colorW);

        Map<String, String> map = new HashMap<>();
        map.put("articleId", getIntent().getStringExtra("articleId"));
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(getContext(), 1).getkey("token", "");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.getArticle, str, token,
                new GsonObjectCallback<String>(Article.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {
                                JSONObject dataObj = object.optJSONObject("data");
                                String str = dataObj.optString("articleInfo");
                                XiangcMo.Article article = JsonUtil.string2Obj(str, XiangcMo.Article.class);
                                setViewData(article);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                    }
                });


        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShare();
            }
        });

    }

    private void setViewData(XiangcMo.Article article) {
        //标题
        textView.setText(article.getTitle());

        //点赞数量
        tvDzNum.setText(TextUtil.checkStr2Num(article.getGoodNum()));

        //收藏数量
        tvCollectionNum.setText(TextUtil.checkStr2Num(article.getCollectNum()));

        //如果点赞字段值不为null ，则已点赞该文章
        if (!StringUtils.isEmpty(article.getAlreadyGood())){
            tvDzNum.setTextColor(getResources().getColor(R.color.colorOrange));
            llDz.setBackgroundResource(R.drawable.shape_login_orange_no);
            btDz.setChecked(true);
        }

        //如果收藏字段值不为null ，则已收藏该文章
        if (!StringUtils.isEmpty(article.getAlreadyCollect())){
            tvCollectionNum.setTextColor(getResources().getColor(R.color.colorOrange));
            llCollection.setBackgroundResource(R.drawable.shape_login_orange_no);
            btCollect.setChecked(true);
        }

        setWebView(webView,spinKitView,article.getContent());

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //动画
        btDz.init(this); //点赞
        btCollect.init(this); //收藏

        btDz.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                int dzNum = Integer.parseInt(tvDzNum.getText().toString());
                if (checked) {
                    dzNum++;
                    tvDzNum.setTextColor(getResources().getColor(R.color.colorOrange));
                    llDz.setBackgroundResource(R.drawable.shape_login_orange_no);
                } else {
                    dzNum--;
                    tvDzNum.setTextColor(getResources().getColor(R.color.colorGri2));
                    llDz.setBackgroundResource(R.drawable.shape_login_blue_no);
                }
                tvDzNum.setText(String.valueOf(dzNum));
                updateData("0", "点赞", checked);
            }
        });

        btCollect.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                int dzNum = Integer.parseInt(tvCollectionNum.getText().toString());
                if (checked) {
                    dzNum++;
                    tvCollectionNum.setTextColor(getResources().getColor(R.color.colorOrange));
                    llCollection.setBackgroundResource(R.drawable.shape_login_orange_no);

                } else {
                    dzNum--;
                    tvCollectionNum.setTextColor(getResources().getColor(R.color.colorGri2));
                    llCollection.setBackgroundResource(R.drawable.shape_login_blue_no);
                }
                tvCollectionNum.setText(String.valueOf(dzNum));
                updateData("1", "收藏", checked);
            }
        });
    }

    //添加浏览量
    @Override
    public void initData() {
        addBrow(getIntent().getStringExtra("articleId"));
    }
    /**
     * 请求后端
     *
     * @param s 0=点赞，1=收藏，2=转发
     */
    private void updateData(String s, String tips, boolean isClick) {
        Map<String, String> map = new HashMap<>();
        map.put("optionType", s);
        map.put("articleId", getIntent().getStringExtra("articleId"));
        map.put("category", "1");
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.toOperatArticle, str, token,
                new GsonObjectCallback<String>(Article.BASE) {
                    @Override
                    public void onUi(String result) {
                    }
                    @Override
                    public void onFailed(Call call, IOException e) {

                    }
                });
    }


    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("趣乡村");
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("www.xcxky.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("第一次分享");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl("http://pic24.nipic.com/20120922/10898738_143746326185_2.jpg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("www.xcxky.cn");
        // comment是我对这条分享的评论，仅在人人网使用
        oks.setComment("第一次分享文本");
        // 启动分享GUI
        oks.show(this);
    }
}
