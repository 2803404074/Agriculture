package com.tzl.agriculture.comment.activity;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
 * 品牌故事软文
 */
public class HtmlForStoryActivity extends BaseHtmlActivity implements View.OnClickListener {

    @BindView(R.id.iv_share)
    ImageView ivShare;

    @BindView(R.id.back)
    ImageView ivBack;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_nickNama)
    TextView tvNickName;

//    @BindView(R.id.tv_follow)
//    TextView tvFollow;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.web_view)
    WebView webView;

    @BindView(R.id.tv_collection_num)
    TextView tvCollectionNum;

    @BindView(R.id.tv_dz_num)
    TextView tvDzNum;

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    @BindView(R.id.bt_collect)
    ShineButton btCollect;

    @BindView(R.id.bt_dz)
    ShineButton btDz;

    @BindView(R.id.ll_collection)
    LinearLayout llCollection;

    @BindView(R.id.ll_dz)
    LinearLayout llDz;

    @BindView(R.id.iv_head)
    ImageView ivHead;

    private XiangcMo.Article article;

    @Override
    public int setLayout() {
        return R.layout.activity_html_for_story;
    }

    @Override
    public void initView() {
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
                                article = JsonUtil.string2Obj(str, XiangcMo.Article.class);
                                setViewData(article);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spinKitView.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spinKitView.setVisibility(View.GONE);
                            }
                        });
                    }
                });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShare();
            }
        });

    }

    private void showShare() {
        ShareUtils.getInstance(this).startShare("趣味乡村-品牌故事",
                article.getTitle(),
                article.getCoverImgurl(),
                article.getArticleShareUrl(), new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        startSendServer(article.getArticleId(),article.getCategory());
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });

        startSendServer(article.getArticleId(), article.getCategory() == null ? "" : article.getCategory());
    }


    private void setViewData(XiangcMo.Article article) {

        //标题
        tvTitle.setText(TextUtil.checkStr2Str(article.getTitle()));

        //作者
        tvNickName.setText(TextUtil.checkStr2Str(article.getUserNickname()));

        //头像
        Glide.with(this).load(article.getHeadUrl()).into(ivHead);

        tvDate.setText(TextUtil.checkStr2Str(article.getCreateTime()));

        //点赞数量
        tvDzNum.setText(TextUtil.checkStr2Num(article.getGoodNum()));

        //收藏数量
        tvCollectionNum.setText(TextUtil.checkStr2Num(article.getCollectNum()));

        //如果点赞字段值不为null ，则已点赞该文章
        if (!StringUtils.isEmpty(article.getAlreadyGood())) {
            tvDzNum.setTextColor(getResources().getColor(R.color.colorOrange));
            llDz.setBackgroundResource(R.drawable.shape_login_orange_no);
            btDz.setChecked(true);
        }

        //如果收藏字段值不为null ，则已收藏该文章
        if (!StringUtils.isEmpty(article.getAlreadyCollect())) {
            tvCollectionNum.setTextColor(getResources().getColor(R.color.colorOrange));
            llCollection.setBackgroundResource(R.drawable.shape_login_orange_no);
            btCollect.setChecked(true);
        }
        setWebView(webView, spinKitView, article.getContent());

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
        String token = (String) SPUtils.instance(getContext(),1).getkey("token","");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.toOperatArticle, str,token,
                new GsonObjectCallback<String>(Article.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {
//                                if (isClick) {
//                                    ToastUtil.showShort(HtmlForStoryActivity.this, tips + "成功,文章id=" + getIntent().getStringExtra("articleId"));
//                                } else {
//                                    ToastUtil.showShort(HtmlForStoryActivity.this, tips + "取消,文章id=" + getIntent().getStringExtra("articleId"));
//                                }
                            }else {
                                ToastUtil.showShort(HtmlForStoryActivity.this, "请求超时");

                                if (s.equals("0")){
                                    btDz.setChecked(false);
                                }else {
                                    btCollect.setChecked(false);
                                }
                            }
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
//            case R.id.tv_follow:
//
//                break;
            default:
                break;
        }
    }

}
